package run.soeasy.framework.core.transform;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypeMapping;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.Registrations;
import run.soeasy.framework.core.exchange.container.map.TreeMapContainer;

/**
 * 转换器注册表，用于注册、管理和查找{@link Transformer}实例，
 * 实现{@link ConditionalTransformer}接口以支持基于类型映射的条件转换。
 * <p>
 * 该注册表通过{@link TypeMapping}作为键存储转换器，支持双向转换查找（S-&gt;T和T-&gt;S），
 * 适用于需要动态注册转换器并根据类型关系自动匹配的场景，如对象映射、数据转换框架等。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>类型映射注册：通过{@link TypeMapping}注册转换器，支持双向转换</li>
 * <li>自动转换器查找：根据源类型和目标类型自动匹配最合适的转换器</li>
 * <li>批量注册：支持一次性注册多个类型映射及其对应的转换器</li>
 * <li>消费型转换器注册：通过BiConsumer快速注册简单的属性转换逻辑</li>
 * </ul>
 *
 * @author soeasy.run
 * @see ConditionalTransformer
 * @see TypeMapping
 * @see Transformer
 */
public class TransformerRegistry extends TreeMapContainer<TypeMapping, Transformer> implements ConditionalTransformer {

	/**
	 * 根据源类型和目标类型查找匹配的转换器（支持双向查找）
	 * <p>
	 * 查找逻辑：
	 * <ol>
	 * <li>先按S->T类型映射哈希查找</li>
	 * <li>若未找到，按T->S类型映射哈希查找</li>
	 * <li>最后遍历所有注册的转换器，检查是否支持当前类型转换</li>
	 * </ol>
	 * 
	 * @param sourceTypeDescriptor 源类型描述符，不可为null
	 * @param targetTypeDescriptor 目标类型描述符，不可为null
	 * @return 匹配的转换器，若未找到返回null
	 */
	private Transformer getTransformer(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		Transformer transformer = getTransformerByHash(sourceTypeDescriptor, targetTypeDescriptor);
		if (transformer == null) {
			transformer = getTransformerByHash(targetTypeDescriptor, sourceTypeDescriptor);
		}

		for (Entry<TypeMapping, Transformer> entry : entrySet()) {
			if (entry.getValue().canTransform(sourceTypeDescriptor, targetTypeDescriptor)) {
				return entry.getValue();
			}
		}
		return null;
	}

	/**
	 * 通过类型映射哈希查找转换器
	 * 
	 * @param sourceTypeDescriptor 源类型描述符，不可为null
	 * @param targetTypeDescriptor 目标类型描述符，不可为null
	 * @return 匹配的转换器，若未找到返回null
	 */
	private Transformer getTransformerByHash(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		TypeMapping typeMapping = new TypeMapping(sourceTypeDescriptor.getType(), targetTypeDescriptor.getType());
		return get(typeMapping);
	}

	/**
	 * 判断是否存在支持当前类型转换的转换器
	 * 
	 * @param sourceTypeDescriptor 源类型描述符，不可为null
	 * @param targetTypeDescriptor 目标类型描述符，不可为null
	 * @return 存在支持的转换器返回true，否则false
	 */
	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return getTransformer(sourceTypeDescriptor, targetTypeDescriptor) != null;
	}

	/**
	 * 执行类型转换操作
	 * 
	 * @param source               源对象，不可为null
	 * @param sourceTypeDescriptor 源类型描述符，不可为null
	 * @param target               目标对象，不可为null
	 * @param targetTypeDescriptor 目标类型描述符，不可为null
	 * @return 转换成功返回true，否则false
	 * @throws ConverterNotFoundException 未找到支持的转换器时抛出
	 */
	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
		Transformer transformer = getTransformer(sourceTypeDescriptor, targetTypeDescriptor);
		if (transformer == null) {
			throw new ConverterNotFoundException(sourceTypeDescriptor, targetTypeDescriptor);
		}
		return transformer.transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
	}

	/**
	 * 获取所有已注册的类型映射集合
	 * 
	 * @return 不可变的类型映射集合（实际为TreeMap的keySet视图）
	 */
	@Override
	public Set<TypeMapping> getTransformableTypeMappings() {
		return keySet();
	}

	/**
	 * 注册条件转换器及其所有类型映射
	 * 
	 * @param conditionalTransformer 条件转换器，不可为null
	 * @return 注册句柄，可用于后续取消注册
	 */
	public Registration register(ConditionalTransformer conditionalTransformer) {
		Set<TypeMapping> typeMappings = conditionalTransformer.getTransformableTypeMappings();
		List<Registration> registrations = typeMappings.stream().map((e) -> register(e, conditionalTransformer))
				.collect(Collectors.toList());
		return Registrations.forList(registrations);
	}

	/**
	 * 注册消费型转换器
	 * 
	 * @param <S>        源泛型
	 * @param <T>        目标泛型
	 * @param sourceType 源类型Class，不可为null
	 * @param targetType 目标类型Class，不可为null
	 * @param consumer   转换逻辑函数，参数为(源对象, 目标对象)
	 * @return 注册句柄，可用于后续取消注册
	 */
	public <S, T> Registration register(Class<S> sourceType, Class<T> targetType,
			BiConsumer<? super S, ? super T> consumer) {
		ConsumeTransformer<S, T> transformer = new ConsumeTransformer<>(sourceType, targetType, consumer);
		return register(transformer);
	}
}