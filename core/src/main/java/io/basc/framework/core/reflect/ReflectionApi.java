package io.basc.framework.core.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Supplier;

import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.ConcurrentReferenceHashMap;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.stream.Processor;

/**
 * 使用反射调用api，不安全的调用
 * 
 * @author wcnnkh
 *
 */
public class ReflectionApi implements Supplier<Object> {
	private final Class<?> declaringClass;
	private final Processor<Class<?>, Object, Throwable> processor;

	public ReflectionApi(@Nullable Class<?> declaringClass,
			@Nullable Processor<Class<?>, Object, Throwable> processor) {
		this.declaringClass = declaringClass;
		this.processor = processor;
	}

	public Class<?> getDeclaringClass() {
		return this.declaringClass;
	}

	/**
	 * 是否可用
	 * 
	 * @return
	 */
	public boolean isAvailable() {
		return this.declaringClass != null;
	}

	public Object get() {
		if (this.declaringClass == null) {
			throw new IllegalStateException("Unavailable API");
		}

		if (this.processor == null) {
			return null;
		}

		try {
			return processor.process(this.declaringClass);
		} catch (Throwable e) {
			throw new IllegalStateException(this.declaringClass.getName(), e);
		}
	}

	@Nullable
	public Method getMethod(String name, Class<?>... parameterTypes) {
		if (this.declaringClass == null) {
			return null;
		}
		return ReflectionUtils.findMethod(this.declaringClass, name, parameterTypes);
	}

	public Object invoke(Method method, Object... args) {
		Assert.requiredArgument(isAvailable(), "declaringClass");
		Assert.requiredArgument(method != null, "method");
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers()) ? null : get(), args);
	}

	/**
	 * 调用指定方法名
	 * 
	 * @param methodName
	 * @param params     parameterTypes和params各一半，如 {String.class, "a"}
	 * @return
	 */
	public Object invoke(String methodName, Object... params) {
		Assert.requiredArgument(isAvailable(), "declaringClass");
		Assert.requiredArgument(StringUtils.hasText(methodName), "methodName");
		Assert.requiredArgument(params != null && params.length % 2 == 0, "params");
		Class<?>[] parameterTypes = new Class<?>[params.length / 2];
		System.arraycopy(params, 0, parameterTypes, 0, parameterTypes.length);
		Method method = getMethod(methodName, parameterTypes);
		if (method == null) {
			throw new IllegalArgumentException(this.declaringClass + " not found method[" + methodName
					+ "] parameterTypes" + Arrays.toString(parameterTypes));
		}
		Object[] args = new Object[parameterTypes.length];
		System.arraycopy(params, parameterTypes.length, args, 0, args.length);
		return invoke(method, args);
	}

	private static final Processor<Class<?>, Object, Throwable> UNSAFE_PROCESSOR = (C) -> {
		Field f = C.getDeclaredField("theUnsafe");
		ReflectionUtils.makeAccessible(f);
		return f.get(null);
	};

	/**
	 * 不安全的api
	 * 
	 * @see sun/misc/Unsafe
	 */
	public static final ReflectionApi UNSAFE = new ReflectionApi(ClassUtils.getClass("sun.misc.Unsafe", null),
			UNSAFE_PROCESSOR);
	private static final Method ALLOCATE_INSTANCE_METHOD = UNSAFE.getMethod("allocateInstance", Class.class);

	/**
	 * 分配一个实例，无需调用对象的构造方法
	 * 
	 * @see ReflectionApi#UNSAFE
	 * @param <T>
	 * @param type
	 * @return
	 */
	public static <T> T allocateInstance(Class<T> type) {
		Assert.isTrue(UNSAFE.isAvailable());
		Assert.requiredArgument(type != null, "type");
		return type.cast(UNSAFE.invoke(ALLOCATE_INSTANCE_METHOD, type));
	}

	private static final Processor<Class<?>, Object, Throwable> REFLECTION_FACTORY_PROCESSOR = (c) -> {
		Method method = c.getMethod("getReflectionFactory");
		return method.invoke(null);
	};

	/**
	 * 不安全的api
	 * 
	 * @see sun/reflect/ReflectionFactory
	 */
	public static final ReflectionApi REFLECTION_FACTORY = new ReflectionApi(
			ClassUtils.getClass("sun.reflect.ReflectionFactory", null), REFLECTION_FACTORY_PROCESSOR);
	private static final Method NEW_CONSTRUCTOR_FOR_SERIALIZATION_METHOD = REFLECTION_FACTORY
			.getMethod("newConstructorForSerialization", Class.class, Constructor.class);

	/**
	 * 获取一个无参的构造方法,无论对象是否存在无参的构造方法
	 * 
	 * @see #REFLECTION_FACTORY
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> newConstructorForSerialization(Class<T> type) {
		Assert.isTrue(REFLECTION_FACTORY.isAvailable());
		Assert.requiredArgument(type != null, "type");
		return (Constructor<T>) REFLECTION_FACTORY.invoke(NEW_CONSTRUCTOR_FOR_SERIALIZATION_METHOD, type,
				ReflectionUtils.OBJECT_CONSTRUCTOR);
	}

	private static final ConcurrentReferenceHashMap<Class<?>, Constructor<?>> CONSTRUCTOR_MAP = new ConcurrentReferenceHashMap<Class<?>, Constructor<?>>(
			128);

	/**
	 * 获取一个无参的构造方法, 无论对象是否存在无参的构造方法
	 * 
	 * @see #newConstructorForSerialization(Class)
	 * @see Class#getDeclaredConstructor(Class...)
	 * @param <T>
	 * @param type
	 * @return {@link REFLECTION_FACTORY}不可用时返回空
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getConstructor(Class<T> type) {
		Assert.requiredArgument(type != null, "type");
		Constructor<T> constructor = ReflectionUtils.getConstructor(type);
		if (constructor == null) {
			constructor = (Constructor<T>) CONSTRUCTOR_MAP.get(type);
			if (constructor == null && REFLECTION_FACTORY.isAvailable()) {
				constructor = newConstructorForSerialization(type);
			}

			if (constructor == null) {
				return null;
			}

			Constructor<T> old = (Constructor<T>) CONSTRUCTOR_MAP.putIfAbsent(type, constructor);
			if (old == null) {
				// 插入成功,整理缓存
				CONSTRUCTOR_MAP.purgeUnreferencedEntries();
			} else {
				constructor = old;
			}
		}
		return constructor;
	}

	/**
	 * 是否可以实例化
	 * 
	 * @param type
	 * @return
	 */
	public static boolean isInstance(Class<?> type) {
		if (type == null || type.isPrimitive() || type.isArray() || type.isAnnotation() || type.isInterface()
				|| Modifier.isAbstract(type.getModifiers())) {
			return false;
		}

		return getConstructor(type) != null || UNSAFE.isAvailable();
	}

	/**
	 * 实例一个对象，无论对象是否存在无参的构造方法
	 * 
	 * @see #isInstance(Class)
	 * @see #REFLECTION_FACTORY
	 * @see ReflectionApi#UNSAFE
	 * @see #getConstructor(Class)
	 * @param <T>
	 * @param type
	 * @return
	 */
	public static <T> T newInstance(Class<T> type) {
		if (REFLECTION_FACTORY.isAvailable()) {
			try {
				return getConstructor(type).newInstance();
			} catch (Exception e) {
				// 如果出现异常，交给Unsafe处理
			}
		}

		if (UNSAFE.isAvailable()) {
			return allocateInstance(type);
		}

		throw new NotSupportedException(type.getName());
	}
}
