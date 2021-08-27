package io.basc.framework.upload.ueditor.upload;

import io.basc.framework.codec.support.Base64;
import io.basc.framework.upload.ueditor.PathFormat;
import io.basc.framework.upload.ueditor.define.AppInfo;
import io.basc.framework.upload.ueditor.define.BaseState;
import io.basc.framework.upload.ueditor.define.FileType;
import io.basc.framework.upload.ueditor.define.State;

import java.util.Map;

public final class Base64Uploader {

	public static State save(String content, Map<String, Object> conf) {
		
		byte[] data = decode(content);

		long maxSize = ((Long) conf.get("maxSize")).longValue();

		if (!validSize(data, maxSize)) {
			return new BaseState(false, AppInfo.MAX_SIZE);
		}

		String suffix = FileType.getSuffix("JPG");

		String savePath = PathFormat.parse((String) conf.get("savePath"),
				(String) conf.get("filename"));
		
		savePath = savePath + suffix;
		String physicalPath = (String) conf.get("rootPath") + savePath;

		State storageState = StorageManager.saveBinaryFile(data, physicalPath);

		if (storageState.isSuccess()) {
			storageState.putInfo("url", PathFormat.format(savePath));
			storageState.putInfo("type", suffix);
			storageState.putInfo("original", "");
		}

		return storageState;
	}

	private static byte[] decode(String content) {
		return Base64.DEFAULT.decode(content);
	}

	private static boolean validSize(byte[] data, long length) {
		return data.length <= length;
	}
	
}