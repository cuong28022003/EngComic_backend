/*
 * EngComic: migrate MongoDB users into Keycloak.
 *
 * BCrypt hashes from Mongo cannot be imported into Keycloak, so every newly
 * created account receives a temporary password and the UPDATE_PASSWORD
 * required action. Existing Keycloak accounts are left untouched except for
 * missing realm roles.
 *
 * Usage (Node 18+):
 *   cd EngComic_backend/scripts
 *   npm install
 *   node migrate-users-to-keycloak.js
 *
 * Configuration (environment variables):
 *   MONGO_URI              Mongo connection string
 *   MONGO_DB               Database name (default: eng_comic)
 *   KEYCLOAK_URL           Keycloak base URL (default: http://localhost:8082)
 *   KEYCLOAK_REALM         Realm (default: myapp)
 *   KEYCLOAK_ADMIN_USER    Admin username (default: admin)
 *   KEYCLOAK_ADMIN_PASSWORD Admin password (default: admin)
 *   TEMP_PASSWORD          Temporary password for new accounts (default: EngComic@123)
 *   DRY_RUN                Set to "true" to preview without writing anything
 */

const { MongoClient, ObjectId } = require('mongodb');

const config = {
  mongoUri: process.env.MONGO_URI || 'mongodb://localhost:27017/eng_comic',
  mongoDb: process.env.MONGO_DB || 'eng_comic',
  keycloakUrl: (process.env.KEYCLOAK_URL || 'http://localhost:8082').replace(/\/+$/, ''),
  realm: process.env.KEYCLOAK_REALM || 'myapp',
  adminUser: process.env.KEYCLOAK_ADMIN_USER || 'admin',
  adminPassword: process.env.KEYCLOAK_ADMIN_PASSWORD || 'admin',
  tempPassword: process.env.TEMP_PASSWORD || 'EngComic@123',
  dryRun: String(process.env.DRY_RUN || '').toLowerCase() === 'true',
};

const summary = { scanned: 0, created: 0, linked: 0, skipped: 0, failed: 0, passwordsRemoved: 0 };

const toObjectId = (value) => {
  if (value instanceof ObjectId) return value;
  if (typeof value === 'string' && /^[0-9a-fA-F]{24}$/.test(value)) return new ObjectId(value);
  return null;
};

const roleRefId = (ref) => {
  if (!ref) return null;
  if (typeof ref === 'string') return ref;
  return ref.$id || ref.id || null;
};

async function adminToken() {
  const body = new URLSearchParams({
    grant_type: 'password',
    client_id: 'admin-cli',
    username: config.adminUser,
    password: config.adminPassword,
  });
  const res = await fetch(
    `${config.keycloakUrl}/realms/master/protocol/openid-connect/token`,
    {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body,
    }
  );
  if (!res.ok) {
    throw new Error(`Keycloak admin login failed (${res.status}): ${await res.text()}`);
  }
  const json = await res.json();
  return json.access_token;
}

function adminApi(token, path, init = {}) {
  return fetch(`${config.keycloakUrl}/admin/realms/${config.realm}${path}`, {
    ...init,
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
      ...(init.headers || {}),
    },
  });
}

async function fetchRealmRoles(token) {
  const res = await adminApi(token, '/roles');
  if (!res.ok) {
    throw new Error(`Cannot list realm roles (${res.status}): ${await res.text()}`);
  }
  const roles = await res.json();
  return new Map(roles.map((role) => [role.name.toUpperCase(), role]));
}

async function findKeycloakUser(token, { username, email }) {
  if (username) {
    const res = await adminApi(token, `/users?username=${encodeURIComponent(username)}&exact=true`);
    if (res.ok) {
      const users = await res.json();
      const match = users.find((u) => u.username && u.username.toLowerCase() === username.toLowerCase());
      if (match) return match;
    }
  }
  if (email) {
    const res = await adminApi(token, `/users?email=${encodeURIComponent(email)}&exact=true`);
    if (res.ok) {
      const users = await res.json();
      if (users.length > 0) return users[0];
    }
  }
  return null;
}

async function createKeycloakUser(token, { username, email, fullName, active, mongoId }) {
  const payload = {
    username,
    enabled: active !== false,
    emailVerified: true,
    attributes: { mongoId: [String(mongoId)] },
  };
  if (email) payload.email = email;
  if (fullName) {
    payload.firstName = fullName;
    payload.lastName = '';
  }

  const res = await adminApi(token, '/users', {
    method: 'POST',
    body: JSON.stringify(payload),
  });

  if (res.status === 201) {
    const location = res.headers.get('location') || '';
    const id = location.split('/').pop();
    if (id) return { id };
  }

  if (res.status === 409) {
    return null;
  }

  throw new Error(`Create user failed (${res.status}): ${await res.text()}`);
}

async function setTemporaryPassword(token, userId) {
  const res = await adminApi(token, `/users/${userId}/reset-password`, {
    method: 'PUT',
    body: JSON.stringify({
      type: 'password',
      value: config.tempPassword,
      temporary: true,
    }),
  });
  if (!res.ok && res.status !== 204) {
    throw new Error(`Reset password failed (${res.status}): ${await res.text()}`);
  }
}

