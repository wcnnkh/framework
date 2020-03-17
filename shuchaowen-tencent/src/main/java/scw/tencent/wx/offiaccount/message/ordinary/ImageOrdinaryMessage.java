package scw.tencent.wx.offiaccount.message.ordinary;


public class ImageOrdinaryMessage extends OrdinaryMessage{
	private static final long serialVersionUID = 1L;
	private String picUrl;//图片链接（由系统生成）
	private String mediaId;//图片消息媒体id，可以调用获取临时素材接口拉取数据。
	
	public String getPicUrl() {
		return picUrl;
	}
	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}
	public String getMediaId() {
		return mediaId;
	}
	
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
}
