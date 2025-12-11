package run.soeasy.framework.core.mapping;

import java.util.Collections;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.spi.ConfigurableServices;
import run.soeasy.framework.core.spi.ServiceComparator;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.transform.Transformer;
import run.soeasy.framework.core.type.InstanceFactory;
import run.soeasy.framework.core.type.InstanceFactorySupporteds;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 默认映射器，继承自{@link GenericMapper}，实现多接口整合的通用映射功能，
 * 支持类型转换、实例创建、映射创建等核心能力，是框架中最全面的映射实现。
 * <p>
 * 该映射器整合了以下核心组件：
 * <ul>
 * <li>过滤器链：继承自父类的过滤器机制，支持预处理和后处理</li>
 * <li>实例工厂：负责目标对象的创建，默认使用反射实现</li>
 * <li>映射提供者：管理源和目标对象的映射关系</li>
 * <li>值映射器：处理具体的属性值转换</li>
 * </ul>
 * 实现了从对象到对象的完整转换流程，包括对象创建、属性映射和类型转换。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>多接口支持：同时实现{@link Transformer}、{@link Converter}、{@link InstanceFactory}等接口</li>
 * <li>类型感知：基于{@link TypeDescriptor}进行精确的类型匹配和转换</li>
 * <li>可配置性：支持自定义过滤器、实例工厂和映射规则</li>
 * <li>递归映射：通过内部映射器实现复杂对象的递归转换</li>
 * <li>异常处理：统一的{@link ConversionException}处理机制</li>
 * </ul>
 *
 * <p>
 * <b>转换流程：</b>
 * <ol>
 * <li>检查源和目标类型是否支持映射</li>
 * <li>创建目标对象实例</li>
 * <li>获取源和目标对象的映射关系</li>
 * <li>应用过滤器链进行预处理</li>
 * <li>执行属性值的映射和转换</li>
 * <li>应用过滤器链进行后处理</li>
 * </ol>
 *
 * @param <K> 映射键的类型
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}
 * @param <T> 映射上下文的类型，需实现{@link Mapping}
 * 
 * @author soeasy.run
 * @see GenericMapper
 * @see Transformer
 * @see Converter
 * @see InstanceFactory
 */
