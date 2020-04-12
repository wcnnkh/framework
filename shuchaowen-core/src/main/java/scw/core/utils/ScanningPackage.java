package scw.core.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import scw.core.Constants;
import scw.core.GlobalPropertyFactory;

public class ScanningPackage {
	public Set<Class<?>> getClassList(String packageName,
			ClassLoader classLoader, boolean initialize) {
		Set<Class<?>> classes;
		if (!StringUtils.hasText(packageName)) {
			classes = getClassesDirectoryList(classLoader, initialize);
		}else{
			classes = new LinkedHashSet<Class<?>>();
			appendClassesByClassLoader(packageName, classes, classLoader,
					initialize);
		}
		return classes;
	}

	private void findAndAddClassesInPackageByFile(String packageName,
			String packagePath, Collection<Class<?>> classes,
			ClassLoader classLoader, boolean initialize) {
		File dir = new File(packagePath);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return (file.isDirectory())
						|| (file.getName()
								.endsWith(ClassUtils.CLASS_FILE_SUFFIX));
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(
						packageName + "." + file.getName(),
						file.getAbsolutePath(), classes, classLoader,
						initialize);
			} else {
				if (packageName.startsWith(".")) {
					packageName = packageName.substring(1);
				}
				Class<?> clazz = forFileName(
						packageName + "." + file.getName(), classLoader,
						initialize);
				if (clazz != null) {
					classes.add(clazz);
				}
			}
		}
	}

	protected Class<?> forFileName(String classFile, ClassLoader classLoader,
			boolean initialize) {
		if (!classFile.endsWith(ClassUtils.CLASS_FILE_SUFFIX)) {
			return null;
		}

		String name = classFile.substring(0, classFile.length() - 6);
		name = name.replaceAll("\\\\", ".");
		name = name.replaceAll("/", ".");
		try {
			return ClassUtils.forName(name, initialize, classLoader);
		} catch (Throwable e) {
		}
		return null;
	}

	private void appendClassesByURL(String packageName, String packageDirName,
			URL url, Collection<Class<?>> clazzList, ClassLoader classLoader,
			boolean initialize) throws IOException {
		String protocol = url.getProtocol();
		if ("file".equals(protocol)) {
			String filePath = URLDecoder.decode(url.getFile(),
					Constants.DEFAULT_CHARSET_NAME);
			findAndAddClassesInPackageByFile(packageName, filePath, clazzList,
					classLoader, initialize);
		} else if ("jar".equals(protocol)) {
			JarFile jar = ((JarURLConnection) url.openConnection())
					.getJarFile();
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}

				String name = entry.getName();
				if (name.charAt(0) == '/') {
					name = name.substring(1);
				}
				if (name.startsWith(packageDirName)) {
					Class<?> clazz = forFileName(name, classLoader, initialize);
					if (clazz != null) {
						clazzList.add(clazz);
					}
				}
			}
		}
	}

	private void appendClassesByClassLoader(String packageName,
			Collection<Class<?>> clazzList, ClassLoader classLoader,
			boolean initialize) {
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = classLoader.getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				appendClassesByURL(packageName, packageDirName, url, clazzList,
						classLoader, initialize);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			dirs = ClassLoader.getSystemClassLoader().getResources(
					packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				appendClassesByURL(packageName, packageDirName, url, clazzList,
						classLoader, initialize);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取一个目录下的class
	 * 
	 * @param directorey
	 * @param prefix
	 * @return
	 */
	private Set<Class<?>> getClassesDirectoryList(ClassLoader classLoader,
			boolean initialize) {
		LinkedHashSet<Class<?>> list = new LinkedHashSet<Class<?>>();
		String path = GlobalPropertyFactory.getInstance().getClassesDirectory();
		if (path == null) {
			return list;
		}
		appendDirectoryClass(null, new File(path), list, classLoader,
				initialize);
		return list;
	}

	private void appendDirectoryClass(String rootPackage, File file,
			Collection<Class<?>> classList, ClassLoader classLoader,
			boolean initialize) {
		File[] files = file.listFiles();
		if (ArrayUtils.isEmpty(files)) {
			return;
		}

		for (File f : files) {
			if (f.isDirectory()) {
				appendDirectoryClass(
						StringUtils.isEmpty(rootPackage) ? f.getName() + "."
								: rootPackage + f.getName() + ".", f,
						classList, classLoader, initialize);
			} else {
				if (f.getName().endsWith(".class")) {
					String classFile = StringUtils.isEmpty(rootPackage) ? f
							.getName() : rootPackage + f.getName();
					Class<?> clz = forFileName(classFile, classLoader,
							initialize);
					if (clz != null) {
						classList.add(clz);
					}
				}
			}
		}
	}
}