async function ensureRealmRoles(token, userId, roles) {
  if (roles.length === 0) return;
  const currentRes = await adminApi(token, `/users/${userId}/role-mappings/realm`);
  const current = currentRes.ok ? await currentRes.json() : [];
  const currentIds = new Set(current.map((r) => r.id));
  const missing = roles.filter((r) => !currentIds.has(r.id));
  if (missing.length === 0) return;

  const res = await adminApi(token, `/users/${userId}/role-mappings/realm`, {
    method: 'POST',
    body: JSON.stringify(missing.map((r) => ({ id: r.id, name: r.name }))),
  });
  if (!res.ok && res.status !== 204) {
    throw new Error(`Assign roles failed (${res.status}): ${await res.text()}`);
  }
}

async function resolveMongoRoles(db, user) {
  const rawRefs = Array.isArray(user.roles) ? user.roles : [];
  const ids = rawRefs.map(roleRefId).map(toObjectId).filter(Boolean);
  if (ids.length === 0) return [];
  const docs = await db.collection('role').find({ _id: { $in: ids } }).toArray();
  return docs.map((doc) => String(doc.name || '').toUpperCase()).filter(Boolean);
}

async function main() {
  const mongoClient = new MongoClient(config.mongoUri);
  await mongoClient.connect();
  const db = mongoClient.db(config.mongoDb);

  const token = await adminToken();
  const realmRoles = await fetchRealmRoles(token);
  const defaultRole = realmRoles.get('USER');

  const users = await db.collection('user').find({}).toArray();
  summary.scanned = users.length;

  for (const user of users) {
    const username = (user.username || '').trim();
    if (!username) {
      summary.skipped += 1;
      console.warn(`[skip] ${user._id}: missing username`);
      continue;
    }

    try {
      const email = (user.email || '').trim() || undefined;
      const fullName = (user.fullName || '').trim() || username;

      let kcUser = user.keycloakId
        ? (await adminApi(token, `/users/${user.keycloakId}`).then(async (r) => (r.ok ? r.json() : null)))
        : null;
      kcUser = kcUser || (await findKeycloakUser(token, { username, email }));

      const roleNames = await resolveMongoRoles(db, user);
      const desiredRoles = roleNames
        .map((name) => realmRoles.get(name))
        .filter(Boolean);
      if (desiredRoles.length === 0 && defaultRole) desiredRoles.push(defaultRole);

      if (kcUser) {
        summary.linked += 1;
        if (!config.dryRun) {
          await ensureRealmRoles(token, kcUser.id, desiredRoles);
          if (String(user.keycloakId || '') !== kcUser.id) {
            await db.collection('user').updateOne(
              { _id: user._id },
              { $set: { keycloakId: kcUser.id } }
            );
          }
        }
        console.log(`[exists] ${username} -> ${kcUser.id}`);
        continue;
      }

      if (config.dryRun) {
        console.log(`[dry-run] would create ${username} (${email || 'no email'}) roles=${desiredRoles.map((r) => r.name).join(',') || 'none'}`);
        summary.created += 1;
        continue;
      }

      const created = await createKeycloakUser(token, {
        username,
        email,
        fullName,
        active: user.active,
        mongoId: user._id,
      });

      if (!created) {
        const existing = await findKeycloakUser(token, { username, email });
        if (!existing) {
          throw new Error('conflict but user cannot be found (duplicate email?)');
        }
        kcUser = existing;
        summary.linked += 1;
        await ensureRealmRoles(token, kcUser.id, desiredRoles);
        await db.collection('user').updateOne(
          { _id: user._id },
          { $set: { keycloakId: kcUser.id } }
        );
        console.log(`[linked] ${username} -> ${kcUser.id}`);
        continue;
      }

      await setTemporaryPassword(token, created.id);
      await ensureRealmRoles(token, created.id, desiredRoles);
      await db.collection('user').updateOne(
        { _id: user._id },
        { $set: { keycloakId: created.id } }
      );

      summary.created += 1;
      console.log(`[created] ${username} -> ${created.id}`);
    } catch (error) {
      summary.failed += 1;
      console.error(`[error] ${username}: ${error.message}`);
    }
  }

  if (!config.dryRun) {
    const cleanup = await db.collection('user').updateMany(
      { password: { $exists: true } },
      { $unset: { password: '' } }
    );
    summary.passwordsRemoved = cleanup.modifiedCount;
  }

  await mongoClient.close();

  console.log('\nMigration summary');
  console.log(`  scanned: ${summary.scanned}`);
  console.log(`  created: ${summary.created}`);
  console.log(`  linked:  ${summary.linked}`);
  console.log(`  skipped: ${summary.skipped}`);
  console.log(`  failed:  ${summary.failed}`);
  console.log(`  password fields removed: ${summary.passwordsRemoved}`);
  if (config.dryRun) {
    console.log('  (dry run: no changes were written)');
  }
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