@Getter
@Setter
public class DefaultMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends GenericMapper<K, V, T>
		implements Transformer, Converter, InstanceFactory, MappingFactory<Object, K, V, T> {

	/** 实例工厂，用于创建目标对象实例，默认使用反射实现 */
	@NonNull
	private InstanceFactory instanceFactory = InstanceFactorySupporteds.ALL;
	
	/** 映射提供者，负责管理和获取对象的映射关系 */
	private final MappingProvider<K, V, T> mappingProvider = new MappingProvider<>();

	/**
	 * 构造默认映射器
	 * <p>
	 * 初始化时使用可配置的过滤器服务和值映射器
	 */
	public DefaultMapper() {
		super(new ConfigurableServices<>(ServiceComparator.defaultServiceComparator()), new ValueMapper<>());
	}

	/**
	 * 获取映射过滤器集合
	 * 
	 * @return 可配置的映射过滤器服务
	 */
	@Override
	public @NonNull ConfigurableServices<MappingFilter<K, V, T>> getFilters() {
		return (ConfigurableServices<MappingFilter<K, V, T>>) super.getFilters();
	}

	/**
	 * 获取值映射器
	 * 
	 * @return 值映射器实例
	 */
	@Override
	public @NonNull ValueMapper<K, V, T> getMapper() {
		return (ValueMapper<K, V, T>) super.getMapper();
	}

	/**
	 * 判断是否可以进行类型转换
	 * <p>
	 * 转换条件：
	 * <ul>
	 * <li>可以实例化目标类型</li>
	 * <li>可以进行源类型到目标类型的转换</li>
	 * </ul>
	 * 
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 可以转换返回true，否则false
	 */
	@Override
	public boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return (canInstantiated(targetTypeDescriptor.getResolvableType())
				&& canTransform(sourceTypeDescriptor, targetTypeDescriptor));
	}

	/**
	 * 判断是否可以实例化指定类型
	 * 
	 * @param requiredType 需要实例化的类型
	 * @return 可以实例化返回true，否则false
	 */
	@Override
	public boolean canInstantiated(@NonNull ResolvableType requiredType) {
		return instanceFactory.canInstantiated(requiredType);
	}

	/**
	 * 判断是否可以进行对象转换
	 * <p>
	 * 转换条件：
	 * <ul>
	 * <li>源类型有可用的映射</li>
	 * <li>目标类型有可用的映射</li>
	 * </ul>
	 * 
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 可以转换返回true，否则false
	 */
	@Override
	public boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return (hasMapping(sourceTypeDescriptor) && hasMapping(targetTypeDescriptor));
	}

	/**
	 * 执行类型转换
	 * <p>
	 * 转换流程：
	 * <ol>
	 * <li>创建目标类型的实例</li>
	 * <li>将源对象的属性值转换并映射到目标对象</li>
	 * </ol>
	 * 
	 * @param source               源对象
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 转换后的目标对象
	 * @throws ConversionException 转换过程中发生错误时抛出
	 */
	@Override
	public Object convert(Object source, TypeDescriptor sourceTypeDescriptor, TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		Object target = newInstance(targetTypeDescriptor.getResolvableType());
		transform(source, sourceTypeDescriptor, target, targetTypeDescriptor);
		return target;
	}

	/**
	 * 获取对象的映射关系
	 * 
	 * @param source       源对象
	 * @param requiredType 所需的类型描述符
	 * @return 对象的映射关系
	 */
	@Override
	public T getMapping(@NonNull Object source, @NonNull TypeDescriptor requiredType) {
		return mappingProvider.getMapping(source, requiredType);
	}

	/**
	 * 判断是否存在指定类型的映射
	 * 
	 * @param requiredType 所需的类型描述符
	 * @return 存在映射返回true，否则false
	 */
	@Override
	public boolean hasMapping(@NonNull TypeDescriptor requiredType) {
		return mappingProvider.hasMapping(requiredType);
	}

	/**
	 * 创建指定类型的新实例
	 * 
	 * @param requiredType 所需的类型
	 * @return 新创建的实例
	 */
	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		return instanceFactory.newInstance(requiredType);
	}

	/**
	 * 执行对象转换
	 * <p>
	 * 转换条件：
	 * <ul>
	 * <li>源类型有可用的映射</li>
	 * <li>目标类型有可用的映射</li>
	 * </ul>
	 * 
	 * @param source               源对象
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param target               目标对象
	 * @param targetTypeDescriptor 目标类型描述符
	 * @return 转换成功返回true，否则false
	 * @throws ConversionException 转换过程中发生错误时抛出
	 */
	@Override
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return transform(source, sourceTypeDescriptor, target, targetTypeDescriptor, Collections.emptyList());
	}

	/**
	 * 执行带额外过滤器的对象转换
	 * 
	 * @param source               源对象
	 * @param sourceTypeDescriptor 源类型描述符
	 * @param target               目标对象
	 * @param targetTypeDescriptor 目标类型描述符
	 * @param filters              额外的映射过滤器集合
	 * @return 转换成功返回true，否则false
	 * @throws ConversionException 转换过程中发生错误时抛出
	 */
	public boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor,
			@NonNull Iterable<MappingFilter<K, V, T>> filters) throws ConversionException {
		if (hasMapping(sourceTypeDescriptor) && hasMapping(targetTypeDescriptor)) {
			return doMapping(getMapping(source, sourceTypeDescriptor), getMapping(target, targetTypeDescriptor),
					filters);
		}
		return false;
	}

	/**
	 * 执行映射转换
	 * 
	 * @param sourceMapping 源映射
	 * @param targetMapping 目标映射
	 * @param filters       映射过滤器集合
	 * @return 映射成功返回true，否则false
	 * @throws ConversionException 转换过程中发生错误时抛出
	 */
	public boolean doMapping(@NonNull T sourceMapping, @NonNull T targetMapping,
			@NonNull Iterable<MappingFilter<K, V, T>> filters) throws ConversionException {
		return doMapping(new MappingContext<>(sourceMapping), new MappingContext<>(targetMapping), filters);
	}

	// ------------------------------------以下都是为了继承时方便重写为final方法----------------------------------------------------//

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return Converter.super.canConvert(sourceClass, targetClass);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final boolean canConvert(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
		return Converter.super.canConvert(sourceClass, targetTypeDescriptor);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final boolean canConvert(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
		return Converter.super.canConvert(sourceTypeDescriptor, targetClass);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final boolean canTransform(@NonNull Class<?> sourceClass, @NonNull Class<?> targetClass) {
		return Transformer.super.canTransform(sourceClass, targetClass);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final boolean canTransform(@NonNull Class<?> sourceClass, @NonNull TypeDescriptor targetTypeDescriptor) {
		return Transformer.super.canTransform(sourceClass, targetTypeDescriptor);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final boolean canTransform(@NonNull TypeDescriptor sourceTypeDescriptor, @NonNull Class<?> targetClass) {
		return Transformer.super.canTransform(sourceTypeDescriptor, targetClass);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final <U> U convert(@NonNull Object source, @NonNull Class<? extends U> targetClass)
			throws ConversionException {
		return Converter.super.convert(source, targetClass);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final Object convert(Object source, @NonNull Class<?> sourceClass,
			@NonNull TypeDescriptor targetTypeDescriptor) throws ConversionException {
		return Converter.super.convert(source, sourceClass, targetTypeDescriptor);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final <U> U convert(Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Class<? extends U> targetClass) throws ConversionException {
		return Converter.super.convert(source, sourceTypeDescriptor, targetClass);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final Object convert(Object source, @NonNull TypeDescriptor targetTypeDescriptor)
			throws ConversionException {
		return Converter.super.convert(source, targetTypeDescriptor);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final boolean transform(@NonNull Object source, @NonNull Object target) {
		return Transformer.super.transform(source, target);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final boolean transform(@NonNull Object source, @NonNull Object target,
			@NonNull TypeDescriptor targetTypeDescriptor) {
		return Transformer.super.transform(source, target, targetTypeDescriptor);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final boolean transform(@NonNull Object source, @NonNull TypeDescriptor sourceTypeDescriptor,
			@NonNull Object target) {
		return Transformer.super.transform(source, sourceTypeDescriptor, target);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	public final <U> boolean transform(Object source, TypeDescriptor sourceTypeDescriptor, U target,
			Class<? extends U> targetClass) {
		return Transformer.super.transform(source, sourceTypeDescriptor, target, targetClass);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	public final <S, U> boolean transform(S source, Class<? extends S> sourceClass, U target,
			Class<? extends U> targetClass) {
		return Transformer.super.transform(source, sourceClass, target, targetClass);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	public final <U> boolean transform(Object source, U target, Class<? extends U> targetClass) {
		return Transformer.super.transform(source, target, targetClass);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass,
			@NonNull Object target) {
		return Transformer.super.transform(source, sourceClass, target);
	}

	/**
	 * 接口默认方法的final实现，防止子类重写
	 */
	@Override
	public final <S> boolean transform(@NonNull S source, @NonNull Class<? extends S> sourceClass,
			@NonNull Object target, @NonNull TypeDescriptor targetTypeDescriptor) {
		return Transformer.super.transform(source, sourceClass, target, targetTypeDescriptor);
	}
}