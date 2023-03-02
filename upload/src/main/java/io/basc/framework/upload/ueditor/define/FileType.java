package io.basc.framework.upload.ueditor.define;

import java.util.HashMap;
import java.util.Map;

public class FileType {

	public static final String JPG = "JPG";

	@SuppressWarnings("serial")
	private static final Map<String, String> types = new HashMap<String, String>() {
		{

			put(FileType.JPG, ".jpg");

		}
	};

	public static String getSuffix(String key) {
		return FileType.types.get(key);
	}

	public static String getSuffixByFilename(String filename) {

		return filename.substring(filename.lastIndexOf(".")).toLowerCase();

	}

}
