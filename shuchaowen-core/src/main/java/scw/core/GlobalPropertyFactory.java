package scw.core;

import java.io.File;
import java.io.UnsupportedEncodingException;

import scw.core.utils.StringUtils;
import scw.io.FileUtils;
import scw.io.support.ResourceOperations;
import scw.util.FormatUtils;
import scw.util.PackageScan;
import scw.value.property.ConcurrentMapPropertyFactory;
import scw.value.property.SystemPropertyFactory;

public final class GlobalPropertyFactory extends ConcurrentMapPropertyFactory {
	private static final String WEB_ROOT = "web.root";
	private static final String CLASSES_DIRECTORY = "classes.directory";
	private static final String SYSTEM_ID_PROPERTY = "private.system.id";
	private static final String BASE_PACKAGE_NAME = "scw.base.package";
	private static GlobalPropertyFactory instance = new GlobalPropertyFactory();

	public static GlobalPropertyFactory getInstance() {
		return instance;
	}

	private GlobalPropertyFactory() {
		addBasePropertyFactory(SystemPropertyFactory.getInstance());
		if (getWorkPath() == null) {
			setWorkPath(getDefaultWorkPath());
		}

		ResourceOperations operations = new ResourceOperations(this, false);
		loadProperties(operations, "global.properties");
		loadProperties(
				operations,
				getValue("scw.properties.private", String.class,
						"/private.properties"));
	}

	public void setWorkPath(String path) {
		if (path == null) {
			return;
		}

		put(WEB_ROOT, path);
	}

	public String getDefaultWorkPath() {
		File file = FileUtils.searchDirectory(new File(SystemPropertyFactory
				.getInstance().getUserDir()), "WEB-INF");
		return file == null ? SystemPropertyFactory.getInstance().getUserDir()
				: file.getParent();
	}

	public String getWorkPath() {
		return getString(WEB_ROOT);
	}

	public String getClassesDirectory() {
		String path = getString(CLASSES_DIRECTORY);
		if (path != null) {
			return path;
		}

		File file = new File(getWorkPath());
		if (!file.exists() || !file.isDirectory()) {
			return null;
		}

		File webInf = null;
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory() && f.getName().equals("WEB-INF")) {
					webInf = f;
				}
			}
		}

		if (webInf == null) {
			return null;
		}

		files = webInf.listFiles();
		if (files != null) {
			for (File f : files) {
				if (f.isDirectory() && f.getName().equals("classes")) {
					setClassesDirectory(f.getPath());
				}
			}
		}
		return getString(CLASSES_DIRECTORY);
	}

	public void setClassesDirectory(String directory) {
		if (StringUtils.isEmpty(directory)) {
			return;
		}

		put(CLASSES_DIRECTORY, directory);
	}

	/**
	 * 获取系统本地标识，注意，仅对本地有效
	 * 
	 * @return
	 */
	public String getSystemLocalId() {
		String systemOnlyId = getString(SYSTEM_ID_PROPERTY);
		if (StringUtils.isEmpty(systemOnlyId)) {
			try {
				systemOnlyId = scw.util.Base64.encode((SystemPropertyFactory
						.getInstance().getUserDir() + "&" + getWorkPath())
						.getBytes(Constants.DEFAULT_CHARSET_NAME));
				if (systemOnlyId.endsWith("==")) {
					systemOnlyId = systemOnlyId.substring(0,
							systemOnlyId.length() - 2);
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			put(SYSTEM_ID_PROPERTY, systemOnlyId);
		}
		return systemOnlyId;
	}

	public String format(String text, boolean supportEL) {
		return FormatUtils.format(text, this, supportEL);
	}

	/**
	 * 可能会返回空
	 * 
	 * @return
	 */
	public String getBasePackageName() {
		return getValue(BASE_PACKAGE_NAME, String.class, PackageScan.ALL);
	}

	public void setBasePackageName(String packageName) {
		if (StringUtils.isEmpty(packageName)) {
			return;
		}

		putIfAbsent(BASE_PACKAGE_NAME, packageName);
	}
}
