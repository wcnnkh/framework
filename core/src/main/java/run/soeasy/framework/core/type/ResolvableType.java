package run.soeasy.framework.core.type;

import java.lang.reflect.Array;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
 * 可解析类型接口，提供泛型类型的解析、操作和查询功能。
 * <p>
 * 整合了{@link ParameterizedType}、{@link WildcardType}和{@link TypeVariableResolver}接口，
 * 用于处理Java泛型类型擦除后的类型信息解析，支持参数化类型、通配符类型、 类型变量和数组类型的解析与操作。
 */
public interface ResolvableType
		extends ParameterizedType, WildcardType, TypeVariableResolver, Comparable<ResolvableType> {

	/**
	 * 空类型数组常量，避免重复创建。
	 */
	ResolvableType[] EMPTY_TYPES_ARRAY = new ResolvableType[0];

	/**
	 * 可解析类型工厂，通过服务加载机制获取，支持扩展自定义类型解析实现。
	 * <p>
	 * 优先加载通过{@link ServiceLoader}注册的{@link ResolvableTypeFactory}实现，
	 * 无自定义实现时使用默认的{@link DefaultResolvableTypeFactory}。
	 */
	ResolvableTypeFactory RESOLVABLE_TYPE_FACTORY = CollectionUtils
			.unknownSizeStream(ServiceLoader.load(ResolvableTypeFactory.class).iterator()).findFirst()
			.orElse(DefaultResolvableTypeFactory.INSTANCE);

	/**
	 * 表示无类型的特殊实例，用于空类型场景的返回值。
	 */
	ResolvableType NONE = new NoneType(Object.class);

	/**
	 * 将原始类型数组转换为{@link ResolvableType}数组，支持类型变量解析器。
	 * 
	 * @param typeVariableResolver 类型变量解析器，用于处理泛型变量绑定
	 * @param types                原始类型数组
	 * @return {@link ResolvableType}数组，类型为{@code null}或空时返回{@link #EMPTY_TYPES_ARRAY}
	 */
	static ResolvableType[] toResolvableTypes(TypeVariableResolver typeVariableResolver, Type... types) {
		if (types == null || types.length == 0) {
			return EMPTY_TYPES_ARRAY;
		}

		ResolvableType[] typeProviders = new ResolvableType[types.length];
		for (int i = 0; i < typeProviders.length; i++) {
			typeProviders[i] = forType(types[i], typeVariableResolver);
		}
		return typeProviders;
	}

	/**
	 * 为可执行元素（方法/构造器）的参数类型创建带泛型解析能力的 ResolvableType 数组
	 * <p>
	 * 核心特性： 1. 自动集成可执行元素的完整泛型解析能力（通过
	 * {@link CompositeTypeVariableResolver#forExecutable(Executable)}），
	 * 支持解析方法/构造器级泛型（如 {@code <T> void method(T param)} 中的 T）和类级泛型（如 {@code class
	 * Service<T> { void method(T param) }} 中的 T） 2.
	 * 严格保持参数类型顺序与可执行元素声明一致，确保参数与解析结果一一对应 3. 依赖
	 * {@link #toResolvableTypes(TypeVariableResolver, Type...)}
	 * 完成类型转换，遵循框架统一的类型处理规则
	 * <p>
	 * 适用场景：反射获取方法/构造器参数类型时，需要保留泛型信息并自动解析泛型变量（如依赖注入参数匹配、动态代理参数处理、接口适配校验）
	 *
	 * @param executable 目标可执行元素（支持 {@link Method} 方法和
	 *                   {@link java.lang.reflect.Constructor} 构造器，不可为 null）
	 * @return 与参数声明顺序一致的 ResolvableType 数组，无参数时返回 {@link #EMPTY_TYPES_ARRAY}（非
	 *         null）
	 * @see #toResolvableTypes(TypeVariableResolver, Type...)
	 * @see CompositeTypeVariableResolver#forExecutable(Executable)
	 */
	static ResolvableType[] forExecutableParameterTypes(@NonNull Executable executable) {
		return toResolvableTypes(CompositeTypeVariableResolver.forExecutable(executable),
				executable.getParameterTypes());
	}

	/**
	 * 为可执行元素（方法/构造器）的返回类型创建带泛型解析能力的 ResolvableType
	 * <p>
	 * 差异化处理逻辑： 1. 若为 {@link Method} 方法：使用 {@link Method#getGenericReturnType()}
	 * 获取带泛型的返回类型（如 {@code List<String>}）， 结合可执行元素的泛型解析器解析其中的类型变量（如方法级泛型、类级泛型） 2. 若为
	 * {@link java.lang.reflect.Constructor} 构造器：返回类型默认为构造器所在类的类型（如
	 * {@code new UserService()} 的返回类型为 {@code UserService}）， 支持解析类级泛型变量（如
	 * {@code class UserService<T>} 的返回类型为 {@code UserService<T>} 并解析 T）
	 * <p>
	 * 适用场景：反射获取方法返回类型或构造器"隐式返回类型"时，需要保留泛型信息（如序列化结果类型匹配、接口返回值适配、动态代理返回值生成）
	 *
	 * @param executable 目标可执行元素（支持 {@link Method} 方法和
	 *                   {@link java.lang.reflect.Constructor} 构造器，不可为 null）
	 * @return 可执行元素返回类型对应的 ResolvableType（非 null），构造器返回其声明类的泛型解析结果
	 * @see #forType(Type, TypeVariableResolver)
	 * @see CompositeTypeVariableResolver#forExecutable(Executable)
	 */
	static ResolvableType forExecutableReturnType(@NonNull Executable executable) {
		TypeVariableResolver typeVariableResolver = CompositeTypeVariableResolver.forExecutable(executable);
		if (executable instanceof Method) {
			return ResolvableType.forType(((Method) executable).getGenericReturnType(), typeVariableResolver);
		}
		return ResolvableType.forType(executable.getDeclaringClass(), typeVariableResolver);
	}

	/**
	 * 为字段创建带泛型解析能力的 ResolvableType
	 * <p>
	 * 核心特性： 1. 使用 {@link Field#getGenericType()} 获取字段的泛型类型（如
	 * {@code List<T>}），而非原始类型（{@code List}），确保泛型信息不丢失 2. 传入字段所在类的 ResolvableType
	 * 作为泛型解析器，自动解析字段类型中的类级泛型变量（如 {@code class UserService<T> { private T value; }}
	 * 中的 T） 3. 遵循框架统一的类型转换规则，与其他 {@link ResolvableType} 创建方法语义一致
	 * <p>
	 * 适用场景：反射获取字段类型时，需要保留泛型信息并解析类级泛型（如 ORM 字段类型映射、配置属性绑定、序列化/反序列化字段处理）
	 *
	 * @param field 目标字段（不可为 null）
	 * @return 字段泛型类型对应的 ResolvableType（非 null），包含类级泛型变量的解析结果
	 * @see #forType(Type, TypeVariableResolver)
	 * @see Field#getGenericType()
	 */
	static ResolvableType forField(@NonNull Field field) {
		return ResolvableType.forType(field.getGenericType(), ResolvableType.forType(field.getDeclaringClass()));
	}

	/**
	 * 为方法/构造器的参数（{@link Parameter}）创建带泛型解析能力的 ResolvableType
	 * <p>
	 * 核心特性：
	 * 1. 基于 {@link Parameter#getParameterizedType()} 获取参数的泛型类型（如 {@code List<String>}、{@code T}），确保泛型信息不丢失
	 * 2. 自动集成参数所属可执行元素（方法/构造器）的完整泛型解析能力（通过 {@link CompositeTypeVariableResolver#forExecutable(Executable)}），
	 *    支持解析方法/构造器级泛型（如 {@code <T> void method(T param)} 中的 T）和类级泛型（如 {@code class Service<T> { void method(T param) }} 中的 T）
	 * 3. 与 {@link #forExecutableParameterTypes(Executable)} 语义一致，专为单个 {@link Parameter} 实例提供精准解析
	 * <p>
	 * 适用场景：反射获取单个方法/构造器参数的类型时，需要保留泛型信息并自动解析泛型变量（如参数校验、注解处理器解析参数类型、动态参数绑定）
	 *
	 * @param parameter 目标参数实例（不可为 null），关联方法/构造器的单个参数
	 * @return 该参数泛型类型对应的 ResolvableType（非 null），包含泛型变量的完整解析结果
	 * @see #forType(Type, TypeVariableResolver)
	 * @see Parameter#getParameterizedType()
	 * @see CompositeTypeVariableResolver#forExecutable(Executable)
	 * @see #forExecutableParameterTypes(Executable)
	 */
	static ResolvableType forParameter(@NonNull Parameter parameter) {
	    return ResolvableType.forType(parameter.getParameterizedType(),
	            CompositeTypeVariableResolver.forExecutable(parameter.getDeclaringExecutable()));
	}

	/**
	 * 根据原始类型创建{@link ResolvableType}实例，使用默认类型变量解析器。
	 * 
	 * @param type 原始类型（不可为{@code null}）
	 * @return {@link ResolvableType}实例
	 */
	static ResolvableType forType(@NonNull Type type) {
		return forType(type, null);
	}

	/**
	 * 根据原始类型和类型变量解析器创建{@link ResolvableType}实例。
	 * 
	 * @param type     原始类型（不可为{@code null}）
	 * @param resolver 类型变量解析器（可为{@code null}，使用默认解析器）
	 * @return {@link ResolvableType}实例
	 */
	static ResolvableType forType(@NonNull Type type, TypeVariableResolver resolver) {
		if (type instanceof ResolvableType) {
			return (ResolvableType) type;
		}
		return RESOLVABLE_TYPE_FACTORY.createResolvableType(type, resolver);
	}

	/**
	 * 根据数组元素类型创建数组类型的{@link ResolvableType}。
	 * 
	 * @param componentType 数组元素的{@link ResolvableType}（不可为{@code null}）
	 * @return 数组类型的{@link ResolvableType}
	 */
	static ResolvableType forArrayComponent(@NonNull ResolvableType componentType) {
		Class<?> arrayClass = Array.newInstance(componentType.getRawType(), 0).getClass();
		return new ArrayType(arrayClass, componentType, componentType);
	}

	/**
	 * 创建带泛型参数的类类型，用于构建参数化类型。
	 * 
	 * @param type     原始类（不可为{@code null}）
	 * @param generics 泛型参数数组
	 * @return 带泛型的{@link GenericType}实例
	 */
	static GenericType forClassWithGenerics(Class<?> type, Type... generics) {
		GenericType genericType = new GenericType(type);
		genericType.setActualTypeArguments(generics);
		return genericType;
	}

	/**
	 * 判断两个类型是否存在赋值兼容性（即右类型是否可赋值给左类型）
	 * 
	 * 该方法通过ResolvableType处理泛型等复杂类型，提供比普通Class.isAssignableFrom()更全面的类型兼容性检查
	 *
	 * @param leftType  目标类型（赋值的接收方类型），不能为null
	 * @param rightType 源类型（赋值的提供方类型），不能为null
	 * @return 如果rightType可赋值给leftType则返回true，否则返回false
	 */
	public static boolean isAssignable(@NonNull Type leftType, @NonNull Type rightType) {
		// 将左类型转换为可解析类型，支持泛型等复杂类型处理
		ResolvableType leftResolvableType = ResolvableType.forType(leftType);
		// 将右类型转换为可解析类型，支持泛型等复杂类型处理
		ResolvableType rightResolvableType = ResolvableType.forType(rightType);

		// 检查左类型是否可从右类型赋值（即右类型是否与左类型兼容）
		return leftResolvableType.isAssignableFrom(rightResolvableType);
	}

	/**
	 * 将当前类型转换为指定类型的{@link ResolvableType}，支持接口和父类转换。
	 * <p>
	 * 优先从接口中查找匹配类型，若未找到则从父类中查找。
	 * 
	 * @param type 目标类型（不可为{@code null}）
	 * @return 转换后的{@link ResolvableType}，转换失败返回{@link #NONE}
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
	 * 获取指定索引的泛型参数，支持多级索引查询。
	 * <p>
	 * 示例：{@code getActualTypeArgument(0, 1)} 表示获取第一层泛型的第0个参数的 第二层泛型的第1个参数。
	 * 
	 * @param indexes 泛型参数索引路径（可变参数）
	 * @return 泛型参数对应的{@link ResolvableType}，索引越界返回{@link #NONE}
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
	 * 判断类型是否包含实际泛型参数。
	 * 
	 * @return {@code true}表示包含实际泛型参数，{@code false}表示无泛型或未解析
	 */
	boolean hasActualTypeArguments();

	/**
	 * 获取实际泛型参数数组。
	 * <p>
	 * 对于参数化类型（如{@code List<java.lang.String>}），返回泛型参数数组（如{@code [java.lang.String]}）；
	 * 对于非参数化类型，返回空数组。
	 * 
	 * @return 泛型参数的{@link ResolvableType}数组，无泛型时返回空数组
	 */
	ResolvableType[] getActualTypeArguments();

	/**
	 * 判断是否为数组类型。
	 * 
	 * @return {@code true}表示数组类型，{@code false}表示非数组类型
	 */
	boolean isArray();

	/**
	 * 获取数组元素类型。
	 * <p>
	 * 对于数组类型（如{@code java.lang.String[]}），返回元素类型的{@link ResolvableType}（如{@code java.lang.String}）；
	 * 对于非数组类型，返回{@link #NONE}。
	 * 
	 * @return 数组元素的{@link ResolvableType}，非数组类型返回{@link #NONE}
	 */
	ResolvableType getComponentType();

	/**
	 * 获取当前类型实现的接口类型数组。
	 * <p>
	 * 返回值为{@link ResolvableType}数组，包含所有接口的泛型信息；
	 * 若无接口实现，返回{@link #EMPTY_TYPES_ARRAY}。
	 * 
	 * @return 接口的{@link ResolvableType}数组
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
	 * 获取通配符类型的下界数组（如{@code ? super T}中的{@code T}）。
	 * <p>
	 * 对于非通配符类型，返回{@link #EMPTY_TYPES_ARRAY}。
	 * 
	 * @return 下界的{@link ResolvableType}数组
	 */
	ResolvableType[] getLowerBounds();

	/**
	 * 获取指定嵌套层级的类型，使用默认索引映射。
	 * 
	 * @param nestingLevel 嵌套层级（从2开始，1表示当前层级）
	 * @return 嵌套后的{@link ResolvableType}，层级无效返回{@link #NONE}
	 */
	default ResolvableType getNested(int nestingLevel) {
		return getNested(nestingLevel, null);
	}

	/**
	 * 获取指定嵌套层级的类型，支持自定义索引映射。
	 * <p>
	 * 层级计算规则：
	 * <ul>
	 * <li>数组类型每一层级降低一维（如{@code String[][]}为2级嵌套）</li>
	 * <li>泛型类型每一层级获取指定索引的泛型参数</li>
	 * </ul>
	 * 
	 * @param nestingLevel        嵌套层级（从2开始，1表示当前层级）
	 * @param typeIndexesPerLevel 层级-索引映射表，key为层级，value为对应索引
	 * @return 嵌套后的{@link ResolvableType}，层级无效返回{@link #NONE}
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
	 * 获取内部类的所有者类型（外部类）。
	 * <p>
	 * 对于顶级类返回{@code null}，对于内部类返回外部类的{@link ResolvableType}。
	 * 
	 * @return 所有者类型的{@link ResolvableType}，无所有者返回{@code null}
	 */
	ResolvableType getOwnerType();

	/**
	 * 获取原始类型（擦除泛型后的{@link Class}对象）。
	 * <p>
	 * 对于参数化类型（如{@code List<java.lang.String>}），返回原始类型{@code List}；
	 * 对于基本类型包装类，返回对应的基本类型（如{@code Integer}返回{@code int}）。
	 * 
	 * @return 原始{@link Class}对象，类型解析失败返回{@code null}
	 */
	Class<?> getRawType();

	/**
	 * 获取父类类型的{@link ResolvableType}。
	 * <p>
	 * 处理泛型父类的类型信息，返回包含泛型参数的父类{@link ResolvableType}；
	 * 若无父类（如{@code Object}）返回{@link #NONE}。
	 * 
	 * @return 父类的{@link ResolvableType}，无父类返回{@link #NONE}
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
	 * 获取完整类型名称（包含泛型信息）。
	 * <p>
	 * 示例：
	 * <ul>
	 * <li>{@code List<String>} 返回 "{@code java.util.List<java.lang.String>}"</li>
	 * <li>{@code String[]} 返回 "{@code java.lang.String[]}"</li>
	 * <li>无泛型类型返回原始类名</li>
	 * </ul>
	 * 
	 * @return 类型名称字符串，{@link #NONE}返回"{@code ?}"
	 */
	default String getTypeName() {
		if (isArray()) {
			ResolvableType component = getComponentType();
			String componentName = (component == null || component == NONE) ? "?" : component.getTypeName();
			return componentName + "[]";
		}

		if (this == NONE) {
			return "?";
		}

		Class<?> rawType = getRawType();
		if (rawType == null) {
			return "?"; // 或返回"?"，根据设计预期调整
		}
		String rawName = rawType.getName();
		if (hasActualTypeArguments()) {
			ResolvableType[] actualTypeArguments = getActualTypeArguments();
			return rawName + '<' + Arrays.asList(actualTypeArguments).stream().map(Type::getTypeName)
					.collect(Collectors.joining(", ")) + '>';
		}
		return rawName;
	}

	/**
	 * 获取通配符类型的上界数组（如{@code ? extends T}中的{@code T}）。
	 * <p>
	 * 对于非通配符类型，返回包含{@code Object}的数组（{@code ? extends Object}）。
	 * 
	 * @return 上界的{@link ResolvableType}数组
	 */
	ResolvableType[] getUpperBounds();

	/**
	 * 判断当前类型是否可赋值给目标类型。
	 * <p>
	 * 校验逻辑：
	 * <ol>
	 * <li>数组类型：元素类型可赋值</li>
	 * <li>通配符类型：上下界兼容</li>
	 * <li>原始类型：继承或实现关系</li>
	 * <li>泛型参数：对应位置类型可赋值</li>
	 * </ol>
	 * 
	 * @param other 目标类型的{@link ResolvableType}
	 * @return {@code true}表示可赋值，{@code false}表示不可赋值
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
	 * 解析类型变量，获取其绑定的实际类型。
	 * <p>
	 * 解析顺序：
	 * <ol>
	 * <li>当前类型的类型参数中查找同名变量</li>
	 * <li>所有者类型（外部类）中查找</li>
	 * <li>上界类型中查找</li>
	 * <li>下界类型中查找</li>
	 * </ol>
	 * 
	 * @param typeVariable 待解析的类型变量
	 * @return 解析后的{@link ResolvableType}，未找到返回{@code null}
	 */
	default ResolvableType resolveTypeVariable(TypeVariable<?> typeVariable) {
		Class<?> rawType = getRawType();
		if (rawType != null) {
			TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
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

	@Override
	default int compareTo(ResolvableType o) {
		// 1. 处理 null：当前非 null 时优先级更高
		if (o == null) {
			return 1;
		}

		// 2. 处理 NONE：普通类型优先级高于 NONE
		boolean isThisNone = this == NONE;
		boolean isONone = o == NONE;
		if (isThisNone != isONone) {
			return isThisNone ? -1 : 1;
		}
		if (isThisNone && isONone) {
			return 0;
		}

		// 3. 比较原始类型（核心维度，类名字典序保证稳定性）
		Class<?> thisRaw = this.getRawType();
		Class<?> oRaw = o.getRawType();
		int rawTypeCompare = 0;
		if (thisRaw == null && oRaw == null) {
			rawTypeCompare = 0;
		} else if (thisRaw == null) {
			rawTypeCompare = -1;
		} else if (oRaw == null) {
			rawTypeCompare = 1;
		} else {
			rawTypeCompare = thisRaw.getName().compareTo(oRaw.getName());
		}
		if (rawTypeCompare != 0) {
			return rawTypeCompare;
		}

		// 4. 比较是否为数组：数组优先级高于非数组
		boolean isThisArray = this.isArray();
		boolean isOArray = o.isArray();
		if (isThisArray != isOArray) {
			return isThisArray ? 1 : -1;
		}

		// 5. 数组组件类型比较（仅数组场景，递归调用 compareTo）
		if (isThisArray) {
			ResolvableType thisComponent = this.getComponentType();
			ResolvableType oComponent = o.getComponentType();
			// 处理组件为 NONE 的情况
			if (thisComponent == NONE && oComponent == NONE) {
				// 组件均为 NONE，继续后续比较
			} else if (thisComponent == NONE) {
				return -1;
			} else if (oComponent == NONE) {
				return 1;
			} else {
				int componentCompare = thisComponent.compareTo(oComponent);
				if (componentCompare != 0) {
					return componentCompare;
				}
			}
		}

		// 6. 泛型参数比较（先个数，再逐个递归比较）
		boolean thisHasGenerics = this.hasActualTypeArguments();
		boolean oHasGenerics = o.hasActualTypeArguments();
		if (thisHasGenerics != oHasGenerics) {
			return thisHasGenerics ? 1 : -1;
		}
		if (thisHasGenerics && oHasGenerics) {
			ResolvableType[] thisGenerics = this.getActualTypeArguments();
			ResolvableType[] oGenerics = o.getActualTypeArguments();
			// 比较泛型参数个数
			if (thisGenerics.length != oGenerics.length) {
				return Integer.compare(thisGenerics.length, oGenerics.length);
			}
			// 逐个递归比较泛型参数
			for (int i = 0; i < thisGenerics.length; i++) {
				ResolvableType thisGeneric = thisGenerics[i];
				ResolvableType oGeneric = oGenerics[i];
				// 处理泛型参数为 NONE 的情况
				if (thisGeneric == NONE && oGeneric == NONE) {
					continue;
				} else if (thisGeneric == NONE) {
					return -1;
				} else if (oGeneric == NONE) {
					return 1;
				}
				int genericCompare = thisGeneric.compareTo(oGeneric);
				if (genericCompare != 0) {
					return genericCompare;
				}
			}
		}

		// 7. 通配符上界比较（逐个递归比较）
		ResolvableType[] thisUpperBounds = this.getUpperBounds();
		ResolvableType[] oUpperBounds = o.getUpperBounds();
		// 比较上界数组长度
		if (thisUpperBounds.length != oUpperBounds.length) {
			return Integer.compare(thisUpperBounds.length, oUpperBounds.length);
		}
		// 逐个递归比较上界
		for (int i = 0; i < thisUpperBounds.length; i++) {
			ResolvableType thisUpper = thisUpperBounds[i];
			ResolvableType oUpper = oUpperBounds[i];
			// 处理上界为 NONE 的情况
			if (thisUpper == NONE && oUpper == NONE) {
				continue;
			} else if (thisUpper == NONE) {
				return -1;
			} else if (oUpper == NONE) {
				return 1;
			}
			int upperCompare = thisUpper.compareTo(oUpper);
			if (upperCompare != 0) {
				return upperCompare;
			}
		}

		// 8. 通配符下界比较（逐个递归比较）
		ResolvableType[] thisLowerBounds = this.getLowerBounds();
		ResolvableType[] oLowerBounds = o.getLowerBounds();
		// 比较下界数组长度
		if (thisLowerBounds.length != oLowerBounds.length) {
			return Integer.compare(thisLowerBounds.length, oLowerBounds.length);
		}
		// 逐个递归比较下界
		for (int i = 0; i < thisLowerBounds.length; i++) {
			ResolvableType thisLower = thisLowerBounds[i];
			ResolvableType oLower = oLowerBounds[i];
			// 处理下界为 NONE 的情况
			if (thisLower == NONE && oLower == NONE) {
				continue;
			} else if (thisLower == NONE) {
				return -1;
			} else if (oLower == NONE) {
				return 1;
			}
			int lowerCompare = thisLower.compareTo(oLower);
			if (lowerCompare != 0) {
				return lowerCompare;
			}
		}

		// 所有维度一致，返回 0（equals 必为 true）
		return 0;
	}
}