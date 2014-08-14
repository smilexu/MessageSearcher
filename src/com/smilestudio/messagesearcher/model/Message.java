package com.smilestudio.messagesearcher.model;

public class Message {

    private String mMsgId;
    private String mContent;
    private String mContentThumb;
    private String mAuthor;
    private String mAuthorThumb;
    private Message mRepostMsg;
    private String mContentPic;
    private String mTime;

    /**
     * Message construction
     * @param msg_id: id from server
     * @param content: text message
     * @param contentThumb: URL for posted picture thumbnail
     * @param time post time
     * @param contentPic: URL for posted picture
     * @param author: the author
     * @param authorThumb: URL for author thunbmail 
     */
    public Message(String msg_id, String content, String time, String contentThumb, String contentPic, String author, String authorThumb) {
        mMsgId = msg_id;
        mContent = content;
        mContentThumb = contentThumb;
        mContentPic = contentPic;
        mAuthor = author;
        mAuthorThumb = authorThumb;
        mTime = time;
    }

    public void setRepostMessage(Message msg) {
        mRepostMsg = msg;
    }

    public String getContent() {
        return mContent;
    }

    public String getContentThumb() {
        return mContentThumb;
    }

    public String getContentPic() {
        return mContentPic;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getAuthorThumb() {
        return mAuthorThumb;
    }

    public Message getRepost() {
        return mRepostMsg;
    }

    public String getId() {
        return mMsgId;
    }

    public String getTime() {
        return mTime;
    }
}
