package scw.upload.kind;

import java.io.InputStream;

public interface KindUpload {
	KindUploadResponse kindUpload(String group, KindDirType dir, String fileName, InputStream body);

	KindManagerResult getPagination(String group, KindDirType dir, String path, KindOrderType kindOrderType);
}
