/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scw.core.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import scw.core.Consumer;
import scw.core.Convert;
import scw.core.Verification;
import scw.core.exception.NotFoundException;
import scw.io.IOUtils;
import scw.logger.log4j.Log4jConfigurer;

/**
 * Utility methods for resolving resource locations to files in the file system.
 * Mainly for internal use within the framework.
 *
 * <p>
 * Consider using Spring's Resource abstraction in the core package for handling
 * all kinds of file resources in a uniform manner.
 * {@link shuchaowen.spring.core.io.ResourceLoader}'s {@code getResource} method
 * can resolve any location to a {@link shuchaowen.spring.core.io.Resource}
 * object, which in turn allows to obtain a {@code java.io.File} in the file
 * system through its {@code getFile()} method.
 *
 * <p>
 * The main reason for these utility methods for resource location handling is
 * to support {@link Log4jConfigurer}, which must be able to resolve resource
 * locations <i>before the logging system has been initialized</i>. Spring'
 * Resource abstraction in the core package, on the other hand, already expects
 * the logging system to be available.
 *
 * @author Juergen Hoeller
 * @since 1.1.5
 * @see shuchaowen.spring.core.io.Resource
 * @see shuchaowen.spring.core.io.ClassPathResource
 * @see shuchaowen.spring.core.io.FileSystemResource
 * @see shuchaowen.spring.core.io.UrlResource
 * @see shuchaowen.spring.core.io.ResourceLoader
 */
public abstract class ResourceUtils {

	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	/** URL prefix for loading from the file system: "file:" */
	public static final String FILE_URL_PREFIX = "file:";

	/** URL protocol for a file in the file system: "file" */
	public static final String URL_PROTOCOL_FILE = "file";

	/** URL protocol for an entry from a jar file: "jar" */
	public static final String URL_PROTOCOL_JAR = "jar";

	/** URL protocol for an entry from a zip file: "zip" */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/** URL protocol for an entry from a JBoss jar file: "vfszip" */
	public static final String URL_PROTOCOL_VFSZIP = "vfszip";

	/** URL protocol for a JBoss VFS resource: "vfs" */
	public static final String URL_PROTOCOL_VFS = "vfs";

	/** URL protocol for an entry from a WebSphere jar file: "wsjar" */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";

	/** URL protocol for an entry from an OC4J jar file: "code-source" */
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

	/** Separator between JAR URL and file path within the JAR */
	public static final String JAR_URL_SEPARATOR = "!/";

	private static final String CONFIG_SUFFIX = "SHUCHAOWEN_CONFIG_SUFFIX";

	private static final String RESOURCE_SUFFIX = "scw_res_suffix";

