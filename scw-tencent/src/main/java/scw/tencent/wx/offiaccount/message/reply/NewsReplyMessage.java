package scw.tencent.wx.offiaccount.message.reply;

import java.io.Serializable;
import java.util.List;

public class NewsReplyMessage extends ReplyMessage{
	private static final long serialVersionUID = 1L;
	private int articleCount;
	private List<Article> articles;
	
	public int getArticleCount() {
		return articleCount;
	}

	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}

	public List<Article> getArticles() {
		return articles;
	}

	public void setArticles(List<Article> articles) {
		this.articles = articles;
	}

	public static class Article implements Serializable{
		private static final long serialVersionUID = 1L;
		private String title;
		private String description;
		private String picUrl;
		private String url;
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
		public String getPicUrl() {
			return picUrl;
		}
		public void setPicUrl(String picUrl) {
			this.picUrl = picUrl;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
	}
}
