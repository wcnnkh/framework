package scw.env;

import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.instance.support.DefaultServiceLoaderFactory;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.logger.LoggerLevelManager;
import scw.util.EnumerationConvert;
import scw.util.MultiIterator;
import scw.util.XUtils;
import scw.value.StringValue;
import scw.value.Value;

public final class SystemEnvironment extends DefaultEnvironment {
	private static Logger logger = LoggerFactory.getLogger(SystemEnvironment.class);
	public static final String PROPERTY_MAVEN_HOME = "maven.home";
	public static final String PROPERTY_PATH_SEPARATOR = "path.separator";
	public static final String PROPERTY_JAVA_CLASS_PATH = "java.class.path";
	public static final String PROPERTY_JAVA_IO_TMPDIR = "java.io.tmpdir";
	public static final String PROPERTY_USER_HOME = "user.home";
	public static final String PROPERTY_OS_NAME = "os.name";
	public static final String PROPERTY_USER_DIR = "user.dir";
	/**
	 * 为了兼容老版本
	 */
	public static final String WEB_ROOT_PROPERTY = "web.root";
	
	private static SystemEnvironment instance = new SystemEnvironment();
	
	static{
		instance.loadProperties("system.properties").register();
		instance.loadProperties(instance.getValue("system.properties.location", String.class, "/private.properties")).register();
		instance.loadServices(new DefaultServiceLoaderFactory(instance), logger);
		//初始化日志管理器
		LoggerLevelManager.getInstance();
	}

	public static SystemEnvironment getInstance() {
		return instance;
	}

	private SystemEnvironment() {
		super(true);
	};

	public Iterator<String> iterator() {
		return new MultiIterator<String>(super.iterator(),
				CollectionUtils
						.toIterator(EnumerationConvert.convertToStringEnumeration(System.getProperties().keys())),
				System.getenv().keySet().iterator());
	}
	
	public Value getValue(String key) {
		Value value = super.getValue(key);
		if(value != null){
			return value;
		}
		
		String v = getSystemProperty(key);
		return v == null? null:new StringValue(v);
	}
	
	public String getSystemProperty(String key){
		String value = System.getProperty(key);
		if (value == null) {
			value = System.getenv(key);
		}
		
		if(value == null && WEB_ROOT_PROPERTY.equals(key)){
			value = getWorkPath();
		}
		return value;
	}
	
	@Override
	public boolean containsKey(String key) {
		if(super.containsKey(key)){
			return true;
		}
		
		return getSystemProperty(key) != null;
	}

	/**
	 * 获取环境变量分割符
	 * 
	 * @return
	 */
	public String getPathSeparator() {
		return getString(PROPERTY_PATH_SEPARATOR);
	}

	public String getJavaClassPath() {
		return getString(PROPERTY_JAVA_CLASS_PATH);
	}

	public String[] getJavaClassPathArray() {
		String classPath = getJavaClassPath();
		if (StringUtils.isEmpty(classPath)) {
			return null;
		}

		return StringUtils.split(classPath, getPathSeparator());
	}

	public String getMavenHome() {
		return getString(PROPERTY_MAVEN_HOME);
	}

	public String getTempDirectoryPath() {
		return getString(PROPERTY_JAVA_IO_TMPDIR);
	}

	public String getUserHome() {
		return getString(PROPERTY_USER_HOME);
	}

	public String getOSName() {
		return getString(PROPERTY_OS_NAME);
	}
	
	public String getUserDir() {
		return getString(PROPERTY_USER_DIR);
	}
	
	/**
	 * 获取工作目录
	 */
	@Override
	public String getWorkPath() {
		String path = super.getWorkPath();
		if(path == null){
			path = XUtils.getWebAppDirectory(getClassLoader());
			if(path != null){
				setWorkPath(path);
				logger.info("default " + WORK_PATH_PROPERTY + " in " + path);
			}
		}
		return path;
	}
}
