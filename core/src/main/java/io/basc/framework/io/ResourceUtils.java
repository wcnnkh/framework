package io.basc.framework.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

import io.basc.framework.io.resolver.PropertiesResolver;
import io.basc.framework.lang.NestedRuntimeException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ArrayUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.element.Elements;

/**
 * 资源工具
 * 
 * @author wcnnkh
 */
public final class ResourceUtils {
	private ResourceUtils() {
	};

	/** Pseudo URL prefix for loading from the class path: "classpath:". */
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	public static final String URL_PROTOCOL_RSRC = "rsrc";

	public static final String RSRC_URL_PREFIX = URL_PROTOCOL_RSRC + ":";

	/** URL protocol for a file in the file system: "file". */
	public static final String URL_PROTOCOL_FILE = "file";

	public static final String FILE_URL_PREFIX = URL_PROTOCOL_FILE + ":";

	/** URL protocol for an entry from a jar file: "jar". */
	public static final String URL_PROTOCOL_JAR = "jar";

	public static final String JAR_URL_PREFIX = URL_PROTOCOL_JAR + ":";

	/** URL protocol for an entry from a war file: "war". */
	public static final String URL_PROTOCOL_WAR = "war";

	/** URL prefix for loading from a war file on Tomcat: "war:". */
	public static final String WAR_URL_PREFIX = URL_PROTOCOL_WAR + ":";

	/** URL protocol for an entry from a zip file: "zip". */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/** URL protocol for an entry from a WebSphere jar file: "wsjar". */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";

	/** URL protocol for an entry from a JBoss jar file: "vfszip". */
	public static final String URL_PROTOCOL_VFSZIP = "vfszip";

	/** URL protocol for a JBoss file system resource: "vfsfile". */
	public static final String URL_PROTOCOL_VFSFILE = "vfsfile";

	/** URL protocol for a general JBoss VFS resource: "vfs". */
	public static final String URL_PROTOCOL_VFS = "vfs";

	/** File extension for a regular jar file: ".jar". */
	public static final String JAR_FILE_EXTENSION = ".jar";

	/** Separator between JAR URL and file path within the JAR: "!/". */
	public static final String JAR_URL_SEPARATOR = "!/";

	/** Special separator between WAR URL and jar part on Tomcat. */
	public static final String WAR_URL_SEPARATOR = "*/";

	public static final String META_INF_PREFIX = "META-INF/";

	public static final Resource NONEXISTENT_RESOURCE = new NonexistentResource();

	/**
	 * 因为eclipse默认打包为可执行jar会将资源打包在resources目录下，所以会尝试在此目录下查找资源
	 * 
	 * @see #getResource(Class, String)
	 * @see #getResource(ClassLoader, String)
	 * @see #getResourceAsStream(Class, String)
	 * @see #getResourceAsStream(ClassLoader, String)
	 * @see #getResources(ClassLoader, String)
	 * @see #getClassLoaderResources(String)
	 * @see #getClassLoaderResource(String)
	 * @see #getClassLoaderResourceAsStream(String)
	 */
	private static final String[] RESOURCE_PREFIXS;

	static {
		String prefixs = System.getProperty("io.basc.framework.resource.prefixs");
		String[] resourcePrefixs = new String[] { "resources/" };
		if (StringUtils.isNotEmpty(prefixs)) {
			String[] array = StringUtils.splitToArray(prefixs);
			if (array != null && array.length != 0) {
				resourcePrefixs = ArrayUtils.merge(array, resourcePrefixs);
			}
		}

		for (int i = 0; i < resourcePrefixs.length; i++) {
			String prefix = resourcePrefixs[i];
			while (prefix.endsWith("/")) {
				prefix = prefix.substring(0, prefix.length() - 2);
			}

			if (prefix.length() > 0) {
				prefix = prefix + "/";
			}

			resourcePrefixs[i] = prefix;
		}
		RESOURCE_PREFIXS = resourcePrefixs;
	}

