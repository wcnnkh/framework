package io.basc.framework.upload.kind;

import io.basc.framework.net.multipart.MultipartMessage;
import io.basc.framework.upload.UploaderException;

import java.io.IOException;

public interface KindEditor {
	String upload(String group, KindDirType dir, MultipartMessage fileItem) throws UploaderException, IOException;

	KindManagerResult manager(String group, KindDirType dir, String path, KindOrderType orderType);
}
