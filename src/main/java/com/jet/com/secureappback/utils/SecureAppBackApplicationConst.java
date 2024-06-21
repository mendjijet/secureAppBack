package com.jet.com.secureappback.utils;

/**
 * @project secureCapita
 * @version 1.0
 * @author MENDJIJET
 * @since 15/06/2024
 */
public class SecureAppBackApplicationConst {
    private SecureAppBackApplicationConst() {
    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String[] PUBLIC_ROUTES = {
            "/user/new/password",
            "/user/login",
            "/user/verify/code",
            "/user/register",
            "/user/refresh/token",
            "/user/image"
    };
    public static final String HTTP_OPTIONS_METHOD = "OPTIONS";

    public static final String[] PUBLIC_URLS = {
            "/user/verify/password/**",
            "/user/login/**",
            "/user/verify/code/**",
            "/user/register/**",
            "/user/resetpassword/**",
            "/user/verify/account/**",
            "/user/refresh/token/**",
            "/user/image/**",
            "/user/new/password/**"
    };
    public static final String AUTHORITIES = "authorities";
    public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
    public static final String JET_ENTERPRISE_LLC = "JET_ENTERPRISE_LLC";
    public static final String CUSTOMER_MANAGEMENT_SERVICE = "CUSTOMER_MANAGEMENT_SERVICE";

    public static final String USER_AGENT_HEADER = "user-agent";
    public static final String X_FORWARDED_FOR_HEADER = "X-FORWARDED-FOR";

    public static final String USERS_COLUMN_ID = "ID";

}
