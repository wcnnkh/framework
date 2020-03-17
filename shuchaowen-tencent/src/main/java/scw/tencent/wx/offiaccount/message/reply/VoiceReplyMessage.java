package scw.tencent.wx.offiaccount.message.reply;

import java.io.Serializable;

public class VoiceReplyMessage extends ReplyMessage{
	private static final long serialVersionUID = 1L;
	private Voice voice;
	
	public Voice getVoice() {
		return voice;
	}

	public void setVoice(Voice voice) {
		this.voice = voice;
	}

	public static class Voice implements Serializable{
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
