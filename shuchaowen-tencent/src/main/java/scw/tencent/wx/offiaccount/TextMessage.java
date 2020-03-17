package scw.tencent.wx.offiaccount;

public class TextMessage extends Message{
	private static final long serialVersionUID = 1L;
	private String content;//文本消息内容
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
