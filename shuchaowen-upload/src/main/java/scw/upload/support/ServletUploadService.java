package scw.upload.support;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import scw.upload.UploadService;

public interface ServletUploadService extends UploadService {
	Collection<HttpUploadResult> multipleUpload(
			HttpServletRequest httpServletRequest) throws Exception;

	HttpUploadResult upload(HttpServletRequest httpServletRequest)
			throws Exception;
}
