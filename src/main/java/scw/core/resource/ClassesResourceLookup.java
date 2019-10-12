package scw.core.resource;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import scw.core.Verification;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.io.IOUtils;

public abstract class ClassesResourceLookup extends AbstractResourceLookup {
	private static final Verification<String> IGNORE_JAR_VERIFICATION = new IgnoreJarVerification();
	private static final Verification<String> IGNORE_CLASS_NAME_VERIFICATION = new IgnoreClassNameVerification();

	public Collection<Class<?>> getClasses() {
		return getClasses(IGNORE_JAR_VERIFICATION, IGNORE_CLASS_NAME_VERIFICATION);
	}

	public Collection<Class<?>> getClasses(Verification<String> jarVerification,
			Verification<String> classNameVerification) {
		LinkedHashSet<Class<?>> list = new LinkedHashSet<Class<?>>();
		URL url = ResourceUtils.getClassPathURL();
		if (url != null) {
			appendClass(url.getPath(), list, jarVerification, classNameVerification, true);
		}

		for (String name : SystemPropertyUtils.getJavaClassPathArray()) {
			appendClass(name, list, jarVerification, classNameVerification, true);
		}
		return list;
	}

	private void appendJarClass(Collection<Class<?>> classList, JarFile jarFile, Verification<String> jarVerification,
			Verification<String> verification, boolean appendManifest) {
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			JarEntry jarEntry = enumeration.nextElement();
			if (jarEntry == null) {
				continue;
			}

			String name = jarEntry.getName();
			if (name.endsWith(CLASS_SUFFIX)) {
				// class
				Class<?> clz = forFileName(name, verification);
				if (clz != null) {
					classList.add(clz);
				}
			}
		}

		if (appendManifest) {
			for (String path : JarUtils.getgetManifestClassPaths(jarFile)) {
				appendClass(path, classList, jarVerification, verification, false);
			}
		}
	}

	private void appendDirectoryClass(String rootPackage, Collection<Class<?>> classList, File file,
			Verification<String> jarVerification, Verification<String> verification) {
		File[] files = file.listFiles();
		if (ArrayUtils.isEmpty(files)) {
			return;
		}

		for (File f : files) {
			if (f.isDirectory()) {
				appendDirectoryClass(
						StringUtils.isEmpty(rootPackage) ? f.getName() + "." : rootPackage + f.getName() + ".",
						classList, f, jarVerification, verification);
			} else {
				if (f.getName().endsWith(".class")) {
					String classFile = StringUtils.isEmpty(rootPackage) ? f.getName() : rootPackage + f.getName();
					Class<?> clz = forFileName(classFile, verification);
					if (clz != null) {
						classList.add(clz);
					}
				} else if (f.getName().endsWith(".jar")) {
					if (jarVerification == null || jarVerification.verification(f.getName())) {
						appendJarClass(f, classList, jarVerification, verification, false);
					}
				}
			}
		}
	}

	private void appendJarClass(File file, Collection<Class<?>> classList, Verification<String> jarVerification,
			Verification<String> verification, boolean appendManifest) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			appendJarClass(classList, jarFile, jarVerification, verification, appendManifest);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(jarFile);
		}
	}

	private void appendClass(String path, Collection<Class<?>> list, Verification<String> jarVerification,
			Verification<String> verification, boolean appendManifest) {
		File file = new File(path);
		if (file.isFile()) {
			if (jarVerification == null || jarVerification.verification(file.getName())) {
				appendJarClass(file, list, jarVerification, verification, appendManifest);
			}
		} else {
			appendDirectoryClass(null, list, file, jarVerification, verification);
		}
	}

	protected Class<?> forFileName(String classFile, Verification<String> verification) {
		if (!classFile.endsWith(CLASS_SUFFIX)) {
			return null;
		}

		String name = classFile.substring(0, classFile.length() - 6);
		name = name.replaceAll("\\\\", ".");
		name = name.replaceAll("/", ".");
		if (verification != null && !verification.verification(name)) {
			return null;
		}

		try {
			return Class.forName(name, false, ClassUtils.getDefaultClassLoader());
		} catch (Throwable e) {
		}
		return null;
	}

	public Collection<Class<?>> getClasses(String resource, Verification<String> classNameVerification) {
		if (StringUtils.isEmpty(resource)) {
			return getClasses(IGNORE_JAR_VERIFICATION, classNameVerification);
		}

		final String[] arr = StringUtils.commonSplit(resource);
		if (ArrayUtils.isEmpty(arr)) {
			return getClasses(IGNORE_JAR_VERIFICATION, classNameVerification);
		}

		HashSet<Class<?>> classes = new HashSet<Class<?>>();
		for (String pg : arr) {
			appendClassesByClassLoader(pg, classes, classNameVerification);
		}
		return classes;
	}

	protected void appendClassesByClassLoader(String packageName, Collection<Class<?>> clazzList,
			Verification<String> classNameVerification) {
		String packageDirName = packageName.replace('.', '/');
		Enumeration<URL> dirs;
		try {
			dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
			while (dirs.hasMoreElements()) {
				URL url = dirs.nextElement();
				String protocol = url.getProtocol();
				if ("file".equals(protocol)) {
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					findAndAddClassesInPackageByFile(packageName, filePath, clazzList);
				} else if ("jar".equals(protocol)) {
					JarFile jar;
					try {
						jar = ((JarURLConnection) url.openConnection()).getJarFile();
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
								Class<?> clazz = forFileName(name, classNameVerification);
								if (clazz != null) {
									clazzList.add(clazz);
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
