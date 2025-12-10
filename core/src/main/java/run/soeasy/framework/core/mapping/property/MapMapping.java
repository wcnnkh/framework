package run.soeasy.framework.core.mapping.property;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.streaming.Streamable;

@RequiredArgsConstructor
@Getter
@AllArgsConstructor
public class MapMapping implements PropertyMapping<PropertyAccessor> {
	/** 被映射的Map实例，不可为null */
	@SuppressWarnings("rawtypes")
	@NonNull
	private final Map map;

	/** Map的类型描述符，用于获取值的类型信息，不可为null */
	@NonNull
	private final TypeDescriptor typeDescriptor;

	/** 类型转换器，用于Map值的类型转换，默认为系统转换服务 */
	private Converter converter = SystemConversionService.getInstance();
	
	@SuppressWarnings("unchecked")
	@Override
	public Streamable<PropertyAccessor> elements() {
		return Streamable.of(((Map<Object, Object>) map).keySet()).map((key) -> createAccessor(key));
	}

	@Override
	public boolean hasKey(String key) {
		return map.containsKey(key);
	}

	/**
	 * 创建Map条目访问器
	 * <p>
	 * 使用提供的Map、键、类型描述符和转换器创建{@link MapPropertyAccessor}实例， 用于类型安全地访问和操作Map条目值。
	 * 
	 * @param key Map中的键
	 * @return 新创建的Map条目访问器
	 */
	private PropertyAccessor createAccessor(Object key) {
		return new MapPropertyAccessor(key);
	}

	@Override
	public Streamable<PropertyAccessor> getValues(String key) {
		return Streamable.singleton(createAccessor(key));
	}

	@Override
	public boolean isMapped() {
		return true;
	}

	@RequiredArgsConstructor
	@Getter
	private class MapPropertyAccessor implements PropertyAccessor {

		/** Map中的键，用于定位具体条目，不可为null */
		@NonNull
		private final Object key;

		/**
		 * 获取Map条目的值并转换为目标类型
		 * <p>
		 * 该方法从Map中获取指定键的值，若值存在则使用转换器将其转换为Map值类型， 若值不存在或转换失败则抛出异常。
		 * 
		 * @return 转换后的Map条目值
		 * @throws ConversionException 当值存在但类型转换失败时抛出
		 */
		@Override
		public Object get() throws ConversionException {
			Object value = map.get(key);
			return converter.convert(value, typeDescriptor.getMapValueTypeDescriptor());
		}

		/**
		 * 设置Map条目的值（支持类型转换）
		 * <p>
		 * 该方法将输入值转换为Map值类型后，设置到Map的指定键位置， 若Map不支持修改操作则抛出异常。
		 * 
		 * @param value 要设置的值
		 * @throws UnsupportedOperationException 当Map不支持修改操作时抛出
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void set(Object value) throws UnsupportedOperationException {
			Object target = converter.convert(value, typeDescriptor.getMapValueTypeDescriptor());
			((Map<Object, Object>) map).put(key, target);
		}

		/**
		 * 判断Map条目是否可读
		 * <p>
		 * 该方法通过检查Map是否包含指定键来判断条目是否可读， 与{@link #get()}方法配合使用可避免空指针异常。
		 * 
		 * @return 若Map包含指定键返回true，否则返回false
		 */
		@Override
		public boolean isReadable() {
			return map.containsKey(key);
		}

		/**
		 * 判断Map条目是否可写
		 * <p>
		 * 该方法始终返回true，因为Map理论上支持写操作， 实际是否可写由Map实现决定（如不可变Map会在set时抛出异常）。
		 * 
		 * @return 始终返回true
		 */
		@Override
		public boolean isWriteable() {
			return true;
		}

		/**
		 * 获取期望的值类型描述符
		 * <p>
		 * 返回Map值类型的描述符，用于指示set操作时期望的值类型。
		 * 
		 * @return Map值类型的描述符
		 */
		@Override
		public TypeDescriptor getRequiredTypeDescriptor() {
			return typeDescriptor.getMapValueTypeDescriptor();
		}

		/**
		 * 获取返回值的类型描述符
		 * <p>
		 * 返回Map值类型的描述符，用于指示get操作返回值的类型。
		 * 
		 * @return Map值类型的描述符
		 */
		@Override
		public TypeDescriptor getReturnTypeDescriptor() {
			return typeDescriptor.getMapValueTypeDescriptor();
		}

		@Override
		public String getName() {
			return converter.convert(key, String.class);
		}
	}
}