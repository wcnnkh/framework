package scw.tencent.wx.offiaccount.message.reply;

import java.io.Serializable;

public class ImageReplyMessage extends ReplyMessage{
	private static final long serialVersionUID = 1L;
	private Image image;
	
	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public static class Image implements Serializable{
		private static final long serialVersionUID = 1L;
		private String mediaId;
		public String getMediaId() {
			return mediaId;
		}
		public void setMediaId(String mediaId) {
			this.mediaId = mediaId;
		}
	}
}
