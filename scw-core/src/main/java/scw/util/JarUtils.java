package scw.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import scw.core.Constants;
import scw.core.utils.StringUtils;
import scw.io.IOUtils;

public abstract class JarUtils {
	private static final String MANIFEST = "META-INF/MANIFEST.MF";
	private static final String MANIFEST_CLASS_PATH_NAME = "Class-Path";

	@SuppressWarnings("unchecked")
	public static Map<String, String> formatManifestFile(JarFile jarFile) {
		JarEntry jarEntry = jarFile.getJarEntry(MANIFEST);
		if (jarEntry == null) {
			return Collections.EMPTY_MAP;
		}

		InputStream inputStream = null;
		try {
			inputStream = jarFile.getInputStream(jarEntry);
			return formatManifestFile(inputStream, Constants.DEFAULT_CHARSET_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.EMPTY_MAP;
	}

	private static Map<String, String> formatManifestFile(InputStream inputStream, String charsetName)
			throws IOException {
		Map<String, String> map = new HashMap<String, String>(8);
		List<String> list = IOUtils.readLines(inputStream, charsetName);
		for (String content : list) {
			content = content.trim();
			if (StringUtils.isEmpty(content)) {
				continue;
			}

			int index = content.indexOf(":");
			if (index == -1) {
				continue;
			}

			String name = content.substring(0, index);
			String value = content.substring(index + 1);
			value = value.trim();
			map.put(name, value);
		}
		return map;
	}

	public static String getManifestClassPath(JarFile jarFile) {
		Map<String, String> map = formatManifestFile(jarFile);
		return map.get(MANIFEST_CLASS_PATH_NAME);
	}

	public static String[] getgetManifestClassPaths(JarFile jarFile) {
		String classPath = getManifestClassPath(jarFile);
		return StringUtils.isEmpty(classPath) ? new String[0] : StringUtils.split(classPath, ' ');
	}

	public static String findEntryName(JarFile jarFile, String searchName) {
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			JarEntry jarEntry = enumeration.nextElement();
			if (jarEntry == null) {
				continue;
			}

			String name = jarEntry.getName();
			name = name.replaceAll("\\\\", "/");
			if (name.endsWith(searchName)) {
				return jarEntry.getName();
			}
		}
		return null;
	}
}
