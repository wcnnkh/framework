package scw.tencent.wx.offiaccount;

/**
 * 请注意，开通语音识别后，用户每次发送语音给公众号时，微信会在推送的语音消息XML数据包中，增加一个Recognition字段（注：由于客户端缓存，开发者开启或者关闭语音识别功能，对新关注者立刻生效，对已关注用户需要24小时生效。开发者可以重新关注此帐号进行测试）
 * @author shuchaowen
 *
 */
public class VoiceMessage extends Message{
	private static final long serialVersionUID = 1L;
	private String mediaId;//语音消息媒体id，可以调用获取临时素材接口拉取数据。
	private String format;//语音格式，如amr，speex等
	private String recognition;//语音识别结果，UTF8编码
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getRecognition() {
		return recognition;
	}
	public void setRecognition(String recognition) {
		this.recognition = recognition;
	}
}
