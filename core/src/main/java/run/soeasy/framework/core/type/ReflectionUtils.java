package run.soeasy.framework.core.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import run.soeasy.framework.core.collection.ArrayUtils;
import run.soeasy.framework.core.comparator.ExecutableMatchComparator;
import run.soeasy.framework.core.function.ThrowingFunction;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 反射工具类，提供一系列简化Java反射操作的静态方法。 该类封装了反射相关的常用操作，包括类成员查找、访问权限设置、方法调用等，
 * 适用于需要频繁使用反射的框架开发、工具类库和应用程序。
 *
 * <p>
 * 核心特性：
 * <ul>
 * <li>类成员查找：支持查找构造函数、字段、方法等类成员</li>
 * <li>访问权限管理：自动处理反射对象的可访问性设置</li>
 * <li>异常处理：统一处理反射操作中的异常，简化异常处理逻辑</li>
 * <li>泛型支持：通过{@link ClassMembersLoader}支持泛型化的类成员加载</li>
 * <li>方法调用：提供便捷的方法调用和字段操作接口</li>
 * </ul>
 *
 * <p>
 * 使用场景：
 * <ul>
 * <li>框架开发：实现ORM、依赖注入等需要反射的框架</li>
 * <li>动态代理：创建和操作代理对象</li>
 * <li>类分析工具：分析类结构和成员信息</li>
 * <li>动态实例化：运行时动态创建对象实例</li>
 * <li>测试框架：在单元测试中动态操作对象</li>
 * </ul>
 *
 * <p>
 * 示例用法：
 * 
 * <pre class="code">
 * // 查找类的构造函数
 * ClassMembersLoader&lt;Constructor&lt;User&gt;&gt; constructors = ReflectionUtils.getDeclaredConstructors(User.class);
 * 
 * // 调用方法
 * Method setNameMethod = ReflectionUtils.findDeclaredMethod(User.class, "setName", String.class).first();
 * User user = new User();
 * ReflectionUtils.invoke(setNameMethod, user, "张三");
 * 
 * // 访问字段
 * Field idField = ReflectionUtils.findDeclaredField(User.class, "id").first();
 * ReflectionUtils.set(idField, user, 1L);
 * </pre>
 *
 * @author soeasy.run
 */
@UtilityClass
public class ReflectionUtils {
	/** 空构造函数数组常量 */
	public static final Constructor<?>[] EMPTY_CONSTRUCTOR_ARRAY = new Constructor<?>[0];

	/** 空字段数组常量 */
	public static final Field[] EMPTY_FIELD_ARRAY = new Field[0];

	/** 空方法数组常量 */
	public static final Method[] EMPTY_METHOD_ARRAY = new Method[0];

	/**
	 * 在类及其继承体系中查找符合条件的类成员。
	 * <p>
	 * 该方法会递归查找当前类、父类和接口中的成员，使用提供的查找函数进行匹配。
	 * 查找结果通过{@link ClassMembersLoader}封装，支持延迟加载和递归遍历。
	 *
	 * @param <T>    类成员类型
	 * @param clazz  要查找的类
	 * @param finder 查找函数，输入类返回对应的成员
	 * @return 类成员加载器，包含查找结果
	 */
	public static <T> ClassMembersLoader<T> find(@NonNull Class<?> clazz,
			@NonNull ThrowingFunction<? super Class<?>, ? extends T, ? extends ReflectiveOperationException> finder) {
		return search(clazz, (e) -> {
			T element = finder.apply(e);
			return element == null ? Streamable.empty() : Streamable.singleton(element);
		});
	}

	/**
	 * 查找类的指定构造函数。
	 * <p>
	 * 该方法会在类及其父类中查找匹配参数类型的公共构造函数。
	 *
	 * @param <T>            类类型
	 * @param clazz          要查找的类
	 * @param parameterTypes 构造函数参数类型
	 * @return 构造函数加载器
	 */
	public static <T> ClassMembersLoader<Constructor<?>> findConstructor(@NonNull Class<?> clazz,
			@NonNull Class<?>... parameterTypes) {
		return find(clazz, (e) -> e.getConstructor(parameterTypes));
	}

