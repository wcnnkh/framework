package scw.tencent.wx.offiaccount.message.ordinary;


public class TextOrdinaryMessage extends OrdinaryMessage{
	private static final long serialVersionUID = 1L;
	private String content;//文本消息内容
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
