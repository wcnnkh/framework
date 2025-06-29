package run.soeasy.framework.core.type;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import lombok.NonNull;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.collection.CollectionUtils;

/**
 * 可解析类型接口，提供泛型类型的解析、操作和查询功能
 * <p>
 * 整合了ParameterizedType、WildcardType和TypeVariableResolver接口，
 * 用于处理Java泛型类型擦除后的类型信息解析，支持参数化类型、通配符类型、
 * 类型变量和数组类型的解析与操作
 * </p>
 */
public interface ResolvableType extends ParameterizedType, WildcardType, TypeVariableResolver {
	
	/**
	 * 空类型数组常量，避免重复创建
	 */
	public static final ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];
	
	/**
	 * 可解析类型工厂，通过服务加载机制获取，支持扩展自定义类型解析实现
	 * <p>
	 * 优先加载通过ServiceLoader注册的ResolvableTypeFactory实现，
	 * 无自定义实现时使用默认的DefaultResolvableTypeFactory
	 * </p>
	 */
	public static final ResolvableTypeFactory RESOLVABLE_TYPE_FACTORY = CollectionUtils
			.unknownSizeStream(ServiceLoader.load(ResolvableTypeFactory.class).iterator()).findFirst()
			.orElse(DefaultResolvableTypeFactory.INSTANCE);
	
	/**
	 * 表示无类型的特殊实例，用于空类型场景的返回值
	 */
	public static final ResolvableType NONE = new NoneType(Object.class);

	/**
	 * 将原始类型数组转换为ResolvableType数组，支持类型变量解析器
	 * 
	 * @param typeVariableResolver 类型变量解析器，用于处理泛型变量绑定
	 * @param types                原始类型数组
	 * @return ResolvableType数组，类型为null或空时返回EMPTY_TYPES_ARRAY
	 */
	public static ResolvableType[] toResolvableTypes(TypeVariableResolver typeVariableResolver, Type... types) {
		if (types == null || types.length == 0) {
			return ResolvableType.EMPTY_TYPES_ARRAY;
		}

		ResolvableType[] typeProviders = new ResolvableType[types.length];
		for (int i = 0; i < typeProviders.length; i++) {
			typeProviders[i] = forType(types[i], typeVariableResolver);
		}
		return typeProviders;
	}

	/**
	 * 根据原始类型创建ResolvableType实例，使用默认类型变量解析器
	 * 
	 * @param type 原始类型（不可为null）
	 * @return ResolvableType实例
	 */
	public static ResolvableType forType(@NonNull Type type) {
		return forType(type, null);
	}

	/**
	 * 根据原始类型和类型变量解析器创建ResolvableType实例
	 * 
	 * @param type       原始类型（不可为null）
	 * @param resolver   类型变量解析器（可为null，使用默认解析器）
	 * @return ResolvableType实例
	 */
	public static ResolvableType forType(@NonNull Type type, TypeVariableResolver resolver) {
		if (type instanceof ResolvableType) {
			return (ResolvableType) type;
		}
		return RESOLVABLE_TYPE_FACTORY.createResolvableType(type, resolver);
	}

	/**
	 * 根据数组元素类型创建数组类型的ResolvableType
	 * 
	 * @param componentType 数组元素的ResolvableType（不可为null）
	 * @return 数组类型的ResolvableType
	 */
	public static ResolvableType forArrayComponent(@NonNull ResolvableType componentType) {
		Class<?> arrayClass = Array.newInstance(componentType.getRawType(), 0).getClass();
		return forClassWithGenerics(arrayClass, componentType);
	}

	/**
	 * 创建带泛型参数的类类型，用于构建参数化类型
	 * 
	 * @param type     原始类（不可为null）
	 * @param generics 泛型参数数组
	 * @return 带泛型的GenericType实例
	 */
	public static GenericType forClassWithGenerics(Class<?> type, Type... generics) {
		GenericType genericType = new GenericType(type);
		genericType.setActualTypeArguments(generics);
		return genericType;
	}

	/**
	 * 将当前类型转换为指定类型的ResolvableType，支持接口和父类转换
	 * <p>
	 * 优先从接口中查找匹配类型，若未找到则从父类中查找
	 * </p>
	 * 
	 * @param type 目标类型（不可为null）
	 * @return 转换后的ResolvableType，转换失败返回NONE
	 */
	default ResolvableType as(Class<?> type) {
		if (this == NONE) {
			return NONE;
		}
		Class<?> resolved = getRawType();
		if (resolved == null || resolved == type) {
			return this;
		}
		for (ResolvableType interfaceType : getInterfaces()) {
			ResolvableType interfaceAsType = interfaceType.as(type);
			if (interfaceAsType != NONE) {
				return interfaceAsType;
			}
		}
		return getSuperType().as(type);
	}

	/**
	 * 获取指定索引的泛型参数，支持多级索引查询
	 * <p>
	 * 示例：getActualTypeArgument(0, 1) 表示获取第一层泛型的第0个参数的
	 * 第二层泛型的第1个参数
	 * </p>
	 * 
	 * @param indexes 泛型参数索引路径（可变参数）
	 * @return 泛型参数对应的ResolvableType，索引越界返回NONE
	 */
	default ResolvableType getActualTypeArgument(int... indexes) {
		ResolvableType[] generics = getActualTypeArguments();
		if (indexes == null || indexes.length == 0) {
			return (generics.length == 0 ? NONE : generics[0]);
		}
		ResolvableType generic = this;
		for (int index : indexes) {
			generics = generic.getActualTypeArguments();
			if (index < 0 || index >= generics.length) {
				return NONE;
			}
			generic = generics[index];
		}
		return generic;
	}

	/**
	 * 判断类型是否包含实际泛型参数
	 * 
	 * @return true表示包含实际泛型参数，false表示无泛型或未解析
	 */
	boolean hasActualTypeArguments();

	/**
	 * 获取实际泛型参数数组
	 * <p>
	 * 对于参数化类型（如List<String>），返回泛型参数数组（如[String]）
	 * 对于非参数化类型，返回空数组
	 * </p>
	 * 
	 * @return 泛型参数的ResolvableType数组，无泛型时返回空数组
	 */
	@Override
	ResolvableType[] getActualTypeArguments();

	/**
	 * 判断是否为数组类型
	 * 
	 * @return true表示数组类型，false表示非数组类型
	 */
	boolean isArray();

	/**
	 * 获取数组元素类型
	 * <p>
	 * 对于数组类型（如String[]），返回元素类型的ResolvableType（如String）
	 * 对于非数组类型，返回NONE
	 * </p>
	 * 
	 * @return 数组元素的ResolvableType，非数组类型返回NONE
	 */
	ResolvableType getComponentType();

	/**
	 * 获取当前类型实现的接口类型数组
	 * <p>
	 * 返回值为ResolvableType数组，包含所有接口的泛型信息
	 * 若无接口实现，返回EMPTY_TYPES_ARRAY
	 * </p>
	 * 
	 * @return 接口的ResolvableType数组
	 */
	default ResolvableType[] getInterfaces() {
		Class<?> resolved = getRawType();
		if (resolved == null) {
			return EMPTY_TYPES_ARRAY;
		}

		Type[] genericIfcs = resolved.getGenericInterfaces();
		return toResolvableTypes(this, genericIfcs);
	}

	/**
	 * 获取通配符类型的下界数组（如? super T中的T）
	 * <p>
	 * 对于非通配符类型，返回EMPTY_TYPES_ARRAY
	 * </p>
	 * 
	 * @return 下界的ResolvableType数组
	 */
	@Override
	ResolvableType[] getLowerBounds();

	/**
	 * 获取指定嵌套层级的类型，使用默认索引映射
	 * 
	 * @param nestingLevel 嵌套层级（从2开始，1表示当前层级）
	 * @return 嵌套后的ResolvableType，层级无效返回NONE
	 */
	default ResolvableType getNested(int nestingLevel) {
		return getNested(nestingLevel, null);
	}

	/**
	 * 获取指定嵌套层级的类型，支持自定义索引映射
	 * <p>
	 * 层级计算规则：
	 * - 数组类型每一层级降低一维（如String[][]为2级嵌套）
	 * - 泛型类型每一层级获取指定索引的泛型参数
	 * </p>
	 * 
	 * @param nestingLevel    嵌套层级（从2开始，1表示当前层级）
	 * @param typeIndexesPerLevel 层级-索引映射表，key为层级，value为对应索引
	 * @return 嵌套后的ResolvableType，层级无效返回NONE
	 */
	default ResolvableType getNested(int nestingLevel, Map<Integer, Integer> typeIndexesPerLevel) {
		ResolvableType result = this;
		for (int i = 2; i <= nestingLevel; i++) {
			if (result.isArray()) {
				result = result.getComponentType();
			} else {
				// 处理派生类型，逐级查找泛型参数
				while (result != ResolvableType.NONE && !result.hasActualTypeArguments()) {
					result = result.getSuperType();
				}
				Integer index = (typeIndexesPerLevel != null ? typeIndexesPerLevel.get(i) : null);
				index = (index == null ? result.getActualTypeArguments().length - 1 : index);
				result = result.getActualTypeArgument(index);
			}
		}
		return result;
	}

	/**
	 * 获取内部类的所有者类型（外部类）
	 * <p>
	 * 对于顶级类返回null，对于内部类返回外部类的ResolvableType
	 * </p>
	 * 
	 * @return 所有者类型的ResolvableType，无所有者返回null
	 */
	@Override
	ResolvableType getOwnerType();

	/**
	 * 获取原始类型（擦除泛型后的Class对象）
	 * <p>
	 * 对于参数化类型（如List<String>），返回原始类型List
	 * 对于基本类型包装类，返回对应的基本类型（如Integer返回int）
	 * </p>
	 * 
	 * @return 原始Class对象，类型解析失败返回null
	 */
	@Override
	Class<?> getRawType();

	/**
	 * 获取父类类型的ResolvableType
	 * <p>
	 * 处理泛型父类的类型信息，返回包含泛型参数的父类ResolvableType
	 * 若无父类（如Object）返回NONE
	 * </p>
	 * 
	 * @return 父类的ResolvableType，无父类返回NONE
	 */
	default ResolvableType getSuperType() {
		Class<?> resolved = getRawType();
		if (resolved == null) {
			return NONE;
		}

		try {
			Type superclass = resolved.getGenericSuperclass();
			if (superclass == null) {
				return NONE;
			}
			return forType(superclass, this);
		} catch (TypeNotPresentException ex) {
			return NONE;
		}
	}

	/**
	 * 获取完整类型名称（包含泛型信息）
	 * <p>
	 * 示例：
	 * - List<String> 返回 "java.util.List<java.lang.String>"
	 * - String[] 返回 "java.lang.String[]"
	 * - 无泛型类型返回原始类名
	 * </p>
	 * 
	 * @return 类型名称字符串，NONE返回"?"
	 */
	@Override
	default String getTypeName() {
		if (isArray()) {
			return getComponentType() + "[]";
		}
		if (this == NONE) {
			return "?";
		}

		String rawName = getRawType().getName();
		if (hasActualTypeArguments()) {
			ResolvableType[] actualTypeArguments = getActualTypeArguments();
			return rawName + '<' + Arrays.asList(actualTypeArguments).stream().map(Type::getTypeName)
					.collect(Collectors.joining(", ")) + '>';
		}
		return rawName;
	}

	/**
	 * 获取通配符类型的上界数组（如? extends T中的T）
	 * <p>
	 * 对于非通配符类型，返回包含Object的数组（? extends Object）
	 * </p>
	 * 
	 * @return 上界的ResolvableType数组
	 */
	@Override
	ResolvableType[] getUpperBounds();

	/**
	 * 判断当前类型是否可赋值给目标类型
	 * <p>
	 * 校验逻辑：
	 * 1. 数组类型：元素类型可赋值
	 * 2. 通配符类型：上下界兼容
	 * 3. 原始类型：继承或实现关系
	 * 4. 泛型参数：对应位置类型可赋值
	 * </p>
	 * 
	 * @param other 目标类型的ResolvableType
	 * @return true表示可赋值，false表示不可赋值
	 */
	default boolean isAssignableFrom(ResolvableType other) {
		if (isArray()) {
			return other.isArray() && getComponentType().isAssignableFrom(other.getComponentType());
		}

		// 校验下界兼容性
		for (ResolvableType leftBound : getLowerBounds()) {
			for (ResolvableType rightBound : other.getLowerBounds()) {
				if (!leftBound.isAssignableFrom(rightBound)) {
					return false;
				}
			}
		}

		// 校验上界兼容性
		for (ResolvableType leftBound : getUpperBounds()) {
			for (ResolvableType rightBound : other.getUpperBounds()) {
				if (!rightBound.isAssignableFrom(leftBound)) {
					return false;
				}
			}
		}

		// 校验原始类型兼容性
		if (hasActualTypeArguments() ? !ClassUtils.isAssignable(getRawType(), other.getRawType())
				: !ObjectUtils.equals(getRawType(), other.getRawType())) {
			return false;
		}

		// 校验泛型参数兼容性
		if (hasActualTypeArguments()) {
			ResolvableType[] actualTypeArguments = getActualTypeArguments();
			ResolvableType[] otherActualTypeArguments = other.as(getRawType()).getActualTypeArguments();
			if (actualTypeArguments.length != otherActualTypeArguments.length) {
				return false;
			}

			for (int i = 0; i < actualTypeArguments.length; i++) {
				if (!actualTypeArguments[i].isAssignableFrom(otherActualTypeArguments[i])) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 解析类型变量，获取其绑定的实际类型
	 * <p>
	 * 解析顺序：
	 * 1. 当前类型的类型参数中查找同名变量
	 * 2. 所有者类型（外部类）中查找
	 * 3. 上界类型中查找
	 * 4. 下界类型中查找
	 * </p>
	 * 
	 * @param typeVariable 待解析的类型变量
	 * @return 解析后的ResolvableType，未找到返回null
	 */
	@Override
	default ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable) {
		TypeVariable<?>[] typeParameters = getRawType().getTypeParameters();
		if (typeParameters.length != 0) {
			for (int i = 0; i < typeParameters.length; i++) {
				if (StringUtils.equals(typeParameters[i].getName(), typeVariable.getName())) {
					ResolvableType resolved = getActualTypeArgument(i);
					if (resolved != null && resolved != NONE) {
						return resolved;
					}
				}
			}
		}

		ResolvableType ownerType = getOwnerType();
		if (ownerType != null) {
			ResolvableType resolved = ownerType.resolveTypeVariable(typeVariable);
			if (resolved != null && resolved != NONE) {
				return resolved;
			}
		}

		for (ResolvableType bound : getUpperBounds()) {
			ResolvableType resolved = bound.resolveTypeVariable(typeVariable);
			if (resolved != null && resolved != NONE) {
				return resolved;
			}
		}

		for (ResolvableType bound : getLowerBounds()) {
			ResolvableType resolved = bound.resolveTypeVariable(typeVariable);
			if (resolved != null && resolved != NONE) {
				return resolved;
			}
		}
		return null;
	}
}
