package scw.tencent.wx.offiaccount.message.reply;


public class TextReplyMessage extends ReplyMessage{
	private static final long serialVersionUID = 1L;
	private String content;
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
}
