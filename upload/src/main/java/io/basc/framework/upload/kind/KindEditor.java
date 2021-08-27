package io.basc.framework.upload.kind;

import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.upload.UploaderException;

import java.io.IOException;

public interface KindEditor {
	/**
	 * 上传成功后返回url
	 * @param group
	 * @param dir
	 * @param fileItem
	 * @return
	 * @throws UploadException
	 * @throws IOException
	 */
	String upload(String group, KindDirType dir, MultipartMessage fileItem) throws UploaderException, IOException;

	KindManagerResult manager(String group, KindDirType dir, String path, KindOrderType orderType);
}
