package scw.upload.ueditor.upload;

import java.util.Map;

import scw.codec.support.Base64;
import scw.upload.ueditor.PathFormat;
import scw.upload.ueditor.define.AppInfo;
import scw.upload.ueditor.define.BaseState;
import scw.upload.ueditor.define.FileType;
import scw.upload.ueditor.define.State;

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