package scw.upload;

import java.io.InputStream;

public interface UploadService {
	UploadResult upload(InputStream inputStream, String fileName)
			throws Exception;

	boolean delete(String url);
}