	/**
	 * 查找类的指定声明构造函数。
	 * <p>
	 * 该方法会在类自身中查找匹配参数类型的构造函数，不查找父类。
	 *
	 * @param <T>            类类型
	 * @param clazz          要查找的类
	 * @param parameterTypes 构造函数参数类型
	 * @return 构造函数加载器
	 */
	public static <T> ClassMembersLoader<Constructor<?>> findDeclaredConstructor(@NonNull Class<?> clazz,
			@NonNull Class<?>... parameterTypes) {
		return find(clazz, (e) -> e.getDeclaredConstructor(parameterTypes));
	}

	/**
	 * 查找类的指定声明字段。
	 * <p>
	 * 该方法会在类自身中查找指定名称的字段，不查找父类。
	 *
	 * @param <T>   类类型
	 * @param clazz 要查找的类
	 * @param name  字段名称
	 * @return 字段加载器
	 */
	public static <T> ClassMembersLoader<Field> findDeclaredField(@NonNull Class<?> clazz, @NonNull String name) {
		return find(clazz, (e) -> e.getDeclaredField(name));
	}

	/**
	 * 查找类的指定声明方法。
	 * <p>
	 * 该方法会在类自身中查找指定名称和参数的方法，不查找父类。
	 *
	 * @param <T>            类类型
	 * @param clazz          要查找的类
	 * @param name           方法名称
	 * @param parameterTypes 方法参数类型
	 * @return 方法加载器
	 */
	public static <T> ClassMembersLoader<Method> findDeclaredMethod(@NonNull Class<?> clazz, @NonNull String name,
			Class<?>... parameterTypes) {
		return find(clazz, (e) -> e.getDeclaredMethod(name, parameterTypes));
	}

	/**
	 * 查找类的指定字段。
	 * <p>
	 * 该方法会在类及其父类中查找指定名称的公共字段。
	 *
	 * @param <T>   类类型
	 * @param clazz 要查找的类
	 * @param name  字段名称
	 * @return 字段加载器
	 */
	public static <T> ClassMembersLoader<Field> findField(@NonNull Class<?> clazz, @NonNull String name) {
		return find(clazz, (e) -> e.getField(name));
	}

	/**
	 * 查找类的指定方法。
	 * <p>
	 * 该方法会在类及其父类中查找指定名称和参数的公共方法。
	 *
	 * @param <T>            类类型
	 * @param clazz          要查找的类
	 * @param name           方法名称
	 * @param parameterTypes 方法参数类型
	 * @return 方法加载器
	 */
	public static <T> ClassMembersLoader<Method> findMethod(@NonNull Class<?> clazz, @NonNull String name,
			@NonNull Class<?>... parameterTypes) {
		return find(clazz, (e) -> e.getMethod(name, parameterTypes));
	}

	/**
	 * 获取字段的值。
	 * <p>
	 * 该方法会自动设置字段的可访问性，并获取字段的值。 对于基本类型字段，会自动包装为对应的包装类型。
	 *
	 * @param field  字段对象
	 * @param target 目标对象
	 * @return 字段的值
	 */
	public static Object get(Field field, Object target) {
		makeAccessible(field);
		try {
			return field.get(target);
		} catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * 获取对象的clone方法。
	 * <p>
	 * 该方法会查找对象的clone方法，并验证其返回类型是否兼容。
	 *
	 * @param source 实现了Cloneable接口的对象
	 * @return clone方法，找不到时返回null
	 */
	public static Method getCloneMethod(Cloneable source) {
		if (source == null) {
			return null;
		}

		Method method = findMethod(source.getClass(), "clone").withSuperclass().first();
		if (method == null) {
			return null;
		}

		if (ClassUtils.isAssignableValue(method.getReturnType(), source)) {
			return method;
		}
		return null;
	}

	/**
	 * 获取类的所有构造函数。
	 * <p>
	 * 该方法会获取类的所有公共构造函数，包括从父类继承的构造函数。
	 *
	 * @param sourceClass 类对象
	 * @return 构造函数加载器
	 */
	public static ClassMembersLoader<Constructor<?>> getConstructors(Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getConstructors);
	}

