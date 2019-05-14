package cz.muni.fi.gag.web.entity;

/**
 * @author Vojtech Prusa
 *
 */
public enum UserRole {
    ANONYMOUS, USER, ADMIN;

    public static final String ADMIN_R = "ADMIN";
    public static final String USER_R = "USER";
    public static final String ANONYMOUS_R = "ANONYMOUS";
}