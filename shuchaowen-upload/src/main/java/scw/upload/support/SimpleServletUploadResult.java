package scw.upload.support;

import scw.upload.SimpleUploadResult;
import scw.upload.UploadResult;

public class SimpleServletUploadResult extends SimpleUploadResult implements HttpUploadResult {
	private static final long serialVersionUID = 1L;
	private String uploadFileName;
	private String key;

	public SimpleServletUploadResult(UploadResult uploadResult, String uploadFileName, String key) {
		super(uploadResult.getUrl(), uploadResult.getFileName(), uploadResult.getSize());
		this.uploadFileName = uploadFileName;
		this.key = key;
	}

	public SimpleServletUploadResult(String url, String fileName, long size, String uploadFileName, String key) {
		super(url, fileName, size);
		this.uploadFileName = uploadFileName;
		this.key = key;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public String getKey() {
		return key;
	}

}
