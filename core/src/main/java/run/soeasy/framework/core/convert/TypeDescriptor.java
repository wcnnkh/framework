package run.soeasy.framework.core.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.annotation.MergedAnnotatedElement;
import run.soeasy.framework.core.type.ClassUtils;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 类型描述符，封装类型信息及注解元数据。
 * <p>
 * 用于描述Java类型的详细信息，包括原始类型、泛型参数、数组/集合/映射类型的元素类型，
 * 以及类型关联的注解信息。该类是类型转换框架的核心数据结构，支持类型窄化、向上转型等操作。
 */
@Getter
public class TypeDescriptor extends MergedAnnotatedElement {
	private static final Map<Class<?>, TypeDescriptor> commonTypesCache = new HashMap<>(32);

	/**
	 * 预缓存的常见类型数组，包含基本类型、包装类型和常用引用类型。
	 * <p>
	 * 用于提升常见类型的{@link TypeDescriptor}创建效率，避免重复反射操作。
	 */
	private static final Class<?>[] CACHED_COMMON_TYPES = { boolean.class, Boolean.class, byte.class, Byte.class,
			char.class, Character.class, double.class, Double.class, float.class, Float.class, int.class, Integer.class,
			long.class, Long.class, short.class, Short.class, String.class, Object.class };

	static {
		for (Class<?> preCachedClass : CACHED_COMMON_TYPES) {
			commonTypesCache.put(preCachedClass, valueOf(preCachedClass));
		}
	}

	/**
	 * 原始类型（可能为{@code null}，由{@link ResolvableType}解析）。
	 */
	private final Class<?> type;

	/**
	 * 可解析类型，包含完整的泛型信息。
	 */
	@NonNull
	private final ResolvableType resolvableType;

	/**
	 * 构造{@link TypeDescriptor}实例。
	 * 
	 * @param resolvableType    可解析类型（不可为{@code null}）
	 * @param type              原始类型（可为{@code null}，通过{@code resolvableType}解析）
	 * @param annotatedElements 注解元素数组（用于合并注解元数据）
	 */
	public TypeDescriptor(@NonNull ResolvableType resolvableType, Class<?> type,
			@NonNull AnnotatedElement... annotatedElements) {
		super(annotatedElements.length == 0 ? Collections.emptyList() : Arrays.asList(annotatedElements));
		this.resolvableType = resolvableType;
		this.type = (type != null ? type : resolvableType.getRawType());
	}

	/**
	 * 获取类型（优先使用构造时指定的{@code type}，否则从{@code resolvableType}解析）。
	 * 
	 * @return 类型{@link Class}对象
	 */
	public Class<?> getType() {
		return type == null ? resolvableType.getRawType() : type;
	}

	/**
	 * 获取对象类型（解析基本类型的包装类型为原始类型）。
	 * 
	 * @return 解析后的对象类型
	 */
	public Class<?> getObjectType() {
		return ClassUtils.resolvePrimitiveIfNecessary(getType());
	}

	/**
	 * 窄化类型描述符（根据实际值的类型调整描述符）。
	 * <p>
	 * 例如：{@code TypeDescriptor.forType(Collection.class).narrow(new ArrayList<String>())}
	 * 将返回包含{@code String}泛型参数的{@code Collection}类型描述符。
	 * 
	 * @param value 实际值（可为{@code null}，返回原描述符）
	 * @return 窄化后的{@link TypeDescriptor}
	 */
	public TypeDescriptor narrow(Object value) {
		if (value == null) {
			return this;
		}
		ResolvableType narrowed = ResolvableType.forType(value.getClass(), getResolvableType());
		return new TypeDescriptor(narrowed, value.getClass(), this);
	}

	/**
	 * 向上转型类型描述符（转换为指定父类型）。
	 * 
	 * @param superType 父类型（不可为{@code null}，必须是当前类型的父类型）
	 * @return 转型后的{@link TypeDescriptor}，{@code null}表示转型失败
	 */
	public TypeDescriptor upcast(Class<?> superType) {
		if (superType == null) {
			return null;
		}
		Assert.isAssignable(superType, getType());
		return new TypeDescriptor(getResolvableType().as(superType), superType, this);
	}

	/**
	 * 获取类型的全限定名。
	 * 
	 * @return 类型全限定名字符串
	 */
	public String getName() {
		return ClassUtils.getQualifiedName(getType());
	}

	/**
	 * 判断是否为基本类型。
	 * 
	 * @return {@code true}表示基本类型，{@code false}表示引用类型
	 */
	public boolean isPrimitive() {
		return getType().isPrimitive();
	}

