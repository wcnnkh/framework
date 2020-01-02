package scw.integration.upload.support;

import scw.integration.upload.UploadResult;

public interface HttpUploadResult extends UploadResult {
	String getUploadFileName();

	String getKey();
}
