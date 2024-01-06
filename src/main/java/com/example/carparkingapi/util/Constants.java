package com.example.carparkingapi.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access =  AccessLevel.PRIVATE)
public class Constants {

    public static final String CUSTOMER_URL = "/api/v1/customer/**";

    public static final String ADMIN_URL = "/api/v1/admin/**";

    public static final String AUTH_URL = "/api/v1/auth/**";

    public static final String USERNAME = "username";

    public static final String USER = "USER";

    public static final String ADMIN = "ADMIN";
}
