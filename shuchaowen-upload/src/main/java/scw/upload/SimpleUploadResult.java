package scw.upload;

public class SimpleUploadResult implements UploadResult {
	private static final long serialVersionUID = 1L;
	private String url;
	private long size;
	private String fileName;

	public SimpleUploadResult(String url, String fileName, long size) {
		this.url = url;
		this.size = size;
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public long getSize() {
		return size;
	}

	public String getFileName() {
		return fileName;
	}
}
