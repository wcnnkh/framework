package scw.core;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import scw.core.utils.StringUtils;
import scw.io.FileUtils;
import scw.io.resource.DefaultResourceLookup;
import scw.io.resource.PropertyFactoryMultiSuffixResourceOperations;
import scw.io.resource.ResourceLookup;
import scw.io.resource.ResourceOperations;
import scw.util.FormatUtils;
import scw.util.MultiEnumeration;
import scw.util.value.Value;
import scw.util.value.property.ConcurrentMapPropertyFactory;
import scw.util.value.property.SystemPropertyFactory;

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
		if (getWorkPath() == null) {
			setWorkPath(getDefaultWorkPath());
		}

		loadProperties(
				getResourceOperations(),
				getValue("scw.properties.private", String.class,
						"/private.properties"));
	}

	public ResourceOperations getResourceOperations() {
		ResourceLookup resourceLookup = new DefaultResourceLookup(
				getWorkPath(), false, this);
		return new PropertyFactoryMultiSuffixResourceOperations(resourceLookup,
				this);
	}

	public Value get(String key) {
		Assert.requiredArgument(key != null, "key");
		Value v = super.get(key);
		if (v == null) {
			v = SystemPropertyFactory.getInstance().get(key);
		}
		return v;
	}

	public String getUserDir() {
		return getString("user.dir");
	}

	public String getUserHome() {
		return getString("user.home");
	}

	@SuppressWarnings("unchecked")
	public Enumeration<String> enumerationKeys() {
		return new MultiEnumeration<String>(super.enumerationKeys(),
				SystemPropertyFactory.getInstance().enumerationKeys());
	}

	/**
	 * 获取环境变量分割符
	 * 
	 * @return
	 */
	public String getPathSeparator() {
		return getString("path.separator");
	}

	public String getJavaClassPath() {
		return getString("java.class.path");
	}

	public void setWorkPath(String path) {
		if (path == null) {
			return;
		}

		put(WEB_ROOT, path);
	}

	public String getDefaultWorkPath() {
		File file = FileUtils
				.searchDirectory(new File(getUserDir()), "WEB-INF");
		return file == null ? getUserDir() : file.getParent();
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

	public String getSystemOnlyId() {
		String systemOnlyId = getString(SYSTEM_ID_PROPERTY);
		if (StringUtils.isEmpty(systemOnlyId)) {
			try {
				systemOnlyId = scw.core.Base64
						.encode((getUserDir() + "&" + getWorkPath())
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

	public String getMavenHome() {
		return getString("maven.home");
	}

	public String getTempDirectoryPath() {
		return getString("java.io.tmpdir");
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
		return getString(BASE_PACKAGE_NAME);
	}

	public void setBasePackageName(String packageName) {
		if (StringUtils.isEmpty(packageName)) {
			return;
		}

		putIfAbsent(BASE_PACKAGE_NAME, packageName);
	}
}