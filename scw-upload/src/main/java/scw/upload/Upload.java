package scw.upload;

import java.io.IOException;

public interface Upload {
	/**
	 * 上传文件并返回可访问路径
	 * @param uploadItem
	 * @return
	 * @throws IOException
	 * @throws UploadException
	 */
	String upload(UploadItem uploadItem) throws IOException, UploadException;
}
