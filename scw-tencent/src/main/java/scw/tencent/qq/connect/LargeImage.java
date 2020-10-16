package scw.tencent.qq.connect;

import java.io.Serializable;

public class LargeImage implements Serializable {
	private static final long serialVersionUID = 1L;
	private int height;
	private int width;
	private String url;

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
