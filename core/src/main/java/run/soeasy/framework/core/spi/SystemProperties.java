package run.soeasy.framework.core.spi;

import java.util.Properties;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.mapping.property.MapMapping;
import run.soeasy.framework.core.mapping.property.PropertyAccessor;
import run.soeasy.framework.core.mapping.property.PropertyMapping;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 系统属性/环境变量的属性映射实现（参考MapMapping结构）
 * 
 * @author soeasy.run
 * @see PropertyMapping
 * @see MapMapping
 */
@Getter
public final class SystemProperties implements PropertyMapping<PropertyAccessor> {
	/** 单例实例（参考MapMapping无构造参数，适配单例） */
	private static volatile SystemProperties instance;

	/** 类型转换器，用于属性值类型转换（参考MapMapping） */
	private Converter converter = SystemConversionService.getInstance();

	// ------------------------------ 核心结构（参考MapMapping）
	// ------------------------------
	/** 私有构造（单例，参考MapMapping的构造风格但无需入参） */
	private SystemProperties() {
	}

	/** 获取单例（适配系统属性全局唯一的特性） */
	public static SystemProperties getInstance() {
		if (instance == null) {
			synchronized (SystemProperties.class) {
				if (instance == null) {
					instance = new SystemProperties();
				}
			}
		}
		return instance;
	}

	// ------------------------------ PropertyMapping 核心方法（严格参考MapMapping）
	// ------------------------------
	@Override
	public Streamable<PropertyAccessor> elements() {
		// 参考MapMapping：遍历key集合 → 创建对应Accessor
		Streamable<String> systemKeys = Streamable.of(System.getProperties().stringPropertyNames());
		Streamable<String> envKeys = Streamable.of(System.getenv().keySet())
				.filter(key -> !System.getProperties().containsKey(key));
		return systemKeys.concat(envKeys).map(this::createAccessor);
	}

	@Override
	public boolean hasKey(String key) {
		return System.getProperties().contains(key) || System.getenv().containsKey(key);
	}

	@Override
	public Streamable<PropertyAccessor> getValues(@NonNull String key) {
		return Streamable.singleton(createAccessor(key));
	}

	@Override
	public boolean isMapped() {
		return true;
	}

	// ------------------------------ 私有方法（参考MapMapping的createAccessor）
	// ------------------------------
	/**
	 * 创建系统属性/环境变量访问器（参考MapMapping.createAccessor）
	 * 
	 * @param key 属性名
	 * @return 对应的PropertyAccessor
	 */
	private PropertyAccessor createAccessor(String key) {
		return new SystemPropertyAccessor(key);
	}

	// ------------------------------ 内部Accessor（参考MapPropertyAccessor）
	// ------------------------------
	/**
	 * 系统属性访问器（参考MapPropertyAccessor结构）
	 */
	@Getter
	private class SystemPropertyAccessor implements PropertyAccessor {
		@NonNull
		private final String name;

		public SystemPropertyAccessor(@NonNull String name) {
			this.name = name;
		}

		@Override
		public void set(Object source) {
			// 系统属性可修改，环境变量只读
			Properties systemProps = System.getProperties();
			if (!systemProps.containsKey(name) && System.getenv().containsKey(name)) {
				throw new UnsupportedOperationException("Environment variable is read-only: " + name);
			}
			String value = (String) converter.convert(source, TypeDescriptor.forObject(source),
					TypeDescriptor.valueOf(String.class));
			System.setProperty(name, value);
		}

		@Override
		public Object get() {
			// 优先读系统属性，再读环境变量
			String value = System.getProperty(name);
			return value != null ? value : System.getenv(name);
		}

		@Override
		public boolean isReadable() {
			return get() != null;
		}

		@Override
		public TypeDescriptor getReturnTypeDescriptor() {
			return TypeDescriptor.valueOf(String.class);
		}

		@Override
		public TypeDescriptor getRequiredTypeDescriptor() {
			return TypeDescriptor.valueOf(String.class);
		}
	}
}