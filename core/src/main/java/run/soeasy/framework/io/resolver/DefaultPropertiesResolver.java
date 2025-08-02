package run.soeasy.framework.io.resolver;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.domain.CharsetCapable;
import run.soeasy.framework.core.type.ReflectionUtils;
import run.soeasy.framework.io.Resource;
import run.soeasy.framework.logging.LogManager;
import run.soeasy.framework.logging.Logger;

/**
 * 默认属性文件解析器，继承自{@link ConfigurablePropertiesResolver}，
 * 实现{@link PropertiesResolver}接口，提供对.properties和.xml格式属性文件的解析能力。
 * 
 * <p>
 * 该解析器支持以下特性：
 * <ul>
 * <li>单例模式：通过{@link #getInstance()}获取唯一实例</li>
 * <li>格式自动识别：根据文件名后缀（.properties/.xml）选择解析方式</li>
 * <li>字符集支持：从资源中获取字符集信息（通过{@link CharsetCapable}）</li>
 * <li>反射兼容：通过反射调用Properties的load方法以支持JDK版本兼容性</li>
 * </ul>
 * 
 * <p>
 * <b>解析策略：</b>
 * <ul>
 * <li>.xml文件：使用{@link Properties#loadFromXML(InputStream)}/storeToXML</li>
 * <li>.properties文件：使用load/store方法，自动适配JDK版本</li>
 * <li>其他格式：先尝试父类解析，不支持时返回false</li>
 * </ul>
 * 
 * @author soeasy.run
 * @see PropertiesResolver
 * @see ConfigurablePropertiesResolver
 * @see Properties
 */
public class DefaultPropertiesResolver extends ConfigurablePropertiesResolver {
	private static Logger logger = LogManager.getLogger(DefaultPropertiesResolver.class.getName());
	/** 反射获取的Properties.load(Reader)方法，用于JDK版本兼容 */
	private static final Method LOAD_METHOD = ReflectionUtils.findDeclaredMethod(Properties.class, "load", Reader.class)
			.first();

	private static volatile DefaultPropertiesResolver instance;

	/**
	 * 获取默认属性解析器单例实例。
	 * <p>
	 * 实现双重检查锁定的单例模式，确保线程安全且延迟初始化。
	 * 
	 * @return 默认属性解析器实例
	 */
	public static DefaultPropertiesResolver getInstance() {
		if (instance == null) {
			synchronized (DefaultPropertiesResolver.class) {
				if (instance == null) {
					instance = new DefaultPropertiesResolver();
				}
			}
		}
		return instance;
	}

	/**
	 * 判断资源是否可被解析（覆盖父类实现）。
	 * <p>
	 * 优先调用父类检测，不支持时检查资源是否存在且为.properties后缀。
	 * 
	 * @param resource 待检测资源
	 * @return true表示可解析（.properties/.xml或父类支持）
	 */
	@Override
	public boolean canResolveProperties(Resource resource) {
		if (super.canResolveProperties(resource)) {
			return true;
		}

		if (!resource.exists()) {
			return false;
		}

		return StringUtils.endsWithIgnoreCase(resource.getName(), ".properties")
				|| StringUtils.endsWithIgnoreCase(resource.getName(), ".xml");
	}

	/**
	 * 从资源解析属性到Properties对象（覆盖父类实现）。
	 * <p>
	 * 根据资源类型选择解析方式：
	 * <ul>
	 * <li>.xml：调用loadFromXML</li>
	 * <li>.properties：通过反射调用load(Reader)以支持字符集</li>
	 * <li>其他：委托父类处理</li>
	 * </ul>
	 * 
	 * @param properties 目标Properties对象
	 * @param resource   源资源
	 * @throws IOException                      读取资源失败
	 * @throws InvalidPropertiesFormatException XML格式错误
	 */
	@Override
	public void resolveProperties(Properties properties, Resource resource)
			throws IOException, InvalidPropertiesFormatException {
		if (super.canResolveProperties(resource)) {
			super.resolveProperties(properties, resource);
			return;
		}

		if (!resource.exists()) {
			return;
		}

		if (StringUtils.endsWithIgnoreCase(resource.getName(), ".xml")) {
			resource.getInputStreamPipeline().optional().ifPresent((is) -> properties.loadFromXML(is));
		} else {
			if (LOAD_METHOD == null) {
				logger.warn(
						"The specified character set is only supported in versions of jdk 1.6 and above: " + resource);
				resource.getInputStreamPipeline().optional().ifPresent((is) -> properties.load(is));
			} else {
				resource.getReaderPipeline().optional()
						.ifPresent((is) -> ReflectionUtils.invoke(LOAD_METHOD, properties, is));
			}
		}
	}

	/**
	 * 将Properties持久化到资源（覆盖父类实现）。
	 * <p>
	 * 根据资源类型选择存储方式：
	 * <ul>
	 * <li>.xml：调用storeToXML并使用资源字符集</li>
	 * <li>.properties：调用store</li>
	 * <li>其他：委托父类处理</li>
	 * </ul>
	 * 
	 * @param properties 源Properties对象
	 * @param resource   目标资源
	 * @throws IOException 写入资源失败
	 */
	@Override
	public void persistenceProperties(@NonNull Properties properties, @NonNull Resource resource) throws IOException {
		if (super.canResolveProperties(resource)) {
			super.persistenceProperties(properties, resource);
			return;
		}

		if (StringUtils.endsWithIgnoreCase(resource.getName(), ".xml")) {
			String charsetName = CharsetCapable.getCharsetName(resource);
			resource.getOutputStreamPipeline().optional().ifPresent((os) -> {
				properties.storeToXML(os, null, charsetName);
			});
		} else {
			resource.getWriterPipeline().optional().ifPresent((os) -> {
				properties.store(os, null);
			});
		}
	}
}