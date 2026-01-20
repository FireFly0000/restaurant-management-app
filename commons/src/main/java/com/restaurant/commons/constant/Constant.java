package com.restaurant.commons.constant;

public class Constant {
    /* COMMON */
    public static final String DEFAULT_ZONE = "Asia/Ho_Chi_Minh";

    /* ERROR CODE */
    // 0xxx: SYSTEM / SERVER ERROR
    public static final String RES0001 = "RES0001"; // Internal Server Error
    public static final String RES0002 = "RES0002"; // Database Error (SQL, Connection...)
    public static final String RES0003 = "RES0003"; // Third-party Service Error
    public static final String RES0004 = "RES0004"; // Timeout Error (Gateway/DB timeout)
    public static final String RES0005 = "RES0005"; // JSON Processing/Parsing Error

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
    public static final String RES3004 = "RES3003"; // Action Isn't Allowed

    /* TASK */
    
}
