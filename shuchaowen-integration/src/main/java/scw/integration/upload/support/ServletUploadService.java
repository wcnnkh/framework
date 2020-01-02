package scw.integration.upload.support;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import scw.integration.upload.UploadService;

public interface ServletUploadService extends UploadService {
	Collection<HttpUploadResult> multipleUpload(
			HttpServletRequest httpServletRequest) throws Exception;

	HttpUploadResult upload(HttpServletRequest httpServletRequest)
			throws Exception;
}
