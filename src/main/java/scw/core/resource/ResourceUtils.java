/*
\ * Copyright 2002-2012 the original author or authors.
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

package scw.core.resource;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.Assert;
import scw.core.Consumer;
import scw.core.Converter;
import scw.core.PropertyFactory;
import scw.core.SystemPropertyFactory;
import scw.core.Verification;
import scw.core.exception.NotFoundException;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.FormatUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.io.IOUtils;

/**
 * 资源工具
 * 
 * @author scw
 */
public abstract class ResourceUtils implements ResourceConstants {
	private static final ResourceLookup RESOURCE_LOOKUP = new DefaultResourceLookup();

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
	public static URL getURL(String resourceLocation) throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
			URL url = ClassUtils.getDefaultClassLoader().getResource(path);
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
	public static File getFile(String resourceLocation) throws FileNotFoundException {
		Assert.notNull(resourceLocation, "Resource location must not be null");
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
			String description = "class path resource [" + path + "]";
			URL url = ClassUtils.getDefaultClassLoader().getResource(path);
			if (url == null) {
				throw new FileNotFoundException(description + " cannot be resolved to absolute file path "
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
	public static File getFile(URI resourceUri, String description) throws FileNotFoundException {
		Assert.notNull(resourceUri, "Resource URI must not be null");
		if (!URL_PROTOCOL_FILE.equals(resourceUri.getScheme())) {
			throw new FileNotFoundException(description + " cannot be resolved to absolute file path "
					+ "because it does not reside in the file system: " + resourceUri);
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
		return (URL_PROTOCOL_FILE.equals(protocol) || protocol.startsWith(URL_PROTOCOL_VFS));
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
		return (URL_PROTOCOL_JAR.equals(protocol) || URL_PROTOCOL_ZIP.equals(protocol)
				|| URL_PROTOCOL_WSJAR.equals(protocol)
				|| (URL_PROTOCOL_CODE_SOURCE.equals(protocol) && url.getPath().contains(JAR_URL_SEPARATOR)));
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
			ProtectionDomain protectionDomain = ResourceUtils.class.getProtectionDomain();
			if (protectionDomain != null) {
				CodeSource codeSource = protectionDomain.getCodeSource();
				if (codeSource != null) {
					url = codeSource.getLocation();
				}
			}
		}
		return url;
	}

	public static void setResourceSuffix(String suffix) {
		if (StringUtils.isEmpty(suffix)) {
			return;
		}

		SystemPropertyUtils.setPrivateProperty(RESOURCE_SUFFIX, suffix);
	}

	private static String getTestFileName(String fileName, String str) {
		int index = fileName.lastIndexOf(".");
		if (index == -1) {// 不存在
			return fileName + str;
		} else {
			return fileName.substring(0, index) + str + fileName.substring(index);
		}
	}

	public static List<String> getResourceNameList(String resourceName) {
		String value = SystemPropertyUtils.getProperty(RESOURCE_SUFFIX);
		if (value == null) {
			value = SystemPropertyUtils.getProperty(CONFIG_SUFFIX);
		}

		if (value == null) {
			return Arrays.asList(resourceName);
		}

		List<String> list = new LinkedList<String>();
		String[] arr = StringUtils.commonSplit(value);
		for (String name : arr) {
			list.add(getTestFileName(resourceName, name));
		}
		list.add(resourceName);
		return list;
	}

	public static void consumterInputStream(String resource, Consumer<InputStream> consumer) {
		consumterInputStream(resource, consumer, true);
	}

	public static void consumterInputStream(String resource, Consumer<InputStream> consumer, boolean multiple) {
		if (multiple) {
			Collection<String> resourceNames = getResourceNameList(resource);
			for (String name : resourceNames) {
				if (RESOURCE_LOOKUP.lookup(name, consumer)) {
					return;
				}
			}
		} else if (RESOURCE_LOOKUP.lookup(resource, consumer)) {
			return;
		}

		throw new NotFoundException(resource);
	}

	public static boolean isExist(String resource) {
		return isExist(resource, true);
	}

	public static boolean isExist(String resource, boolean multiple) {
		if (StringUtils.isEmpty(resource)) {
			return false;
		}

		if (multiple) {
			Collection<String> resourceNames = getResourceNameList(resource);
			for (String name : resourceNames) {
				if (RESOURCE_LOOKUP.lookup(name)) {
					return true;
				}
			}
			return false;
		} else {
			return RESOURCE_LOOKUP.lookup(resource);
		}
	}

	public static <T> T getResource(String resource, final Converter<InputStream, T> converter) {
		return RESOURCE_LOOKUP.getResource(resource, converter);
	}

	public static Collection<Class<?>> getClassList(String packagePrefix) {
		return RESOURCE_LOOKUP.getClasses(packagePrefix);
	}

	public static Collection<Class<?>> getClassList() {
		return RESOURCE_LOOKUP.getClasses();
	}

	public static Properties getProperties(final String resource, final String charsetName,
			PropertyFactory propertyFactory) {
		List<String> resourceNameList = getResourceNameList(resource);
		ListIterator<String> iterator = resourceNameList.listIterator(resourceNameList.size());
		Properties properties = new Properties();
		while (iterator.hasPrevious()) {
			final String name = iterator.previous();
			if (isExist(name, false)) {
				RESOURCE_LOOKUP.lookup(name, new LoadPropertiesConsumer(properties, name, charsetName));
			}
		}

		if (propertyFactory == null) {
			return properties;
		}

		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			entry.setValue(FormatUtils.format(value.toString(), propertyFactory, true));
		}
		return properties;
	}

	public static Properties getProperties(final String path) {
		return getProperties(path, (String) null);
	}

	public static Properties getProperties(final String path, final String charsetName) {
		return getProperties(path, charsetName, SystemPropertyFactory.INSTANCE);
	}

	public static Properties getProperties(final String path, PropertyFactory propertyFactory) {
		return getProperties(path, null, propertyFactory);
	}

	public static List<String> getFileContentLineList(String path, final String charsetName) {
		return ResourceUtils.getResource(path, new Converter<InputStream, List<String>>() {

			public List<String> convert(InputStream inputStream) {
				try {
					return IOUtils.readLines(inputStream, charsetName);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	public static String getFileContent(String path, final String charsetName) {
		return ResourceUtils.getResource(path, new Converter<InputStream, String>() {

			public String convert(InputStream inputStream) {
				return IOUtils.readContent(inputStream, charsetName);
			}
		});
	}

	public static Collection<Class<?>> getClasses(String packageName, Verification<Class<?>> ignoreClass) {
		LinkedList<Class<?>> interfaceClassList = new LinkedList<Class<?>>();
		Collection<Class<?>> clazzList = RESOURCE_LOOKUP.getClasses(packageName);
		if (!CollectionUtils.isEmpty(clazzList)) {
			for (Class<?> clazz : clazzList) {
				if (clazz == null) {
					continue;
				}

				if (ignoreClass != null && ignoreClass.verification(clazz)) {
					continue;
				}

				interfaceClassList.add(clazz);
			}
		}
		return interfaceClassList;
	}

	public static Collection<Class<?>> getClasses(String packageName, boolean interfaceClass,
			boolean ignoreEmptyMethod) {
		return getClasses(packageName, new IgnoreClassVerification(ignoreEmptyMethod, interfaceClass));
	}
}
