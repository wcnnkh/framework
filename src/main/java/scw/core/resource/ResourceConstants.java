package scw.core.resource;

public interface ResourceConstants {
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
	
	public static final String CLASS_SUFFIX = ".class";

	static final String CONFIG_SUFFIX = "SHUCHAOWEN_CONFIG_SUFFIX";
	static final String RESOURCE_SUFFIX = "scw_res_suffix";
	static final String CLASS_PATH_PREFIX_EL = "{classpath}";
	static final String CLASS_PATH_PREFIX_EL_2 = "{" + CLASSPATH_URL_PREFIX + "}";
	static final String CONFIG_NAME = "config";
}
