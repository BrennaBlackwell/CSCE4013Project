package edu.uark.spARK;

public class ServerUtil {
	
	public static final String TAG_SUCCESS = "success";
	public static final String TAG_CONTENTS = "contents";
	public static final String TAG_COMMENTS = "comments";
	public static final String TAG_GROUPS = "groups";
	public static final String TAG_PRIVACY = "privacy";
	public static final String TAG_VISIBILITY = "visibility";
	public static final String TAG_ID = "id";
	public static final String TAG_TITLE = "title";
	public static final String TAG_BODY = "body";
	public static final String TAG_TYPE = "type";
	public static final String TAG_TIMESTAMP = "timestamp";
	public static final String TAG_USER_ID = "userid";
	public static final String TAG_USER_NAME = "username";
	public static final String TAG_RATING_TOTAL = "rating_total";
	public static final String TAG_RATING_TOTAL_FLAG = "rating_total_flag";
	public static final String TAG_USER_RATING = "user_rating";
	public static final String TAG_USER_RATING_FLAG = "user_rating_flag";
	
	public static final String SERVER_ADDR = "http://csce.uark.edu/~mmmcclel/spark/";
	
	public static final String URL_GET_MY_CONTENT = SERVER_ADDR + "mycontent.php";
	
	public static final String URL_AUTHENTICATE = SERVER_ADDR + "authentication.php";
	
	public static final String URL_REGISTER = SERVER_ADDR + "register.php";
	
	public static final String URL_VERIFY = SERVER_ADDR + "verify.php";
	
	public static final String URL_LOAD_ALL_POSTS = SERVER_ADDR + "loadnewsfeed.php";
	
	public static final String URL_POST_COMMENT = SERVER_ADDR + "postcomment.php";
	
	public static final String URL_LIKE_DISLIKE = SERVER_ADDR + "rating.php";
	
	public static final String URL_CREATE_CONTENT = SERVER_ADDR + "createcontent.php";

 
}
