package kuchingitsolution.betterpepperboard.helper;

public class Config {

    public static String PREF_NAME = "pepper_board";
    private static String DOMAIN_URL = "http://kuchingitsolution.net/admin_panel/api";
//    private static String DOMAIN_URL = "http://192.168.1.102/ComplaintManagementPanel/admin_panel/api";
    public static String LOGIN_URL = DOMAIN_URL + "/login";
    public static String REGISTER_URL = DOMAIN_URL + "/register";
    public static String HOTLINE_URL = DOMAIN_URL + "/hotline";
    public static String INFO_URL = DOMAIN_URL + "/info";
    public static String DETAIL_URL = DOMAIN_URL + "/get_details_tip";
    public static String URL_ReportType = DOMAIN_URL + "/report_type";
    public static String NEW_COMPLAINT_URL = DOMAIN_URL + "/report_post";
    public static String GET_REPORT = DOMAIN_URL + "/get_report";
    public static String UPDATE_RESPONSE = DOMAIN_URL + "/update_response";
    public static String SINGLE_REPORT = DOMAIN_URL + "/get_single_report";
    public static String URL_AddComment = DOMAIN_URL + "/addComment";
    public static String URL_GetComment = DOMAIN_URL + "/getComment";
    public static String URL_GET_ACTION = DOMAIN_URL + "/getAction";
    public static String URL_GET_OFFICER = DOMAIN_URL + "/free_officer";
    public static String URL_ASSIGN_OFFICER = DOMAIN_URL + "/assign_officer";
    public static String URL_UPDATE_ACTION = DOMAIN_URL + "/add_action";
    public static String URL_GET_DETAIL = DOMAIN_URL + "/get_simple_details";
    public static String URL_GET_OWN_REPORT = DOMAIN_URL + "/get_own_report";
    public static String URL_GET_FOLLOWED_REPORT = DOMAIN_URL + "/get_followed_report";
    public static String URL_GET_NOTI = DOMAIN_URL + "/notification";
    public static String URL_GET_USERLIST = DOMAIN_URL + "/fetchChatRoom";
    public static String URL_FETCH_CHAT_ROOM = DOMAIN_URL + "/fetchSingleChatRoom";
    public static String URL_ADD_MSG = DOMAIN_URL + "/addMessage";
    public static String URL_GET_UNSOLVE = DOMAIN_URL + "/unsolved_complaint";
    public static String URL_GET_SOLVE = DOMAIN_URL + "/solved_complaint";
    public static String URL_QUERY = DOMAIN_URL + "/query";
    public static String URL_SEARCH_USER = DOMAIN_URL + "/searchUser";
    public static String URL_ADD_USER = DOMAIN_URL + "/add_chat_user";
    public static String URL_GET_LOCATION = DOMAIN_URL + "/get_location_report";
    public static String URL_RESET_PASSWORD = DOMAIN_URL + "/password/email";
    public static String URL_CHANGE_NAME = DOMAIN_URL + "/room/change_name";
    public static String URL_DELETE_ROOM = DOMAIN_URL + "/room/delete";
    public static String URL_DELETE_MSG = DOMAIN_URL + "/room/delete_msg";

    //badge position
    public static final String COMPLAINT = "complaint";
    public static final String MESSAGE = "message";
    public static final String NOTI = "notification";

    // KEY THHING
    public static String CHAT_NOTIFICATION = "room_list";
    public static String CHAT_ROOM_MSG = "room_msg";

    public static int ONLINE = 1;
    public static int OFFLINE = 2;

    public static int ENABLE = 1;
    public static int DISABLE = 2;

}
