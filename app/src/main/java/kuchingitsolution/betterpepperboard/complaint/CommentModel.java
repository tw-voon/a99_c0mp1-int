package kuchingitsolution.betterpepperboard.complaint;

public class CommentModel {

    private String username, commentmsg, timeago, image_link;
    private int userID, msgID;

    CommentModel(String username, String message, String timeago, int userID, int msgID, String avatar_link){
        this.userID = userID;
        this.username = username;
        this.msgID = msgID;
        this.commentmsg = message;
        this.timeago = timeago;
        this.image_link = avatar_link;
    }

    CommentModel(){}

    public void setUsername(String username){ this.username = username; }

    public void setTimeago(String timeago) { this.timeago = timeago; }

    public void setCommentmsg(String message){ this.commentmsg = message; }

    public void setUserID(int userID){ this.userID = userID; }

    public void setMsgID(int msgID){ this.msgID = msgID; }

    public String getUsername(){ return username; }

    public String getCommentmsg(){ return commentmsg; }

    public String getTimeago() { return timeago; }

    public int getUserID(){ return userID; }

    public String getImage_link(){return image_link;}

    public int getMsgID(){ return msgID; }
}