	/**
	 * Return whether the given resource location is a URL: either a special
	 * "classpath" pseudo URL or a standard URL.
	 * 
	 * @param resourceLocation
	 *            the location String to check
	 * @return whether the location qualifies as a URL
	 * @see #CLASSPATH_URL_PREFIX
	 * @see java.net.URL
	 */
	public static boolean isUrl(String resourceLocation) {
		if (resourceLocation == null) {
			return false;
		}
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			return true;
		}
		try {
			new URL(resourceLocation);
			return true;
		} catch (MalformedURLException ex) {
			return false;
		}
	}

	/**
	 * Resolve the given resource location to a {@code java.net.URL}.
	 * <p>
	 * Does not check whether the URL actually exists; simply returns the URL
	 * that the given location would correspond to.
	 * 
	 * @param resourceLocation
	 *            the resource location to resolve: either a "classpath:" pseudo
	 *            URL, a "file:" URL, or a plain file path
	 * @return a corresponding URL object
	 * @throws FileNotFoundException
	 *             if the resource cannot be resolved to a URL
	 */
	public static URL getURL(String resourceLocation)
			throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(CLASSPATH_URL_PREFIX
					.length());
			URL url = ClassUtils.getDefaultClassLoader().getResource(path);
			if (url == null) {
				String description = "class path resource [" + path + "]";
				throw new FileNotFoundException(
						description
								+ " cannot be resolved to URL because it does not exist");
			}
			return url;
		}
		try {
			// try URL
			return new URL(resourceLocation);
		} catch (MalformedURLException ex) {
			// no URL -> treat as file path
			try {
				return new File(resourceLocation).toURI().toURL();
			} catch (MalformedURLException ex2) {
				throw new FileNotFoundException("Resource location ["
						+ resourceLocation
						+ "] is neither a URL not a well-formed file path");
			}
		}
	}

	/**
	 * Resolve the given resource location to a {@code java.io.File}, i.e. to a
	 * file in the file system.
	 * <p>
	 * Does not check whether the fil actually exists; simply returns the File
	 * that the given location would correspond to.
	 * 
	 * @param resourceLocation
	 *            the resource location to resolve: either a "classpath:" pseudo
	 *            URL, a "file:" URL, or a plain file path
	 * @return a corresponding File object
	 * @throws FileNotFoundException
	 *             if the resource cannot be resolved to a file in the file
	 *             system
	 */
	public static File getFile(String resourceLocation)
			throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(CLASSPATH_URL_PREFIX
					.length());
			String description = "class path resource [" + path + "]";
			URL url = ClassUtils.getDefaultClassLoader().getResource(path);
			if (url == null) {
				throw new FileNotFoundException(description
						+ " cannot be resolved to absolute file path "
						+ "because it does not reside in the file system");
			}
			return getFile(url, description);
		}
		try {
			// try URL
			return getFile(new URL(resourceLocation));
		} catch (MalformedURLException ex) {
			// no URL -> treat as file path
			return new File(resourceLocation);
		}
	}

	/**
	 * Resolve the given resource URL to a {@code java.io.File}, i.e. to a file
	 * in the file system.
	 * 
	 * @param resourceUrl
	 *            the resource URL to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException
	 *             if the URL cannot be resolved to a file in the file system
	 */
	public static File getFile(URL resourceUrl) throws FileNotFoundException {
		return getFile(resourceUrl, "URL");
	}

	/**
	 * Resolve the given resource URL to a {@code java.io.File}, i.e. to a file
	 * in the file system.
	 * 
	 * @param resourceUrl
	 *            the resource URL to resolve
	 * @param description
	 *            a description of the original resource that the URL was
	 *            created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException
	 *             if the URL cannot be resolved to a file in the file system
	 */
	public static File getFile(URL resourceUrl, String description)
			throws FileNotFoundException {
		Assert.notNull(resourceUrl, "Resource URL must not be null");
		if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(description
					+ " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: "
					+ resourceUrl);
		}
		try {
			return new File(toURI(resourceUrl).getSchemeSpecificPart());
		} catch (URISyntaxException ex) {
			// Fallback for URLs that are not valid URIs (should hardly ever
			// happen).
			return new File(resourceUrl.getFile());
		}
	}

	/**
	 * Resolve the given resource URI to a {@code java.io.File}, i.e. to a file
	 * in the file system.
	 * 
	 * @param resourceUri
	 *            the resource URI to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException
	 *             if the URL cannot be resolved to a file in the file system
	 */
	public static File getFile(URI resourceUri) throws FileNotFoundException {
		return getFile(resourceUri, "URI");
	}

	/**
	 * Resolve the given resource URI to a {@code java.io.File}, i.e. to a file
	 * in the file system.
	 * 
	 * @param resourceUri
	 *            the resource URI to resolve
	 * @param description
	 *            a description of the original resource that the URI was
	 *            created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException
	 *             if the URL cannot be resolved to a file in the file system
	 */
	public static File getFile(URI resourceUri, String description)
			throws FileNotFoundException {
		Assert.notNull(resourceUri, "Resource URI must not be null");
		if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
			throw new FileNotFoundException(description
					+ " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: "
					+ resourceUri);
		}
		return new File(resourceUri.getSchemeSpecificPart());
	}

	/**
	 * Determine whether the given URL points to a resource in the file system,
	 * that is, has protocol "file" or "vfs".
	 * 
	 * @param url
	 *            the URL to check
	 * @return whether the URL has been identified as a file system URL
	 */
	public static boolean isFileURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_FILE.equals(protocol) || protocol
				.startsWith(URL_PROTOCOL_VFS));
	}

	/**
	 * Determine whether the given URL points to a resource in a jar file, that
	 * is, has protocol "jar", "zip", "wsjar" or "code-source".
	 * <p>
	 * "zip" and "wsjar" are used by BEA WebLogic Server and IBM WebSphere,
	 * respectively, but can be treated like jar files. The same applies to
	 * "code-source" URLs on Oracle OC4J, provided that the path contains a jar
	 * separator.
	 * 
	 * @param url
	 *            the URL to check
	 * @return whether the URL has been identified as a JAR URL
	 */
	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol)
				|| URL_PROTOCOL_ZIP.equals(protocol)
				|| URL_PROTOCOL_WSJAR.equals(protocol) || (URL_PROTOCOL_CODE_SOURCE
				.equals(protocol) && url.getPath().contains(JAR_URL_SEPARATOR)));
	}

	/**
	 * Extract the URL for the actual jar file from the given URL (which may
	 * point to a resource in a jar file or to a jar file itself).
	 * 
	 * @param jarUrl
	 *            the original URL
	 * @return the URL for the actual jar file
	 * @throws MalformedURLException
	 *             if no valid jar file URL could be extracted
	 */
	public static URL extractJarFileURL(URL jarUrl)
			throws MalformedURLException {
		String urlFile = jarUrl.getFile();
		int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
		if (separatorIndex != -1) {
			String jarFile = urlFile.substring(0, separatorIndex);
			try {
				return new URL(jarFile);
			} catch (MalformedURLException ex) {
				// Probably no protocol in original jar URL, like
				// "jar:C:/mypath/myjar.jar".
				// This usually indicates that the jar file resides in the file
				// system.
				if (!jarFile.startsWith("/")) {
					jarFile = "/" + jarFile;
				}
				return new URL(FILE_URL_PREFIX + jarFile);
			}
		} else {
			return jarUrl;
		}
	}

	/**
	 * Create a URI instance for the given URL, replacing spaces with "%20"
	 * quotes first.
	 * <p>
	 * Furthermore, this method works on JDK 1.4 as well, in contrast to the
	 * {@code URL.toURI()} method.
	 * 
	 * @param url
	 *            the URL to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException
	 *             if the URL wasn't a valid URI
	 * @see java.net.URL#toURI()
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}

	/**
	 * Create a URI instance for the given location String, replacing spaces
	 * with "%20" quotes first.
	 * 
	 * @param location
	 *            the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException
	 *             if the location wasn't a valid URI
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(StringUtils.replace(location, " ", "%20"));
	}

	/**
	 * Set the {@link URLConnection#setUseCaches "useCaches"} flag on the given
	 * connection, preferring {@code false} but leaving the flag at {@code true}
	 * for JNLP based resources.
	 * 
	 * @param con
	 *            the URLConnection to set the flag on
	 */
	public static void useCachesIfNecessary(URLConnection con) {
		con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
	}

	public static URL getClassPathURL() {
		URL url = ResourceUtils.class.getResource("/");
		if (url == null) {
			ProtectionDomain protectionDomain = ResourceUtils.class
					.getProtectionDomain();
			if (protectionDomain != null) {
				CodeSource codeSource = protectionDomain.getCodeSource();
				if (codeSource != null) {
					url = codeSource.getLocation();
				}
			}
		}
		return url;
	}

	public static void setConfigSuffix(String suffix) {
		if (StringUtils.isEmpty(suffix)) {
			return;
		}

		System.setProperty(CONFIG_SUFFIX, suffix);
	}

	private static String searchNameByJar(JarFile jarFile, String searchName) {
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

	private static boolean consumterInputStream(String rootPath, String path,
			String[] suffixs, Consumer<InputStream> consumer) {
		File file = new File(rootPath);
		if (!file.exists()) {
			return false;
		}

		if (file.isFile()) {// jar
			File configFile = searchJarClassPathConfigFile(file, "config",
					suffixs, path);
			if (configFile != null) {
				consumerFileInputStream(configFile, consumer);
				return true;
			}

			JarFile jarFile = null;
			InputStream inputStream = null;
			try {
				jarFile = new JarFile(file);
				String entryName = null;
				if (!ArrayUtils.isEmpty(suffixs)) {
					for (String name : suffixs) {
						String n = getTestFileName(path, name);
						entryName = searchNameByJar(jarFile, n);
						if (entryName != null) {
							break;
						}
					}
				}

				if (entryName == null) {
					entryName = searchNameByJar(jarFile, path);
				}

				if (entryName == null) {
					return false;
				}

				inputStream = jarFile.getInputStream(jarFile
						.getEntry(entryName));
				consumer.consume(inputStream);
				return true;
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.close(inputStream, jarFile);
			}
		} else {
			File f = getClassPathFile(file, suffixs, path);
			if (f == null) {
				return false;
			}

			consumerFileInputStream(f, consumer);
			return true;
		}
	}

	public static void consumterInputStream(String path,
			Consumer<InputStream> consumer) {
		if (StringUtils.isEmpty(path)) {
			throw new NotFoundException(path);
		}

		String text = SystemPropertyUtils.format(path);
		String[] suffixs = getResourceSuffix();
		String eqPath = path.replaceAll("\\\\", "/");
		if (text.length() > CLASSPATH_URL_PREFIX.length()
				&& text.substring(0, CLASSPATH_URL_PREFIX.length())
						.toLowerCase().startsWith(CLASSPATH_URL_PREFIX)) {
			eqPath = eqPath.substring(CLASSPATH_URL_PREFIX.length());
			boolean b = false;
			URL url = getClassPathURL();
			if (url != null) {
				b = consumterInputStream(url.getPath(), eqPath, suffixs,
						consumer);
			}

			if (!b) {
				for (String classPath : SystemPropertyUtils
						.getJavaClassPathArray()) {
					b = consumterInputStream(classPath, eqPath, suffixs,
							consumer);
					if (b) {
						break;
					}
				}
			}

			if (!b) {
				throw new NotFoundException(path);
			}
		} else {
			File file = null;
			if (!ArrayUtils.isEmpty(suffixs)) {
				for (String name : suffixs) {
					file = new File(getTestFileName(path, name));
					if (file.exists()) {
						break;
					}
				}
			}

			if (file == null) {
				file = new File(path);
				if (!file.exists()) {
					file = null;
				}
			}

			if (file == null) {
				throw new NotFoundException(path);
			}
			consumerFileInputStream(file, consumer);
		}
	}

	private static File searchJarClassPathConfigFile(File rootFile,
			String configPath, String[] suffixs, String path) {
		File file = new File(rootFile.getParent() + File.separator + configPath);
		if (!file.exists()) {
			return null;
		}

		return getClassPathFile(file, suffixs, path);
	}

	private static File getClassPathFile(File rootFile, String[] suffixs,
			String path) {
		File f = null;
		if (!ArrayUtils.isEmpty(suffixs)) {
			for (String name : suffixs) {
				f = searchFile(getTestFileName(path, name), rootFile);
				if (f != null) {
					break;
				}
			}
		}

		if (f == null) {
			f = searchFile(path, rootFile);
		}

		return f;
	}

	private static void consumerFileInputStream(File file,
			Consumer<InputStream> consumer) {
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

	private static String[] getResourceSuffix() {
		String value = SystemPropertyUtils.getProperty(RESOURCE_SUFFIX);
		if (value == null) {
			value = SystemPropertyUtils.getProperty(CONFIG_SUFFIX);
		}

		return StringUtils.isEmpty(value) ? null : StringUtils
				.commonSplit(value);
	}

	private static String getTestFileName(String fileName, String str) {
		int index = fileName.indexOf(".");
		if (index == -1) {// 不存在
			return fileName + str;
		} else {
			return fileName.substring(0, index) + str
					+ fileName.substring(index);
		}
	}

	public static boolean isExist(String path) {
		if (StringUtils.isEmpty(path)) {
			return false;
		}

		try {
			consumterInputStream(path, new Consumer<InputStream>() {

				public void consume(InputStream message) throws Exception {
					// ignore
				}
			});
			return true;
		} catch (NotFoundException e) {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getAndConvert(String path,
			final Convert<InputStream, T> convert) {
		final Object[] objs = new Object[1];
		consumterInputStream(path, new Consumer<InputStream>() {

			public void consume(InputStream message) throws Exception {
				objs[0] = convert.convert(message);
			}
		});
		return (T) objs[0];
	}

	private static Class<?> forFileNmae(String classFile,
			Verification<String> verification) {
		if (!classFile.endsWith(".class")) {
			return null;
		}

		String name = classFile.substring(0, classFile.length() - 6);
		name = name.replaceAll("\\\\", ".");
		name = name.replaceAll("/", ".");
		if (!verification.verification(name)) {
			return null;
		}

		try {
			return Class.forName(name, false,
					ClassUtils.getDefaultClassLoader());
		} catch (Throwable e) {
		}
		return null;
	}

	private static void appendJarClass(Collection<Class<?>> classList,
			JarFile jarFile, Verification<String> verification) {
		Enumeration<JarEntry> enumeration = jarFile.entries();
		while (enumeration.hasMoreElements()) {
			JarEntry jarEntry = enumeration.nextElement();
			if (jarEntry == null) {
				continue;
			}

			String name = jarEntry.getName();
			if (name.endsWith(".class")) {
				// class
				Class<?> clz = forFileNmae(name, verification);
				if (clz != null) {
					classList.add(clz);
				}
			}
		}
	}

	private static void appendDirectoryClass(String rootPackage,
			Collection<Class<?>> classList, File file,
			Verification<String> verification) {
		File[] files = file.listFiles();
		if (ArrayUtils.isEmpty(files)) {
			return;
		}

		for (File f : files) {
			if (f.isDirectory()) {
				appendDirectoryClass(
						StringUtils.isEmpty(rootPackage) ? f.getName() + "."
								: rootPackage + f.getName() + ".", classList,
						f, verification);
			} else {
				if (f.getName().endsWith(".class")) {
					String classFile = StringUtils.isEmpty(rootPackage) ? f
							.getName() : rootPackage + f.getName();
					Class<?> clz = forFileNmae(classFile, verification);
					if (clz != null) {
						classList.add(clz);
					}
				} else if (f.getName().endsWith(".jar")) {
					appendJarClass(f, classList, verification);
				}
			}
		}
	}

	private static void appendJarClass(File file,
			Collection<Class<?>> classList, Verification<String> verification) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(file);
			appendJarClass(classList, jarFile, verification);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.close(jarFile);
		}
	}

	public static Collection<Class<?>> getClassList(
			Verification<String> verification) {
		LinkedHashSet<Class<?>> list = new LinkedHashSet<Class<?>>();
		URL url = getClassPathURL();
		if (url != null) {
			File file = new File(url.getPath());
			if (file != null) {
				if (file.isFile()) {
					appendJarClass(file, list, verification);
				} else {
					appendDirectoryClass(null, list, file, verification);
				}
			}
		}

		for (String name : SystemPropertyUtils.getJavaClassPathArray()) {
			File file = new File(name);
			if (file.isFile()) {
				appendJarClass(file, list, verification);
			} else {
				appendDirectoryClass(null, list, file, verification);
			}
		}
		return list;
	}

	public static Collection<Class<?>> getClassList(String packagePrefix) {
		if (StringUtils.isEmpty(packagePrefix)) {
			return getClassList();
		}

		final String[] arr = StringUtils.commonSplit(packagePrefix);
		return getClassList(new Verification<String>() {

			public boolean verification(String data) {
				for (String name : arr) {
					if (data.startsWith(name)) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public static Collection<Class<?>> getClassList() {
		return getClassList(new DefaultClassNameVerification());
	}

	public static class DefaultClassNameVerification implements
			Verification<String> {
		public boolean verification(String name) {
			return !((name.startsWith("java.") || name.startsWith("javax.")
					|| name.startsWith("scw.core.")
					|| name.startsWith("scw.integration.")
					|| name.startsWith("org.apache.")
					|| name.startsWith("freemarker.")
					|| name.startsWith("com.alibaba.")
					|| name.startsWith("com.caucho.")
					|| name.startsWith("redis.clients.")
					|| name.startsWith("com.google.")
					|| name.startsWith("net.rubyeye.")
					|| name.startsWith("com.rabbitmq.")
					|| name.startsWith("com.esotericsoftware.")
					|| name.startsWith("com.zaxxer.")
					|| name.startsWith("support.")
					|| name.startsWith("com.oracle.")
					|| name.startsWith("com.sun.") || name.startsWith("jdk.")
					|| name.startsWith("org.w3c.")
					|| name.startsWith("org.omg.")
					|| name.startsWith("org.xml.")
					|| name.startsWith("org.jcp.")
					|| name.startsWith("org.ietf.") || name.startsWith("sun.")
					|| name.startsWith("oracle.")
					|| name.startsWith("netscape.")
					|| name.startsWith("junit.")
					|| name.startsWith("com.aliyun.")
					|| name.startsWith("mozilla.")
					|| name.startsWith("org.jdom")
					|| name.startsWith("org.codehaus.")
					|| name.startsWith("com.aliyuncs.")
					|| name.startsWith("org.json")
					|| name.startsWith("javassist.")
					|| name.startsWith("org.jboss")
					|| name.startsWith("org.I0Itec.")
					|| name.startsWith("common.") || name.startsWith("jxl.")
					|| name.startsWith("com.mysql.")
					|| name.startsWith("google.")
					|| name.startsWith("com.corundumstudio.")
					|| name.startsWith("io.netty.")
					|| name.startsWith("org.slf4j.")
					|| name.startsWith("org.fasterxml.")
					|| name.startsWith("com.fasterxml")
					|| name.startsWith("org.objectweb.")
					|| name.startsWith("lombok.")
					|| name.startsWith("com.zwitserloot.")
					|| name.startsWith("org.eclipse.")
					|| name.indexOf(".") == -1
					|| name.startsWith("scw.transaction.")
					|| name.startsWith("scw.id.") || name.startsWith("scw.io")
					|| name.startsWith("scw.application")
					|| name.startsWith("scw.beans.")
					|| name.startsWith("scw.data.")
					|| name.startsWith("scw.db.")
					|| name.startsWith("scw.logger.")
					|| name.startsWith("scw.mq.")
					|| name.startsWith("scw.net.")
					|| name.startsWith("scw.json.")
					|| name.startsWith("scw.locks.")
					|| name.startsWith("scw.login.")
					|| name.startsWith("scw.servlet.")
					|| name.startsWith("scw.testing.")
					|| name.startsWith("scw.sql.") || name
						.startsWith("scw.result.")));
		}
	}
}
