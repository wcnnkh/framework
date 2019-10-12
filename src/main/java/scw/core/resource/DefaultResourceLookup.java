package scw.core.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.JarFile;

import scw.core.Consumer;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.io.IOUtils;

public class DefaultResourceLookup extends ClassesResourceLookup {
	private static final ClassLoaderResourceLookup classLoaderResourceLookup = new ClassLoaderResourceLookup();

	@Override
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

			boolean b = false;
			if (!b) {
				for (String classPath : SystemPropertyUtils.getJavaClassPathArray()) {
					b = consumterInputStream(classPath, eqPath, consumer);
					if (b) {
						break;
					}
				}
			}

			if (!b) {
				b = classLoaderResourceLookup.lookup(eqPath, consumer);
			}

			if (!b) {
				URL url = ResourceUtils.getClassPathURL();
				if (url != null) {
					b = consumterInputStream(url.getPath(), eqPath, consumer);
				}
			}
			return b;
		} else {
			File file = new File(text);
			if (!file.exists()) {
				file = null;
			}

			if (file == null) {
				if (classLoaderResourceLookup.lookup(text.replaceAll("\\\\", "/"), consumer)) {
					return true;
				}
				return false;
			}

			consumerFileInputStream(file, consumer);
			return true;
		}
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
			} catch (Exception e) {
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
		} catch (Exception e) {
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

}
