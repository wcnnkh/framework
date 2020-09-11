package scw.upload.kind;

import java.io.Serializable;

public class KindUploadResponse implements Serializable {
	private static final long serialVersionUID = 1L;
	private int error;
	private String url;

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
