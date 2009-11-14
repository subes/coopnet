package coopnetclient.utils.ui;

public class StyledChatMessage {

    private String senderName;
    private String style;
    private int contentStartIndex;
    private int contentEndIndex;

    public StyledChatMessage(String senderName, String style, int start, int end){
        this.senderName = senderName;
        this.style = style;
        this.contentStartIndex = start;
        this.contentEndIndex = end;
    }

    /**
     * @return the senderName
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * @param senderName the senderName to set
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * @return the style
     */
    public String getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * @return the contentStartIndex
     */
    public int getContentStartIndex() {
        return contentStartIndex;
    }

    /**
     * @return the contentEndIndex
     */
    public int getContentEndIndex() {
        return contentEndIndex;
    }
}
