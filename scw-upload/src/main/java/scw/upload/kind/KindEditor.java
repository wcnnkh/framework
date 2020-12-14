package scw.upload.kind;

import java.io.IOException;

import scw.upload.UploadException;
import scw.upload.UploadItem;

public interface KindEditor {
	/**
	 * 上传成功后返回url
	 * @param group
	 * @param dir
	 * @param uploadItem
	 * @return
	 * @throws UploadException
	 * @throws IOException
	 */
	String upload(String group, KindDirType dir, UploadItem uploadItem) throws UploadException, IOException;

	KindManagerResult manager(String group, KindDirType dir, String path, KindOrderType orderType);
}
