package scw.tencent.wx.offiaccount.message.reply;

import java.io.Serializable;


public class MusicReplyMessage extends ReplyMessage{
	private static final long serialVersionUID = 1L;
	private Music music;
	
	public Music getMusic() {
		return music;
	}

	public void setMusic(Music music) {
		this.music = music;
	}

	public static class Music implements Serializable{
		private static final long serialVersionUID = 1L;
		private String title;
		private String description;
		private String musicUrl;
		private String hqMusicUrl;
		private String thumbMediaId;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getMusicUrl() {
			return musicUrl;
		}
		public void setMusicUrl(String musicUrl) {
			this.musicUrl = musicUrl;
		}
		public String getHqMusicUrl() {
			return hqMusicUrl;
		}
		public void setHqMusicUrl(String hqMusicUrl) {
			this.hqMusicUrl = hqMusicUrl;
		}
		public String getThumbMediaId() {
			return thumbMediaId;
		}
		public void setThumbMediaId(String thumbMediaId) {
			this.thumbMediaId = thumbMediaId;
		}
	}
}