	/**
	 * Resolve the given resource location to a {@code java.net.URL}.
	 * <p>
	 * Does not check whether the URL actually exists; simply returns the URL that
	 * the given location would correspond to.
	 * 
	 * @param resourceLocation the resource location to resolve: either a
	 *                         "classpath:" pseudo URL, a "file:" URL, or a plain
	 *                         file path
	 * @return a corresponding URL object
	 * @throws FileNotFoundException if the resource cannot be resolved to a URL
	 */
	public static URL getURL(String resourceLocation) throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
			ClassLoader cl = ClassUtils.getDefaultClassLoader();
			URL url = (cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path));
			if (url == null) {
				String description = "class path resource [" + path + "]";
				throw new FileNotFoundException(description + " cannot be resolved to URL because it does not exist");
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
				throw new FileNotFoundException(
						"Resource location [" + resourceLocation + "] is neither a URL not a well-formed file path");
			}
		}
	}

	/**
	 * Resolve the given resource location to a {@code java.io.File}, i.e. to a file
	 * in the file system.
	 * <p>
	 * Does not check whether the file actually exists; simply returns the File that
	 * the given location would correspond to.
	 * 
	 * @param resourceLocation the resource location to resolve: either a
	 *                         "classpath:" pseudo URL, a "file:" URL, or a plain
	 *                         file path
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the resource cannot be resolved to a file in
	 *                               the file system
	 */
	public static File getFile(String resourceLocation) throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
			String description = "class path resource [" + path + "]";
			ClassLoader cl = ClassUtils.getDefaultClassLoader();
			URL url = (cl != null ? cl.getResource(path) : ClassLoader.getSystemResource(path));
			if (url == null) {
				throw new FileNotFoundException(
						description + " cannot be resolved to absolute file path because it does not exist");
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
	 * Resolve the given resource URL to a {@code java.io.File}, i.e. to a file in
	 * the file system.
	 * 
	 * @param resourceUrl the resource URL to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to a file in the
	 *                               file system
	 */
	public static File getFile(URL resourceUrl) throws FileNotFoundException {
		return getFile(resourceUrl, "URL");
	}

	/**
	 * Resolve the given resource URL to a {@code java.io.File}, i.e. to a file in
	 * the file system.
	 * 
	 * @param resourceUrl the resource URL to resolve
	 * @param description a description of the original resource that the URL was
	 *                    created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to a file in the
	 *                               file system
	 */
	public static File getFile(URL resourceUrl, String description) throws FileNotFoundException {
		Assert.notNull(resourceUrl, "Resource URL must not be null");
		if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(description + " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: " + resourceUrl);
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
	 * Resolve the given resource URI to a {@code java.io.File}, i.e. to a file in
	 * the file system.
	 * 
	 * @param resourceUri the resource URI to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to a file in the
	 *                               file system
	 */
	public static File getFile(URI resourceUri) throws FileNotFoundException {
		return getFile(resourceUri, "URI");
	}

	/**
	 * Resolve the given resource URI to a {@code java.io.File}, i.e. to a file in
	 * the file system.
	 * 
	 * @param resourceUri the resource URI to resolve
	 * @param description a description of the original resource that the URI was
	 *                    created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the URL cannot be resolved to a file in the
	 *                               file system
	 */
	public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
		Assert.notNull(resourceUri, "Resource URI must not be null");
		if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
			throw new FileNotFoundException(description + " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: " + resourceUri);
		}
		return new File(resourceUri.getSchemeSpecificPart());
	}

	/**
	 * Determine whether the given URL points to a resource in the file system, i.e.
	 * has protocol "file", "vfsfile" or "vfs".
	 * 
	 * @param url the URL to check
	 * @return whether the URL has been identified as a file system URL
	 */
	public static boolean isFileURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_FILE.equals(protocol) || URL_PROTOCOL_VFSFILE.equals(protocol)
				|| URL_PROTOCOL_VFS.equals(protocol));
	}

	/**
	 * Determine whether the given URL points to a resource in a jar file. i.e. has
	 * protocol "jar", "war, ""zip", "vfszip" or "wsjar".
	 * 
	 * @param url the URL to check
	 * @return whether the URL has been identified as a JAR URL
	 */
	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_WAR.equals(protocol)
				|| URL_PROTOCOL_ZIP.equals(protocol) || URL_PROTOCOL_VFSZIP.equals(protocol)
				|| URL_PROTOCOL_WSJAR.equals(protocol));
	}

	/**
	 * Determine whether the given URL points to a jar file itself, that is, has
	 * protocol "file" and ends with the ".jar" extension.
	 * 
	 * @param url the URL to check
	 * @return whether the URL has been identified as a JAR file URL
	 */
	public static boolean isJarFileURL(URL url) {
		return (URL_PROTOCOL_FILE.equals(url.getProtocol())
				&& url.getPath().toLowerCase().endsWith(JAR_FILE_EXTENSION));
	}

	/**
	 * Extract the URL for the actual jar file from the given URL (which may point
	 * to a resource in a jar file or to a jar file itself).
	 * 
	 * @param jarUrl the original URL
	 * @return the URL for the actual jar file
	 * @throws MalformedURLException if no valid jar file URL could be extracted
	 */
	public static URL extractJarFileURL(URL jarUrl) throws MalformedURLException {
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
	 * Extract the URL for the outermost archive from the given jar/war URL (which
	 * may point to a resource in a jar file or to a jar file itself).
	 * <p>
	 * In the case of a jar file nested within a war file, this will return a URL to
	 * the war file since that is the one resolvable in the file system.
	 * 
	 * @param jarUrl the original URL
	 * @return the URL for the actual jar file
	 * @throws MalformedURLException if no valid jar file URL could be extracted
	 * @see #extractJarFileURL(URL)
	 */
	public static URL extractArchiveURL(URL jarUrl) throws MalformedURLException {
		String urlFile = jarUrl.getFile();

		int endIndex = urlFile.indexOf(WAR_URL_SEPARATOR);
		if (endIndex != -1) {
			// Tomcat's
			// "war:file:...mywar.war*/WEB-INF/lib/myjar.jar!/myentry.txt"
			String warFile = urlFile.substring(0, endIndex);
			if (URL_PROTOCOL_WAR.equals(jarUrl.getProtocol())) {
				return new URL(warFile);
			}
			int startIndex = warFile.indexOf(WAR_URL_PREFIX);
			if (startIndex != -1) {
				return new URL(warFile.substring(startIndex + WAR_URL_PREFIX.length()));
			}
		}

		// Regular "jar:file:...myjar.jar!/myentry.txt"
		return extractJarFileURL(jarUrl);
	}

	/**
	 * Create a URI instance for the given URL, replacing spaces with "%20" URI
	 * encoding first.
	 * 
	 * @param url the URL to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the URL wasn't a valid URI
	 * @see java.net.URL#toURI()
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}

	/**
	 * Create a URI instance for the given location String, replacing spaces with
	 * "%20" URI encoding first.
	 * 
	 * @param location the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException if the location wasn't a valid URI
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(StringUtils.replace(location, " ", "%20"));
	}

	/**
	 * Set the {@link URLConnection#setUseCaches "useCaches"} flag on the given
	 * connection, preferring {@code false} but leaving the flag at {@code true} for
	 * JNLP based resources.
	 * 
	 * @param con the URLConnection to set the flag on
	 */
	public static void useCachesIfNecessary(URLConnection con) {
		con.setUseCaches(con.getClass().getSimpleName().startsWith("JNLP"));
	}

	private static String cleanClassLoaderResourceName(String name) {
		String nameToUse = name;
		while (nameToUse.length() != 0 && nameToUse.startsWith("/")) {
			nameToUse = nameToUse.substring(1);
		}
		return nameToUse;
	}

	public static URL getResource(ClassLoader classLoader, String name) {
		String nameToUse = cleanClassLoaderResourceName(name);
		URL url = classLoader.getResource(nameToUse);
		if (url == null) {
			for (String prefix : RESOURCE_PREFIXS) {
				if (nameToUse.startsWith(prefix)) {
					continue;
				}

				url = classLoader.getResource(prefix + nameToUse);
				if (url != null) {
					break;
				}
			}
		}
		return url;
	}

	public static URL getResource(Class<?> clazz, String name) {
		if (clazz == null || name == null) {
			return null;
		}

		String nameToUse = cleanClassLoaderResourceName(name);
		URL url = clazz.getResource(nameToUse);
		if (url == null) {
			for (String prefix : RESOURCE_PREFIXS) {
				if (nameToUse.startsWith(prefix)) {
					continue;
				}

				url = clazz.getResource(prefix + nameToUse);
				if (url != null) {
					break;
				}
			}
		}

		if (url == null) {
			url = getResource(clazz.getClassLoader(), nameToUse);
		}
		return url;
	}

	public static InputStream getResourceAsStream(ClassLoader classLoader, String name) {
		if (classLoader == null || name == null) {
			return null;
		}

		String nameToUse = cleanClassLoaderResourceName(name);
		InputStream is = classLoader.getResourceAsStream(nameToUse);
		if (is == null) {
			for (String prefix : RESOURCE_PREFIXS) {
				if (nameToUse.startsWith(prefix)) {
					continue;
				}

				is = classLoader.getResourceAsStream(prefix + nameToUse);
				if (is != null) {
					break;
				}
			}
		}
		return is;
	}

	public static InputStream getResourceAsStream(Class<?> clazz, String name) {
		if (clazz == null || name == null) {
			return null;
		}

		String nameToUse = cleanClassLoaderResourceName(name);
		InputStream is = clazz.getResourceAsStream(nameToUse);
		if (is == null) {
			for (String prefix : RESOURCE_PREFIXS) {
				if (nameToUse.startsWith(prefix)) {
					continue;
				}

				is = clazz.getResourceAsStream(prefix + nameToUse);
				if (is != null) {
					break;
				}
			}
		}

		if (is == null) {
			is = getResourceAsStream(clazz.getClassLoader(), nameToUse);
		}
		return is;
	}

	/**
	 * @param classLoader
	 * @param name
	 * @see Enumeration#hasMoreElements()
	 * @throws IOException
	 */
	public static Enumeration<URL> getResources(ClassLoader classLoader, String name) throws IOException {
		if (classLoader == null || name == null) {
			return Collections.emptyEnumeration();
		}

		String nameToUse = cleanClassLoaderResourceName(name);
		Enumeration<URL> urls = classLoader.getResources(nameToUse);
		if (urls == null || !urls.hasMoreElements()) {
			for (String prefix : RESOURCE_PREFIXS) {
				if (nameToUse.startsWith(prefix)) {
					continue;
				}

				urls = classLoader.getResources(prefix + nameToUse);
				if (urls != null && urls.hasMoreElements()) {
					break;
				}
			}
		}

		if (urls == null) {
			return Collections.emptyEnumeration();
		}
		return urls;
	}

	public static URL getSystemResource(@Nullable ClassLoader classLoader, String name) {
		if (name == null) {
			return null;
		}

		URL url = getResource(classLoader, name);
		if (url == null) {
			String nameToUse = cleanClassLoaderResourceName(name);
			url = ClassLoader.getSystemResource(nameToUse);
			if (url == null) {
				for (String prefix : RESOURCE_PREFIXS) {
					if (nameToUse.startsWith(prefix)) {
						continue;
					}

					url = ClassLoader.getSystemResource(prefix + nameToUse);
					if (url != null) {
						break;
					}
				}
			}
		}
		return url;
	}

	/**
	 * @param classLoader
	 * @param name
	 * @see Enumeration#hasMoreElements()
	 * @throws IOException
	 */
	public static Enumeration<URL> getSystemResources(@Nullable ClassLoader classLoader, String name)
			throws IOException {
		if (name == null) {
			return Collections.emptyEnumeration();
		}

		Enumeration<URL> urls = getResources(classLoader, name);
		if (urls == null || !urls.hasMoreElements()) {
			String nameToUse = cleanClassLoaderResourceName(name);
			urls = ClassLoader.getSystemResources(nameToUse);
			if (urls == null || !urls.hasMoreElements()) {
				for (String prefix : RESOURCE_PREFIXS) {
					if (nameToUse.startsWith(prefix)) {
						continue;
					}

					urls = ClassLoader.getSystemResources(prefix + nameToUse);
					if (urls != null && urls.hasMoreElements()) {
						break;
					}
				}
			}
		}

		if (urls == null) {
			return Collections.emptyEnumeration();
		}

		return urls;
	}

	public static InputStream getSystemResourceAsStream(@Nullable ClassLoader classLoader, String name) {
		if (name == null) {
			return null;
		}

		InputStream is = getResourceAsStream(classLoader, name);
		if (is == null) {
			String nameToUse = cleanClassLoaderResourceName(name);
			is = ClassLoader.getSystemResourceAsStream(nameToUse);
			if (is == null) {
				for (String prefix : RESOURCE_PREFIXS) {
					if (nameToUse.startsWith(prefix)) {
						continue;
					}

					is = ClassLoader.getSystemResourceAsStream(prefix + nameToUse);
					if (is != null) {
						break;
					}
				}
			}
		}
		return is;
	}

	public static Resource getSystemResource(String name) {
		URL url = getSystemResource(ClassUtils.getDefaultClassLoader(), name);
		return url == null ? null : new UrlResource(url);
	}

	public static Resource[] getSystemResources(String name) throws IOException {
		Enumeration<URL> urls = getSystemResources(ClassUtils.getDefaultClassLoader(), name);
		List<Resource> resources = toUrlResources(urls);
		return resources.toArray(new Resource[0]);
	}

	public static List<Resource> toUrlResources(Enumeration<URL> urls) {
		if (urls == null || !urls.hasMoreElements()) {
			return Collections.emptyList();
		}

		List<Resource> list = new ArrayList<Resource>(8);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			if (url == null) {
				continue;
			}

			list.add(new UrlResource(url));
		}
		return list;
	}

	public static Elements<String> readLines(Resource resource, String charsetName) {
		if (resource == null || !resource.exists()) {
			return Elements.empty();
		}

		return Elements.of(() -> {
			InputStream is;
			try {
				is = resource.getInputStream();
				return IOUtils.readLines(is, charsetName).onClose(() -> {
					if (is != null && !resource.isOpen()) {
						IOUtils.closeQuietly(is);
					}
				});
			} catch (IOException ignore) {
			}
			return Stream.empty();
		});
	}

	public static Elements<String> readLines(Resource resource, Charset charset) {
		return readLines(resource, charset.name());
	}

	@Nullable
	public static String getContent(Resource resource, String charsetName) {
		if (resource == null || !resource.exists()) {
			return null;
		}

		InputStream is = null;
		try {
			is = resource.getInputStream();
			return IOUtils.readContent(is, charsetName);
		} catch (IOException e) {
			throw new NestedRuntimeException(resource.getDescription(), e);
		} finally {
			if (!resource.isOpen()) {
				IOUtils.closeQuietly(is);
			}
		}
	}

	@Nullable
	public static String getContent(Resource resource, Charset charset) {
		return getContent(resource, charset.name());
	}

	@Nullable
	public static byte[] getBytes(Resource resource) {
		if (resource == null || !resource.exists()) {
			return null;
		}

		InputStream is = null;
		try {
			is = resource.getInputStream();
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			throw new NestedRuntimeException(resource.getDescription(), e);
		} finally {
			if (!resource.isOpen()) {
				IOUtils.closeQuietly(is);
			}
		}
	}

	@Nullable
	public static UnsafeByteArrayInputStream getByteArrayInputStream(Resource resource) {
		byte[] data = getBytes(resource);
		if (data == null) {
			return null;
		}

		return new UnsafeByteArrayInputStream(data);
	}

	public static Function<Resource, Properties> toPropertiesConverter(PropertiesResolver propertiesResolver) {
		return toPropertiesConverter(propertiesResolver, null);
	}

	public static Function<Resource, Properties> toPropertiesConverter(PropertiesResolver propertiesResolver,
			Charset charset) {
		return (o) -> {
			Properties properties = new Properties();
			propertiesResolver.resolveProperties(properties, o, charset);
			return properties;
		};
	}
}
