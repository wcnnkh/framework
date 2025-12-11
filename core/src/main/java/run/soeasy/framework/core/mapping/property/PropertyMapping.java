package run.soeasy.framework.core.mapping.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.core.NumberUtils;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 属性映射核心接口，基于{@link Mapping}扩展属性描述符的专属映射能力。
 * <p>
 * 核心定位：将「属性名（String）- 属性描述符（{@link PropertyDescriptor}）」的映射关系， 与「索引（Number）-
 * 属性描述符」的顺序访问能力融合，提供统一、宽松的唯一属性获取方式， 适配反射、ORM、配置解析等需要灵活访问属性的业务场景。
 * </p>
 * <h3>核心特性</h3>
 * <ul>
 * <li>双维度访问：支持按属性名（String）或索引（Number）获取唯一属性描述符，返回{@link Optional}避免空指针；</li>
 * <li>流式兼容：继承{@link Mapping}和{@link Streamable}的所有流式操作（过滤、映射、遍历等），无额外内存开销；</li>
 * <li>类型安全：泛型约束属性描述符类型，需实现{@link PropertyDescriptor}接口，保证属性名、类型等核心信息可获取；</li>
 * <li>无侵入适配：通过{@link #elements()}抽象属性描述符源，子类可灵活实现基于集合/反射/配置的属性加载。</li>
 * </ul>
 *
 * @author soeasy.run
 * @param <E> 属性描述符类型，需实现{@link run.soeasy.framework.core.mapping.property.PropertyDescriptor}接口
 * @see Mapping
 * @see PropertyDescriptor
 * @see Streamable
 * @see NumberUtils#toInteger(Number)
 */
public interface PropertyMapping<E extends PropertyDescriptor> extends Mapping<String, E> {
	/**
	 * 获取属性描述符的流式集合，是所有属性操作的数据源。
	 * <p>
	 * 子类需实现此方法，提供属性描述符的流式访问能力，空集合需返回{@link Streamable#empty()}而非null。
	 *
	 * @return 非null的{@link Streamable}&lt;E&gt;实例，空集合返回{@link Streamable#empty()}
	 */
	Streamable<E> elements();

	/**
	 * 获取属性类型数组，按{@link #elements()}的顺序映射。
	 * <p>
	 * 常用于反射调用（如{@link java.lang.reflect.Method#invoke(Object, Object...)}）的参数类型匹配、
	 * ORM字段类型校验等场景，空流返回空数组而非null。
	 *
	 * @param typeMapper 属性描述符到类型的映射函数（非null），需保证返回值非null（可返回{@link Object}.class兜底）
	 * @return 非null的类型数组，空流返回空数组（类型为Class&lt;?&gt;[]）
	 */
	default Class<?>[] getTypes(Function<? super E, ? extends Class<?>> typeMapper) {
		return elements().map(typeMapper).toArray(Class<?>[]::new);
	}

	/**
	 * 重写{@link Streamable#stream()}，将属性描述符映射为「属性名-描述符」的键值对流。
	 * <p>
	 * 保证与{@link Mapping}&lt;String, E&gt;的泛型约束一致，底层通过{@link #elements()}映射实现，无数据拷贝。
	 *
	 * @return 「属性名-描述符」的键值对流（{@link Stream}&lt;{KeyValue}&lt;String, E&gt;&gt;），非null
	 */
	@Override
	default Stream<KeyValue<String, E>> stream() {
		return elements().map((e) -> KeyValue.of(e.getName(), e)).stream();
	}

	@Override
	default PropertyMapping<E> toIndexed() {
		return toIndexed(ArrayList::new);
	}

	@Override
	default PropertyMapping<E> toIndexed(@NonNull Supplier<? extends List<KeyValue<String, E>>> collectionFactory) {
		return new IndexedPropertyMapping<>(this, collectionFactory);
	}

	@Override
	default PropertyMapping<E> toMapped() {
		return toMapped((a, b) -> {
			throw new IllegalStateException(String.format("Duplicate key (conflicting values: %s vs %s)", a, b));
		});
	}

	@Override
	default PropertyMapping<E> toMapped(@NonNull BinaryOperator<E> mergeFunction) {
		return toMapped(mergeFunction, LinkedHashMap::new);
	}

	@Override
	default PropertyMapping<E> toMapped(@NonNull BinaryOperator<E> mergeFunction,
			@NonNull Supplier<Map<String, E>> mapFactory) {
		return new MappedPropertyMapping<>(this, mergeFunction, mapFactory);
	}

	@Override
	default PropertyMapping<E> toMultiMapped() {
		return toMultiMapped(LinkedHashMap::new);
	}

	@Override
	default PropertyMapping<E> toMultiMapped(@NonNull Supplier<Map<String, Collection<E>>> mapFactory) {
		return toMultiMapped(mapFactory, ArrayList::new);
	}

	@Override
	default PropertyMapping<E> toMultiMapped(@NonNull Supplier<Map<String, Collection<E>>> mapFactory,
			@NonNull Supplier<Collection<E>> collectionFactory) {
		return new MultiMappedPropertyMapping<>(this, mapFactory, collectionFactory);
	}

	/**
	 * 宽松获取唯一属性描述符（区别于{@link Streamable#getUnique()}的严格抛异常模式）。
	 * <p>
	 * 自动适配两种key类型，统一返回{@link Optional}：
	 * <ul>
	 * <li><b>Number类型</b>：通过{@link NumberUtils#toInteger(Number)}转为整数索引，调用{@link Streamable#at(int)}按索引获取，
	 * 索引越界/无元素返回{@link Optional#empty()}；</li>
	 * <li><b>String类型</b>：按属性名匹配，调用{@link #getValues(Object)}后通过{@link Streamable#unique()}获取唯一元素，
	 * 无匹配/多匹配返回{@link Optional#empty()}；</li>
	 * <li><b>其他类型</b>：直接返回{@link Optional#empty()}。</li>
	 * </ul>
	 *
	 * <h3>异常说明</h3>
	 * <ul>
	 * <li>{@link NumberUtils#toInteger(Number)}会在传入非整数（如Double.NaN）、数值溢出（如Long.MAX_VALUE+1）时抛异常，
	 * 需调用方保证Number类型的合法性；</li>
	 * <li>属性名匹配时，若存在多个匹配项，不会抛{@link run.soeasy.framework.core.streaming.NoUniqueElementException}，
	 * 直接返回{@link Optional#empty()}。</li>
	 * </ul>
	 *
	 * @param key 索引（Number）或属性名（String），非null
	 * @return 唯一属性描述符的{@link Optional}&lt;E&gt;，无匹配/多匹配/不支持的key类型返回{@link Optional#empty()}
	 * @throws IllegalArgumentException 若Number类型无法转为合法整数（如数值溢出）
	 * @see NumberUtils#toInteger(Number)
	 * @see Streamable#at(int)
	 * @see Streamable#unique()
	 * @see #getValues(Object)
	 */
	default Optional<E> uniqueProperty(@NonNull Object key) {
		if (key instanceof Number) {
			return at(NumberUtils.toInteger((Number) key)).map(KeyValue::getValue);
		} else if (key instanceof String) {
			return getValues((String) key).unique();
		}
		return Optional.empty();
	}

	@Override
	default PropertyMapping<E> reload() {
		return this;
	}
}