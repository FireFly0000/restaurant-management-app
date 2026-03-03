package com.restaurant.commons.constant;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Constant {
    /* REQUEST HEADER / gRPC METADATA */
    public static final String H_USER_ID = "X-User-Id";
    public static final String H_FORWARDED_FOR = "X-Forwarded-For";
    /* COMMON */
    public static final String DEFAULT_ZONE = "Asia/Ho_Chi_Minh";
    public static final String USER_ID = "USER_ID";
    public static final String EXPIRATION = "exp";
    public static final String API_V1_PREFIX = "/api/v1";
    public static final String API_V2_PREFIX = "/api/v2";
    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_PUT = "PUT";
    public static final String HTTP_DELETE = "DELETE";
    public static final String HTTP_PATCH = "PATCH";
    public static final String VN_LANGUAGE = "vi";
    public static final String VN_COUNTRY = "VN";
    public static final String UTF_8 = "UTF-8";
    public static final String SOURCE_MESSAGES_CLASSPATH = "classpath:i18n/messages";
    public static final String SOURCE_VALIDATIONS_CLASSPATH = "classpath:i18n/validation";
    public static final String DLT = ".DLT";
    /* PUBLIC API */
    public static final List<Pair<String, String>> PUBLIC_ENDPOINT = List.of(
            Pair.of(String.format("%s/auth/sign-in", API_V1_PREFIX), HTTP_POST),
            Pair.of(String.format("%s/auth/sign-up", API_V1_PREFIX), HTTP_POST)
    );

    /* ERROR CODE */
    // 0xxx: SYSTEM / SERVER ERROR
    public static final String RES0001 = "RES0001"; // Internal Server Error
    public static final String RES0002 = "RES0002"; // Database Error (SQL, Connection...)
    public static final String RES0003 = "RES0003"; // Third-party Service Error
    public static final String RES0004 = "RES0004"; // Timeout Error (Gateway/DB timeout)
    public static final String RES0005 = "RES0005"; // JSON Processing/Parsing Error
    public static final String RES0006 = "RES0006"; // Email Sender Error
    public static final String RES0007 = "RES0007"; //Downstream service error (RPC)

    // 1xxx: INPUT VALIDATION
    public static final String RES1001 = "RES1001"; // Parameter Invalid
    public static final String RES1002 = "RES1002"; // Parameter Missing
    public static final String RES1003 = "RES1003"; // Type Mismatch
    public static final String RES1004 = "RES1004"; // File Upload Error

    // 2xxx: AUTHENTICATION & AUTHORIZATION
    public static final String RES2001 = "RES2001"; // Unauthorized
    public static final String RES2002 = "RES2002"; // Forbidden
    public static final String RES2003 = "RES2003"; // Token Expired
    public static final String RES2004 = "RES2004"; // Account Locked/Disabled

    // 3xxx: BUSINESS LOGIC
    public static final String RES3001 = "RES3001"; // Resource Not Found
    public static final String RES3002 = "RES3002"; // Resource Already Exists
    public static final String RES3003 = "RES3003"; // Action Isn't Allowed
    public static final String RES3004 = "RES3004"; // The Payload of notification is invalid
    public static final String RES3005 = "RES3005"; // Confirm password and password do not match
    public static final String RES3006 = "RES3006"; // Email already exists
    public static final String RES3007 = "RES3007"; // Phone number already exists
    public static final String RES3008 = "RES3008"; // Failed Creating new user

    /* TASK */

}
