package mobile.security.constants;

public final class AppAuthorities {
    private AppAuthorities() {}

    // Resource Names
    public static final String RES__CARD = "CARD";
    public static final String RES__DECK = "DECK";
    public static final String RES__PENDING_ITEM = "PENDING_ITEM";
    public static final String RES__COMIC = "COMIC";
    public static final String RES__USER = "USER";

    // SpEL Expressions for @PreAuthorize
    public static final String IS_AUTHENTICATED = "isAuthenticated()";
    public static final String HAS_AUTHENTICATED = "isAuthenticated()";
    public static final String HAS_ROLE_ADMIN = "hasRole('ADMIN')";
    public static final String HAS_ROLE_USER = "hasRole('USER')";
    public static final String HAS_ROLE_TRANSLATOR = "hasRole('TRANSLATOR')";

    public static final String HAS_CARD_READ = "isAuthenticated()";
    public static final String HAS_CARD_WRITE = "isAuthenticated()";
    public static final String HAS_DECK_MANAGE = "isAuthenticated()";
    public static final String HAS_PENDING_ITEM_MANAGE = "isAuthenticated()";
    public static final String HAS_USER_READ = "isAuthenticated()";
    public static final String HAS_USER_WRITE = "isAuthenticated()";
    public static final String HAS_ADMIN = "hasRole('ADMIN')";
}
