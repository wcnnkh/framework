package scw.upload.support;

import scw.upload.UploadResult;

public interface HttpUploadResult extends UploadResult {
	String getUploadFileName();

	String getKey();
}