	/**
	 * 判断当前类型是否可赋值给目标类型描述符。
	 * <p>
	 * 校验逻辑：
	 * <ol>
	 * <li>原始类型兼容性</li>
	 * <li>数组/集合/映射类型的元素类型兼容性</li>
	 * </ol>
	 * 
	 * @param typeDescriptor 目标类型描述符
	 * @return {@code true}表示可赋值，{@code false}表示不可赋值
	 */
	public boolean isAssignableTo(TypeDescriptor typeDescriptor) {
		boolean typesAssignable = typeDescriptor.getObjectType().isAssignableFrom(getObjectType());
		if (!typesAssignable) {
			return false;
		}
		if (isArray() && typeDescriptor.isArray()) {
			return isNestedAssignable(getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
		} else if (isCollection() && typeDescriptor.isCollection()) {
			return isNestedAssignable(getElementTypeDescriptor(), typeDescriptor.getElementTypeDescriptor());
		} else if (isMap() && typeDescriptor.isMap()) {
			return isNestedAssignable(getMapKeyTypeDescriptor(), typeDescriptor.getMapKeyTypeDescriptor())
					&& isNestedAssignable(getMapValueTypeDescriptor(), typeDescriptor.getMapValueTypeDescriptor());
		} else {
			return true;
		}
	}

	/**
	 * 判断嵌套类型是否可赋值（辅助{@link #isAssignableTo(TypeDescriptor)}方法）。
	 * 
	 * @param nestedTypeDescriptor      当前嵌套类型描述符
	 * @param otherNestedTypeDescriptor 目标嵌套类型描述符
	 * @return {@code true}表示可赋值
	 */
	private boolean isNestedAssignable(TypeDescriptor nestedTypeDescriptor, TypeDescriptor otherNestedTypeDescriptor) {
		return (nestedTypeDescriptor == null || otherNestedTypeDescriptor == null
				|| nestedTypeDescriptor.isAssignableTo(otherNestedTypeDescriptor));
	}

	/**
	 * 判断是否为集合类型（实现{@link Collection}接口）。
	 * 
	 * @return {@code true}表示集合类型
	 */
	public boolean isCollection() {
		return Collection.class.isAssignableFrom(getType());
	}

	/**
	 * 判断是否为数组类型。
	 * 
	 * @return {@code true}表示数组类型
	 */
	public boolean isArray() {
		return getType().isArray();
	}

	/**
	 * 映射可解析类型（用于泛型转换）。
	 * 
	 * @param mapper 可解析类型映射函数（不可为{@code null}）
	 * @return 映射后的{@link TypeDescriptor}
	 */
	public TypeDescriptor map(@NonNull Function<? super ResolvableType, ? extends ResolvableType> mapper) {
		ResolvableType resolvableType = mapper.apply(this.resolvableType);
		return new TypeDescriptor(resolvableType, null, this);
	}

	/**
	 * 获取元素类型描述符（适用于数组和集合）。
	 * <p>
	 * 示例：{@code List<String>}的元素类型描述符为{@code String}。
	 * 
	 * @return 元素类型的{@link TypeDescriptor}
	 */
	public TypeDescriptor getElementTypeDescriptor() {
		if (getResolvableType().isArray()) {
			return new TypeDescriptor(getResolvableType().getComponentType(), null, this);
		}
		return upcast(Collection.class).map((e) -> e.getActualTypeArgument(0));
	}

	/**
	 * 根据元素值窄化元素类型描述符。
	 * 
	 * @param element 元素值
	 * @return 窄化后的元素类型描述符
	 */
	public TypeDescriptor elementTypeDescriptor(Object element) {
		return narrow(element, getElementTypeDescriptor());
	}

	/**
	 * 判断是否为映射类型（实现{@link Map}接口）。
	 * 
	 * @return {@code true}表示映射类型
	 */
	public boolean isMap() {
		return Map.class.isAssignableFrom(getType());
	}

	/**
	 * 获取映射键类型描述符。
	 * <p>
	 * 示例：{@code Map<String, Integer>}的键类型描述符为{@code String}。
	 * 
	 * @return 键类型的{@link TypeDescriptor}
	 */
	public TypeDescriptor getMapKeyTypeDescriptor() {
		Assert.state(isMap(), "Not a [java.util.Map]");
		return upcast(Map.class).map((e) -> e.getActualTypeArgument(0));
	}

	/**
	 * 根据键值窄化映射键类型描述符。
	 * 
	 * @param mapKey 键值
	 * @return 窄化后的键类型描述符
	 */
	public TypeDescriptor getMapKeyTypeDescriptor(Object mapKey) {
		return narrow(mapKey, getMapKeyTypeDescriptor());
	}

	/**
	 * 获取映射值类型描述符。
	 * <p>
	 * 示例：{@code Map<String, Integer>}的值类型描述符为{@code Integer}。
	 * 
	 * @return 值类型的{@link TypeDescriptor}
	 */
	public TypeDescriptor getMapValueTypeDescriptor() {
		Assert.state(isMap(), "Not a [java.util.Map]");
		return upcast(Map.class).map((e) -> e.getActualTypeArgument(1));
	}

	/**
	 * 根据值窄化映射值类型描述符。
	 * 
	 * @param mapValue 映射值
	 * @return 窄化后的值类型描述符
	 */
	public TypeDescriptor getMapValueTypeDescriptor(Object mapValue) {
		return narrow(mapValue, getMapValueTypeDescriptor());
	}

	/**
	 * 窄化类型描述符（通用辅助方法）。
	 * 
	 * @param value          实际值
	 * @param typeDescriptor 原始类型描述符
	 * @return 窄化后的类型描述符
	 */
	private TypeDescriptor narrow(Object value, TypeDescriptor typeDescriptor) {
		if (typeDescriptor != null) {
			return typeDescriptor.narrow(value);
		}
		if (value != null) {
			return narrow(value);
		}
		return null;
	}

	/**
	 * 判断与另一个{@link TypeDescriptor}是否相等。
	 * <p>
	 * 相等条件：
	 * <ol>
	 * <li>原始类型相同</li>
	 * <li>注解元数据相同</li>
	 * <li>集合/数组/映射的元素类型相同</li>
	 * </ol>
	 * 
	 * @param other 待比较的对象
	 * @return {@code true}表示相等
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof TypeDescriptor)) {
			return false;
		}
		TypeDescriptor otherDesc = (TypeDescriptor) other;
		if (getType() != otherDesc.getType()) {
			return false;
		}
		if (!annotationsMatch(otherDesc)) {
			return false;
		}
		if (isCollection() || isArray()) {
			return ObjectUtils.equals(getElementTypeDescriptor(), otherDesc.getElementTypeDescriptor());
		} else if (isMap()) {
			return (ObjectUtils.equals(getMapKeyTypeDescriptor(), otherDesc.getMapKeyTypeDescriptor())
					&& ObjectUtils.equals(getMapValueTypeDescriptor(), otherDesc.getMapValueTypeDescriptor()));
		} else {
			return true;
		}
	}

	/**
	 * 比较注解元数据是否匹配。
	 * 
	 * @param otherDesc 另一个{@link TypeDescriptor}
	 * @return {@code true}表示注解匹配
	 */
	private boolean annotationsMatch(TypeDescriptor otherDesc) {
		Annotation[] anns = getAnnotations();
		Annotation[] otherAnns = otherDesc.getAnnotations();
		if (anns == otherAnns) {
			return true;
		}
		if (anns.length != otherAnns.length) {
			return false;
		}
		if (anns.length > 0) {
			for (int i = 0; i < anns.length; i++) {
				if (!annotationEquals(anns[i], otherAnns[i])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 比较两个注解是否相等（优化版）。
	 * 
	 * @param ann      注解1
	 * @param otherAnn 注解2
	 * @return {@code true}表示相等
	 */
	private boolean annotationEquals(Annotation ann, Annotation otherAnn) {
		// 优先比较引用地址，再比较注解类型和值，提升性能
		return (ann == otherAnn || (ann.getClass() == otherAnn.getClass() && ann.equals(otherAnn)));
	}

	/**
	 * 获取哈希码（基于原始类型）。
	 * 
	 * @return 哈希码
	 */
	@Override
	public int hashCode() {
		return getType().hashCode();
	}

	/**
	 * 获取类型描述字符串（包含注解和类型信息）。
	 * 
	 * @return 类型描述字符串
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Annotation ann : getAnnotations()) {
			builder.append('@').append(ann.annotationType().getName()).append(' ');
		}
		builder.append(getResolvableType());
		return builder.toString();
	}

	/**
	 * 根据对象创建{@link TypeDescriptor}（对象为{@code null}时返回{@code null}）。
	 * 
	 * @param source 源对象
	 * @return {@link TypeDescriptor}实例，{@code null}表示{@code source}为{@code null}
	 */
	public static TypeDescriptor forObject(Object source) {
		return (source != null ? valueOf(source.getClass()) : null);
	}

	/**
	 * 根据类型创建{@link TypeDescriptor}（使用预缓存优化常见类型）。
	 * 
	 * @param type 类型（可为{@code null}，默认{@link Object}）
	 * @return {@link TypeDescriptor}实例
	 */
	public static TypeDescriptor valueOf(Class<?> type) {
		if (type == null) {
			type = Object.class;
		}
		TypeDescriptor desc = commonTypesCache.get(type);
		return (desc != null ? desc : forType(type));
	}

	/**
	 * 创建带泛型参数的{@link TypeDescriptor}。
	 * 
	 * @param <T>      原始类型泛型
	 * @param type     原始类型
	 * @param generics 泛型参数数组
	 * @return 带泛型的{@link TypeDescriptor}
	 */
	public static <T> TypeDescriptor forClassWithGenerics(Class<T> type, Type... generics) {
		return forType(ResolvableType.forClassWithGenerics(type, generics));
	}

	/**
	 * 创建集合类型的{@link TypeDescriptor}。
	 * 
	 * @param collectionType 集合类型（必须是{@link Collection}的子类型）
	 * @param elementType    元素类型
	 * @return 集合类型描述符
	 */
	public static TypeDescriptor collection(@NonNull Class<?> collectionType, Type elementType) {
		if (!Collection.class.isAssignableFrom(collectionType)) {
			throw new IllegalArgumentException("Collection type must be a [java.util.Collection]");
		}
		return forClassWithGenerics(collectionType, elementType);
	}

	/**
	 * 创建映射类型的{@link TypeDescriptor}。
	 * 
	 * @param mapType   映射类型（必须是{@link Map}的子类型）
	 * @param keyType   键类型
	 * @param valueType 值类型
	 * @return 映射类型描述符
	 */
	public static TypeDescriptor map(@NonNull Class<?> mapType, @NonNull Type keyType, @NonNull Type valueType) {
		if (!Map.class.isAssignableFrom(mapType)) {
			throw new IllegalArgumentException("Map type must be a [java.util.Map]");
		}
		return forClassWithGenerics(mapType, keyType, valueType);
	}

	/**
	 * 创建数组类型的{@link TypeDescriptor}。
	 * 
	 * @param elementTypeDescriptor 元素类型描述符
	 * @return 数组类型描述符
	 */
	public static TypeDescriptor array(@NonNull TypeDescriptor elementTypeDescriptor) {
		return forType(ResolvableType.forArrayComponent(elementTypeDescriptor.resolvableType), elementTypeDescriptor);
	}

	/**
	 * 根据可执行方法创建返回值的{@link TypeDescriptor}。
	 * 
	 * @param executable 可执行方法（构造方法或普通方法）
	 * @return 返回值类型描述符
	 */
	public static TypeDescriptor forMethodReturnType(@NonNull Executable executable) {
		if (executable instanceof Method) {
			return forType(((Method) executable).getGenericReturnType(), executable);
		}
		return forType(executable.getDeclaringClass(), executable);
	}

	/**
	 * 根据字段创建{@link TypeDescriptor}。
	 * 
	 * @param field 字段对象
	 * @return 字段类型描述符
	 */
	public static TypeDescriptor forFieldType(@NonNull Field field) {
		return forType(field.getGenericType(), field);
	}

	/**
	 * 根据参数创建{@link TypeDescriptor}。
	 * 
	 * @param parameter 参数对象
	 * @return 参数类型描述符
	 */
	public static TypeDescriptor forParameter(@NonNull Parameter parameter) {
		return forType(parameter.getParameterizedType(), parameter);
	}

	/**
	 * 根据可执行方法的参数索引创建{@link TypeDescriptor}。
	 * 
	 * @param executable 可执行方法
	 * @param index      参数索引
	 * @return 参数类型描述符
	 * @throws IndexOutOfBoundsException 当索引超出参数数量时抛出
	 */
	public static TypeDescriptor forExecutableParameter(Executable executable, int index) {
		if (index >= executable.getParameterCount()) {
			throw new IndexOutOfBoundsException(
					"index: " + index + ", parameter count: " + executable.getParameterCount());
		}
		Parameter parameter = executable.getParameters()[index];
		return forParameter(parameter);
	}

	/**
	 * 为指定的类型和注解元素创建一个TypeDescriptor实例
	 * 
	 * TypeDescriptor用于描述Java类型的详细信息，包括泛型参数、注解等元数据，
	 * 通常在需要处理类型信息的框架中使用（如Spring框架的类型转换系统）
	 *
	 * @param type              要描述的Java类型（如Class对象）
	 * @param annotatedElements 与该类型相关的注解元素数组，可包含字段、方法等带有注解的元素
	 * @return 新创建的TypeDescriptor实例，包含了指定类型的解析信息和相关注解
	 */
	public static TypeDescriptor forType(Type type, AnnotatedElement... annotatedElements) {
		// 将普通Type转换为可解析的ResolvableType，便于处理泛型等复杂类型
		ResolvableType resolvableType = ResolvableType.forType(type);

		// 创建并返回TypeDescriptor实例，封装解析后的类型信息和注解元素
		return new TypeDescriptor(resolvableType, null, annotatedElements);
	}
}