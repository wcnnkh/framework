package scw.upload;

import java.io.InputStream;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public interface UploadService {
	UploadResult upload(InputStream inputStream, String fileName)
			throws Exception;

	boolean delete(String url);
}
