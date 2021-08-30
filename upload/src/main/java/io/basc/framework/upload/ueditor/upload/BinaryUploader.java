package io.basc.framework.upload.ueditor.upload;

import io.basc.framework.net.message.multipart.MultipartMessage;
import io.basc.framework.upload.ueditor.PathFormat;
import io.basc.framework.upload.ueditor.define.AppInfo;
import io.basc.framework.upload.ueditor.define.BaseState;
import io.basc.framework.upload.ueditor.define.FileType;
import io.basc.framework.upload.ueditor.define.State;
import io.basc.framework.web.MultiPartServerHttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BinaryUploader {

	public static final State save(MultiPartServerHttpRequest request,
			Map<String, Object> conf) {
		MultipartMessage fileItem = request.getFirstFile();
		if (fileItem == null) {
			return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
		}
		
		try {
			String savePath = (String) conf.get("savePath");
			String originFileName = fileItem.getOriginalFilename();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0,
					originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);

			String physicalPath = (String) conf.get("rootPath") + savePath;

			InputStream is = fileItem.getInputStream();
			State storageState = StorageManager.saveFileByInputStream(is,
					physicalPath, maxSize);
			is.close();

			if (storageState.isSuccess()) {
				storageState.putInfo("url", PathFormat.format(savePath));
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}
