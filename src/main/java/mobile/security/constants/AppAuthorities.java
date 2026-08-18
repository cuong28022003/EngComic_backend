package mobile.security.constants;

public final class AppAuthorities {
    private AppAuthorities() {}

    // Resource Names
    public static final String RES__CARD = "CARD";
    public static final String RES__DECK = "DECK";
    public static final String RES__PENDING_ITEM = "PENDING_ITEM";
    public static final String RES__COMIC = "COMIC";

    // Permission Names
    public static final String PERM__READ__CARD = "PERM__READ__CARD";
    public static final String PERM__WRITE__CARD = "PERM__WRITE__CARD";
    public static final String PERM__MANAGE__DECK = "PERM__MANAGE__DECK";
    public static final String PERM__MANAGE__PENDING_ITEM = "PERM__MANAGE__PENDING_ITEM";

    // SpEL Expressions for @PreAuthorize
    public static final String HAS_AUTHENTICATED = "isAuthenticated()";
    public static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";
    public static final String HAS_ROLE_USER = "hasRole('USER')";
    public static final String HAS_ROLE_TRANSLATOR = "hasRole('TRANSLATOR')";

    public static final String HAS_CARD_READ = "isAuthenticated()";
    public static final String HAS_CARD_WRITE = "isAuthenticated()";
    public static final String HAS_DECK_MANAGE = "isAuthenticated()";
    public static final String HAS_PENDING_ITEM_MANAGE = "isAuthenticated()";
}