	/**
	 * 获取类的所有声明构造函数。
	 * <p>
	 * 该方法会获取类自身声明的所有构造函数，不包括从父类继承的构造函数。
	 *
	 * @param sourceClass 类对象
	 * @return 构造函数加载器
	 */
	public static ClassMembersLoader<Constructor<?>> getDeclaredConstructors(Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getDeclaredConstructors);
	}

	/**
	 * 获取类的所有声明字段。
	 * <p>
	 * 该方法会获取类自身声明的所有字段，不包括从父类继承的字段。
	 *
	 * @param sourceClass 类对象
	 * @return 字段加载器
	 */
	public static ClassMembersLoader<Field> getDeclaredFields(@NonNull Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getDeclaredFields);
	}

	/**
	 * 获取类的所有声明方法。
	 * <p>
	 * 该方法会获取类自身声明的所有方法，不包括从父类继承的方法。
	 *
	 * @param sourceClass 类对象
	 * @return 方法加载器
	 */
	public static ClassMembersLoader<Method> getDeclaredMethods(Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getDeclaredMethods);
	}

	/**
	 * 获取类的所有字段。
	 * <p>
	 * 该方法会获取类的所有公共字段，包括从父类继承的字段。
	 *
	 * @param sourceClass 类对象
	 * @return 字段加载器
	 */
	public static ClassMembersLoader<Field> getFields(@NonNull Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getFields);
	}

	/**
	 * 获取类的所有成员。
	 * <p>
	 * 该方法通过提供的加载函数获取类的指定类型成员。
	 *
	 * @param <T>    成员类型
	 * @param clazz  类对象
	 * @param loader 成员加载函数
	 * @return 成员加载器
	 */
	public static <T> ClassMembersLoader<T> getMembers(@NonNull Class<?> clazz,
			@NonNull ThrowingFunction<? super Class<?>, ? extends T[], ? extends ReflectiveOperationException> loader) {
		return search(clazz, (e) -> {
			T[] array = loader.apply(e);
			return ArrayUtils.isEmpty(array) ? Streamable.empty() : Streamable.array(array);
		});
	}

	/**
	 * 获取类的所有方法。
	 * <p>
	 * 该方法会获取类的所有公共方法，包括从父类继承的方法。
	 *
	 * @param sourceClass 类对象
	 * @return 方法加载器
	 */
	public static ClassMembersLoader<Method> getMethods(Class<?> sourceClass) {
		return getMembers(sourceClass, Class::getMethods);
	}

	/**
	 * 处理调用目标异常。
	 * <p>
	 * 该方法会重新抛出目标异常，适用于不需要处理检查异常的场景。
	 *
	 * @param ex 调用目标异常
	 */
	public static void handleInvocationTargetException(InvocationTargetException ex) {
		rethrowRuntimeException(ex.getTargetException());
	}

	/**
	 * 处理反射异常。
	 * <p>
	 * 该方法会将反射异常转换为运行时异常或错误，便于上层调用处理。
	 *
	 * @param ex 反射异常
	 */
	public static void handleReflectionException(Exception ex) {
		if (ex instanceof ReflectiveOperationException) {
			throw new IllegalStateException("Reflective operation: " + ex.getMessage());
		}
		if (ex instanceof IllegalAccessException) {
			throw new IllegalStateException("Could not access: " + ex.getMessage());
		}
		if (ex instanceof InvocationTargetException) {
			handleInvocationTargetException((InvocationTargetException) ex);
		}
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * 处理可抛出异常。
	 * <p>
	 * 该方法会将异常转换为运行时异常或错误，便于上层调用处理。
	 *
	 * @param ex 异常对象
	 */
	public static void handleThrowable(Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		} else if (ex instanceof Exception) {
			handleReflectionException((Exception) ex);
		} else {
			throw new UndeclaredThrowableException(ex);
		}
	}

	/**
	 * 调用方法。
	 * <p>
	 * 该方法会自动设置方法的可访问性，并调用方法。
	 *
	 * @param method 方法对象
	 * @param target 目标对象
	 * @param args   方法参数
	 * @return 方法返回值
	 */
	public static Object invoke(Method method, Object target, Object... args) {
		makeAccessible(method);
		try {
			return method.invoke(target, args == null ? new Object[0] : args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * 判断方法是否为默认方法。
	 * <p>
	 * 默认方法是接口中声明的非抽象、非静态的公共实例方法。
	 *
	 * @param method 方法对象
	 * @return 是否为默认方法
	 */
	public static boolean isDefault(Method method) {
		return ((method.getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC)
				&& method.getDeclaringClass().isInterface();
	}

	/**
	 * 判断方法是否为equals方法。
	 * <p>
	 * 检查方法名称是否为"equals"，且参数类型为Object。
	 *
	 * @param method 方法对象
	 * @return 是否为equals方法
	 */
	public static boolean isEqualsMethod(Method method) {
		if (method == null || !method.getName().equals("equals")) {
			return false;
		}
		Class<?>[] paramTypes = method.getParameterTypes();
		return (paramTypes.length == 1 && paramTypes[0] == Object.class);
	}

	/**
	 * 判断方法是否为hashCode方法。
	 * <p>
	 * 检查方法名称是否为"hashCode"，且没有参数。
	 *
	 * @param method 方法对象
	 * @return 是否为hashCode方法
	 */
	public static boolean isHashCodeMethod(Method method) {
		return (method != null && method.getName().equals("hashCode") && method.getParameterCount() == 0);
	}

	/**
	 * 判断方法是否为Object类的方法。
	 * <p>
	 * 检查方法是否在Object类中声明。
	 *
	 * @param method 方法对象
	 * @return 是否为Object类的方法
	 */
	public static boolean isObjectMethod(Method method) {
		return !findMethod(Object.class, method.getName(), method.getParameterTypes()).isEmpty();
	}

	/**
	 * 判断方法是否为toString方法。
	 * <p>
	 * 检查方法名称是否为"toString"，且没有参数。
	 *
	 * @param method 方法对象
	 * @return 是否为toString方法
	 */
	public static boolean isToStringMethod(Method method) {
		return (method != null && method.getName().equals("toString") && method.getParameterCount() == 0);
	}

	/**
	 * 设置构造函数的可访问性。
	 * <p>
	 * 该方法会自动设置构造函数的可访问性，以便于反射调用。
	 *
	 * @param ctor 构造函数对象
	 */
	public static void makeAccessible(Constructor<?> ctor) {
		if (ctor == null) {
			return;
		}

		// JDK 9 中isAccessible()方法已被弃用，改为直接检查访问修饰符
		if (!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) {
			ctor.setAccessible(true);
		}
	}

	/**
	 * 设置字段的可访问性。
	 * <p>
	 * 该方法会自动设置字段的可访问性，以便于反射访问。
	 *
	 * @param field 字段对象
	 */
	public static void makeAccessible(Field field) {
		if (field == null) {
			return;
		}

		// JDK 9 中isAccessible()方法已被弃用，改为直接检查访问修饰符
		if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())
				|| Modifier.isFinal(field.getModifiers())) {
			field.setAccessible(true);
		}
	}

	/**
	 * 设置方法的可访问性。
	 * <p>
	 * 该方法会自动设置方法的可访问性，以便于反射调用。
	 *
	 * @param method 方法对象
	 */
	public static void makeAccessible(Method method) {
		if (method == null) {
			return;
		}
		// JDK 9 中isAccessible()方法已被弃用，改为直接检查访问修饰符
		if (!Modifier.isPublic(method.getModifiers())
				|| !Modifier.isPublic(method.getDeclaringClass().getModifiers())) {
			method.setAccessible(true);
		}
	}

	/**
	 * 通过构造函数创建实例。
	 * <p>
	 * 该方法会自动设置构造函数的可访问性，并创建实例。
	 *
	 * @param <T>         实例类型
	 * @param constructor 构造函数对象
	 * @param args        构造函数参数
	 * @return 新创建的实例
	 */
	public static <T> T newInstance(@NonNull Constructor<T> constructor, @NonNull Object... args) {
		makeAccessible(constructor);
		try {
			return constructor.newInstance(args == null ? new Object[0] : args);
		} catch (Exception ex) {
			handleReflectionException(ex);
		}
		throw new IllegalStateException("Should never get here");
	}

	/**
	 * 重新抛出异常。
	 * <p>
	 * 该方法会将异常转换为检查异常抛出。
	 *
	 * @param ex 异常对象
	 * @throws Exception 转换后的异常
	 */
	public static void rethrowException(Throwable ex) throws Exception {
		if (ex instanceof Exception) {
			throw (Exception) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * 重新抛出运行时异常。
	 * <p>
	 * 该方法会将异常转换为运行时异常或错误抛出。
	 *
	 * @param ex 异常对象
	 * @throws RuntimeException 转换后的运行时异常
	 */
	public static void rethrowRuntimeException(Throwable ex) {
		if (ex instanceof RuntimeException) {
			throw (RuntimeException) ex;
		}
		if (ex instanceof Error) {
			throw (Error) ex;
		}
		throw new UndeclaredThrowableException(ex);
	}

	/**
	 * 在类及其继承体系中搜索类成员。
	 * <p>
	 * 该方法会递归搜索当前类、父类和接口中的成员，使用提供的搜索函数进行匹配。
	 *
	 * @param <T>      成员类型
	 * @param clazz    要搜索的类
	 * @param searcher 搜索函数
	 * @return 类成员加载器
	 */
	public static <T> ClassMembersLoader<T> search(@NonNull Class<?> clazz,
			@NonNull ThrowingFunction<? super Class<?>, ? extends Streamable<T>, ? extends ReflectiveOperationException> searcher) {
		return new ClassMembersLoader<>(clazz, (c) -> {
			try {
				return searcher.apply(c);
			} catch (ReflectiveOperationException | SecurityException e) {
				return Streamable.empty();
			}
		});
	}

	/**
	 * 设置字段的值。
	 * <p>
	 * 该方法会自动设置字段的可访问性，并设置字段的值。 对于基本类型字段，会自动拆箱为对应的基本类型。
	 *
	 * @param field  字段对象
	 * @param target 目标对象
	 * @param value  要设置的值
	 */
	public static void set(Field field, Object target, Object value) {
		makeAccessible(field);
		try {
			field.set(target, value);
		} catch (IllegalAccessException ex) {
			handleReflectionException(ex);
			throw new IllegalStateException(
					"Unexpected reflection exception - " + ex.getClass().getName() + ": " + ex.getMessage());
		}
	}

	/**
	 * 反射创建类实例的工具方法，支持根据入参自动匹配最适配的构造器
	 * <p>
	 * 核心特性：
	 * 1. 自动推导入参类型（null 入参默认按 Object.class 处理）；
	 * 2. 筛选与入参个数匹配的构造器；
	 * 3. 通过 {@link ExecutableMatchComparator} 对构造器进行适配度排序，优先选择最匹配的构造器（如精确类型匹配优于父类类型匹配）；
	 * 4. 支持访问私有构造器（依赖 {@link #getDeclaredConstructors(Class)} 方法的访问权限处理）。
	 *
	 * @param <T>          目标实例类型泛型，与传入的 instanceClass 类型一致
	 * @param instanceClass 目标实例的 Class 对象（不可为 null，若为 null 会导致反射操作异常）
	 * @param args         构造器入参（可变参数，支持 null 值，无参时传入空数组或不传入）
	 * @return 通过匹配的构造器创建的目标类实例（非 null，创建失败会抛出异常）
	 * @throws IllegalArgumentException 当未找到匹配的构造器（入参个数/类型不匹配）时抛出
	 * @throws RuntimeException          包装反射创建实例过程中的异常（如 InstantiationException、IllegalAccessException、InvocationTargetException 等）
	 * @see ExecutableMatchComparator 构造器适配度比较器，定义了类型匹配的优先级规则
	 * @see #getDeclaredConstructors(Class) 获取类的所有声明构造器（含私有）的辅助方法
	 * @see Constructor#newInstance(Object...) 反射创建实例的底层方法
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(@NonNull Class<T> instanceClass, Object... args) {
	    // 推导入参对应的 Class 类型数组（null 入参按 Object.class 处理，避免空指针）
	    Class<?>[] parameterTypes = new Class<?>[args.length];
	    for (int i = 0; i < parameterTypes.length; i++) {
	        Object arg = args[i];
	        parameterTypes[i] = arg == null ? Object.class : arg.getClass();
	    }

	    // 筛选：1. 入参个数匹配 2. 按适配度排序 3. 取最优匹配的构造器
	    Constructor<?> constructor = getDeclaredConstructors(instanceClass)
	            .filter((e) -> e.getParameterCount() == args.length)
	            .sorted(new ExecutableMatchComparator<>(parameterTypes))
	            .findFirst()
	            .orElse(null);

	    // 未找到匹配构造器，抛出参数不匹配异常
	    if (constructor == null) {
	        throw new IllegalArgumentException(
	                String.format("类 [%s] 未找到与入参匹配的构造器，入参个数：%d，入参类型：%s",
	                        instanceClass.getName(),
	                        args.length,
	                        Stream.of(parameterTypes).map(Class::getName).collect(Collectors.toList()))
	        );
	    }

	    // 调用构造器创建实例（底层会处理访问权限、入参注入等逻辑）
	    return (T) newInstance(constructor, args);
	}
}