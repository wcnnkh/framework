package scw.upload.kind;

import java.io.InputStream;

public interface KindUpload {
	KindUploadResponse kindUpload(String group, KindDirType dir, String fileName, InputStream inputStream);

	KindManagerResult getPagination(String group, KindDirType dir, String path, KindOrderType kindOrderType);
}
