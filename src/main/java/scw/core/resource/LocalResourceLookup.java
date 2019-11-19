package scw.core.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarFile;

import scw.core.Consumer;
import scw.core.utils.ClassUtils;
import scw.core.utils.JarUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.io.IOUtils;

/**
 * 本地资源处理
 * 
 * @author shuchaowen
 *
 */
public final class LocalResourceLookup implements ResourceLookup {
	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	private static final String CLASSPATH_URL_PREFIX = "classpath:";
	private static final String CONFIG_NAME = "config";
	private static final String CLASS_PATH_PREFIX_EL = "{classpath}";
	private static final String CLASS_PATH_PREFIX_EL_2 = "{" + CLASSPATH_URL_PREFIX + "}";

	public boolean lookup(String resource, Consumer<InputStream> consumer) {
		if (StringUtils.isEmpty(resource)) {
			return false;
		}

		String text = SystemPropertyUtils.format(resource);
		// 兼容老版本
		if (StringUtils.startsWithIgnoreCase(text, CLASSPATH_URL_PREFIX)
				|| StringUtils.startsWithIgnoreCase(text, CLASS_PATH_PREFIX_EL_2)
				|| StringUtils.startsWithIgnoreCase(text, CLASS_PATH_PREFIX_EL)) {
			String eqPath = text.replaceAll("\\\\", "/");
			if (StringUtils.startsWithIgnoreCase(text, CLASSPATH_URL_PREFIX)) {
				eqPath = eqPath.substring(CLASSPATH_URL_PREFIX.length());
			} else if (StringUtils.startsWithIgnoreCase(text, CLASS_PATH_PREFIX_EL_2)) {
				eqPath = eqPath.substring(CLASS_PATH_PREFIX_EL_2.length());
			} else {
				eqPath = eqPath.substring(CLASS_PATH_PREFIX_EL.length());
			}

			return lockupClassPath(eqPath, consumer);
		} else {
			if (lockupFile(text, consumer)) {
				return true;
			}

			if (lockupClassPath(text, consumer)) {
				return true;
			}

			if (lockupFile(SystemPropertyUtils.getWorkPath() + (text.startsWith("/") ? "" : "/") + text, consumer)) {
				return true;
			}
			return false;
		}
	}

	private boolean lockupClassPath(String resource, Consumer<InputStream> consumer) {
		boolean b = false;
		if (!b) {
			for (String classPath : SystemPropertyUtils.getJavaClassPathArray()) {
				b = consumterInputStream(classPath, resource, consumer);
				if (b) {
					break;
				}
			}
		}

		if (!b) {
			b = lookupClassLoader(resource, consumer);
		}

		if (!b) {
			URL url = ResourceUtils.getClassPathURL();
			if (url != null) {
				b = consumterInputStream(url.getPath(), resource, consumer);
			}
		}
		return b;
	}

	private boolean lockupFile(String resource, Consumer<InputStream> consumer) {
		File file = new File(resource);
		if (!file.exists()) {
			file = null;
		}

		if (file == null) {
			if (lookupClassLoader(resource.replaceAll("\\\\", "/"), consumer)) {
				return true;
			}

			return false;
		}
		consumerFileInputStream(file, consumer);
		return true;
	}

	private static boolean consumterInputStream(String rootPath, String path, Consumer<InputStream> consumer) {
		File file = new File(rootPath);
		if (!file.exists()) {
			return false;
		}

		if (file.isFile()) {// jar
			File configFile = searchJarClassPathConfigFile(file, CONFIG_NAME, path);
			if (configFile != null) {
				consumerFileInputStream(configFile, consumer);
				return true;
			}

			JarFile jarFile = null;
			InputStream inputStream = null;
			try {
				jarFile = new JarFile(file);
				String entryName = JarUtils.findEntryName(jarFile, path);
				if (entryName == null) {
					return false;
				}

				if (consumer != null) {
					inputStream = jarFile.getInputStream(jarFile.getEntry(entryName));
					consumer.consume(inputStream);
				}
				return true;
			} catch (Throwable e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.close(inputStream, jarFile);
			}
		} else {
			File f = searchFile(path, file);
			if (f == null) {
				return false;
			}

			consumerFileInputStream(f, consumer);
			return true;
		}
	}

	private static File searchJarClassPathConfigFile(File rootFile, String configPath, String path) {
		File file = new File(rootFile.getParent() + File.separator + configPath);
		if (!file.exists()) {
			return null;
		}

		return searchFile(path, file);
	}

	private static void consumerFileInputStream(File file, Consumer<InputStream> consumer) {
		if (consumer == null) {
			return;
		}

		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			consumer.consume(inputStream);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.close(inputStream);
		}
	}

	private static File searchFile(String path, File rootFile) {
		if (!rootFile.exists()) {
			return null;
		}

		File[] files = rootFile.listFiles();
		if (files == null) {
			return null;
		}

		for (File file : files) {
			if (file.isFile()) {
				String p = file.getPath().replaceAll("\\\\", "/");
				if (p.endsWith(path)) {
					return file;
				}
			} else {
				File f = searchFile(path, file);
				if (f != null) {
					return f;
				}
			}
		}
		return null;
	}

	private boolean lookupClassLoader(String resource, Consumer<InputStream> consumer) {
		InputStream inputStream = getResourceAsStream(resource);
		if (inputStream == null) {
			return false;
		}

		if (consumer != null) {
			try {
				consumer.consume(inputStream);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.close(inputStream);
			}
		}
		return true;
	}

	private static InputStream getResourceAsStream(String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}

		InputStream inputStream = LocalResourceLookup.class.getResourceAsStream(name);
		if (inputStream == null) {
			try {
				inputStream = ClassUtils.getDefaultClassLoader().getResourceAsStream(name);
			} catch (Exception e) {
				// ignore 在一些特殊情况下可能出现异常，忽略此异常
			}
		}
		return inputStream;
	}
}
