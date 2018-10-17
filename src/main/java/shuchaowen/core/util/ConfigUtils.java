package shuchaowen.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import shuchaowen.core.exception.ShuChaoWenRuntimeException;

public class ConfigUtils {
	private static final String WEB_ROOT = "web.root";
	private static final String CLASSPATH = "classpath";
	private static final String CLASSPATH_PREFIX = CLASSPATH + ":";
	private static final String WEB_CONTENT = "WebContent";
	private static final String WEBAPP = "webapp";
	private static final StringFormatSystemProperties format1 = new StringFormatSystemProperties("{", "}");
	private static final StringFormatSystemProperties format2 = new StringFormatSystemProperties("[", "]");
	public static final String CONFIG_SUFFIX = "SHUCHAOWEN_CONFIG_SUFFIX";

	public static String getSystemProperty(String key) {
		String v = System.getProperty(key);
		if (v == null) {
			v = System.getenv(key);
		}

		if (v == null) {
			if (WEB_ROOT.equalsIgnoreCase(key)) {
				return getWorkPath();
			} else if (CLASSPATH.equalsIgnoreCase(key)) {
				return getClassPath();
			}
		}
		return v;
	}

	public static String format(String value) {
		if (StringUtils.isNull(value)) {
			return value;
		}

		String newPath = format1.format(value);
		newPath = format2.format(newPath);

		if (newPath.length() < CLASSPATH_PREFIX.length()) {
			return newPath;
		}

		String prefix = newPath.substring(0, CLASSPATH_PREFIX.length());
		prefix = prefix.toUpperCase();
		if (prefix.equals(CLASSPATH_PREFIX.toUpperCase())) {
			return getClassPath() + newPath.substring(CLASSPATH_PREFIX.length());
		}
		return newPath;
	}

	public static String getWorkPath() {
		String path = ConfigUtils.class.getResource("/").getPath();
		File file = new File(path);
		file = file.getParentFile().getParentFile();
		File f = FileUtils.searchDirectory(file.getPath(), WEB_CONTENT);
		if (f == null) {
			f = FileUtils.searchDirectory(file.getPath(), WEBAPP);
		}
		return f == null ? file.getPath() : f.getPath();
	}

	public static String getClassPath() {
		return ConfigUtils.class.getResource("/").getPath();
	}

	public static <T> T parseObject(Map<String, String> map, Class<T> clz)
			throws InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		T t = clz.newInstance();
		for (Entry<String, String> entry : map.entrySet()) {
			Field field = clz.getDeclaredField(entry.getKey());
			if (field == null) {
				continue;
			}
			FieldInfo fieldInfo = new FieldInfo(clz, field);
			fieldInfo.set(t, StringUtils.conversion(entry.getValue(), fieldInfo.getType()));
		}
		return t;
	}

	public static String searchFileName(String fileName) {
		return FileUtils.searchFileName(fileName, getClassPath(), true);
	}

	private static String getFilePath(final String filePath) {
		String path = format(filePath);
		File file = new File(path);
		if (file.exists()) {
			return path;
		} else {// 如果找不到目录就到classpath下去找
			String classPath = getClassPath() + path;
			file = new File(classPath);
			if (file.exists()) {
				return classPath;
			} else {// 如果clsspath下找不到就去workpath下去找
				String workPath = getWorkPath() + path;
				file = new File(workPath);
				if (file.exists()) {
					return workPath;
				} else {
					return path;
				}
			}
		}
	}

	public static File getFile(String filePath) {
		String configSuffix = getSystemProperty(CONFIG_SUFFIX);
		if (StringUtils.isNull(configSuffix)) {
			return getFile(filePath, null);
		} else {
			return getFile(filePath, StringUtils.splitList(String.class, configSuffix, ";", false));
		}
	}

	public static File getFile(String filePath, List<String> testSuffix) {
		File file = new File(getFilePath(filePath));
		if (testSuffix == null || testSuffix.isEmpty()) {
			return file;
		}

		for (String sf : testSuffix) {
			File testFile = new File(file.getParent() + File.separator + getTestFileName(file.getName(), sf));
			if (testFile.exists()) {
				return testFile;
			}
		}
		return file;
	}

	private static String getTestFileName(String fileName, String str) {
		int index = fileName.indexOf(".");
		if (index == -1) {// 不存在
			return fileName + str;
		} else {
			return fileName.substring(0, index) + str + fileName.substring(index);
		}
	}

	public static final Properties getProperties(File file, String charsetName) {
		Properties properties = new Properties();
		InputStreamReader isr = null;
		try {
			isr = new InputStreamReader(new FileInputStream(file), charsetName);
			properties.load(isr);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			XUtils.close(isr);
		}
		return properties;
	}

	public static List<String> getFileContentLineList(File file, String charsetName) {
		FileInputStream fis = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(file);
			br = new BufferedReader(new InputStreamReader(fis, charsetName));
			return IOUtils.readerContent(br);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			XUtils.close(br, fis);
		}
		return null;
	}

	public static List<Map<String, String>> getDefaultXmlContent(File file, String rootTag) {
		if (rootTag == null) {
			throw new NullPointerException("rootTag is null");
		}

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		try {
			try {
				Document doc = DOMHelper.parse(file);
				Element root = doc.getDocumentElement();

				NodeList nhosts = root.getChildNodes();
				for (int i = 0; i < nhosts.getLength(); i++) {
					Node nRoot = nhosts.item(i);
					if (nRoot.getNodeName().equals(rootTag)) {
						list.add(DOMHelper.xmlToMap(nRoot));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static final Properties getProperties(String filePath, String charsetName) {
		return getProperties(getFile(filePath), charsetName);
	}

	public static <T> T wrapperProerties(Class<T> type, Properties properties) {
		ClassInfo classInfo = new ClassInfo(type);
		try {
			T obj = ClassUtils.newInstance(type);
			for (Object key : properties.keySet()) {
				FieldInfo fieldInfo = classInfo.getFieldMap().get(key.toString());
				if (fieldInfo == null) {
					continue;
				}

				String value = ConfigUtils.format(properties.getProperty(key.toString()));
				fieldInfo.set(obj, StringUtils.conversion(value, fieldInfo.getType()));
			}
			return obj;
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}

	public static List<String> getFileContentLineList(String filePath, String charsetName) {
		return getFileContentLineList(getFile(filePath), charsetName);
	}

	public static String getFileContent(String filePath, String charsetName) {
		return FileUtils.readerFileContent(getFile(filePath), charsetName).toString();
	}
}
