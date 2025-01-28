package com.dream.six.constants;

public final class ErrorMessageConstants {


    private ErrorMessageConstants() {
        throw new AssertionError("Utility class ErrorMessageConstants cannot be instantiated");
    }

    public static final String CAMPAIGN_NAME_EXISTS = "Campaign with this name and client already exists";
    public static final String CAMPAIGN_NOT_FOUND = "Campaign is not found for ID: ";
    public static final String CAMPAIGN_FILE_NOT_FOUND = "Campaign file not found with ID: ";
    public static final String CAMPAIGN_PATH_NOT_FOUND = "Campaign file not found or not active at the specified path:";

    public static final String USER_NOT_FOUND = "User Info is not found";
    public static final String USER_NOT_CREDENTIALS_FOUND = "User Credentials is not found";

    public static final String PERMISSION_NOT_FOUND = "Permission  is not found";
    public static final String DUPLICATE_PERMISSION_FOUND = "Duplicate permission found. This permission already exists.";
    public static final String ROLE_NOT_FOUND = "Role is not found";

    public static final String RESOURCE_WITH_ID_NOT_FOUND = "%s with ID: %s";

    public static final String SUPER_ADMIN_CHECK = "You can not update/delete super admin email";

    public static final String LINE_ITEM_NAME_EXISTS = "LineItem with this name and Campaign already exists";
    public static final String LINE_ITEM_NOT_FOUND = "LineItem is not found for ID: ";

    public static final String CLIENT_NOT_FOUND = "Client is not found";

    public static final String USER_EMAIL_EXISTS = "User Email is already exists";
    public static final String USER_NAME_EXISTS = "User Name is already exists";

    public static final String ROLE_NAME_EXISTS = "Role name is already exists";

    public static final String CLIENT_NAME_EXISTS = "Client name is already exists";
    public static final String CLIENT_ID_EXISTS = "Client Id is already exists";

    public static final String INVALID_OR_EXPIRED_TOKEN = "Invalid or expired token";

    public static final  String INVALID_COLUMN_IN_ICP = "No matching column name found in the request.";

    public static final String SUPPRESSION_NOT_FOUND = "Suppression detail is not found";

    public static  final String CLIENT_ALREADY_ASSIGN ="client already assigned to this user";

    public static  final String CAMPAIGN_ALREADY_ASSIGN ="campaign already assigned to this user";

    public static  final String LINE_ITEM_ALREADY_ASSIGN ="lineItem already assigned to this user";

    public static  final String CAMPAIGN_CALENDAR_NOTE_NOT_FOUND ="No campaign calendar notes found with ID:";


    public static final String INVALID_CURRENT_PASSWORD = "The current password provided is incorrect. Please try again.";

    public static final String USER_ALREADY_ASSIGN_TO_LEAD_PUBLISH_FILE ="User is already assigned to this lead publish file";

}
