package run.soeasy.framework.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.function.Supplier;

import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.collection.ConcurrentReferenceHashMap;
import run.soeasy.framework.util.function.Function;

/**
 * 使用反射调用api，不安全的调用
 * 
 * @author wcnnkh
 *
 */
public class ReflectionApi implements Supplier<Object> {
	private final Class<?> declaringClass;
	private final Function<? super Class<?>, ? extends Object, ? extends Throwable> processor;

	public ReflectionApi( Class<?> declaringClass,
			 Function<Class<?>, Object, ? extends Throwable> processor) {
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
			return processor.apply(this.declaringClass);
		} catch (Throwable e) {
			throw new IllegalStateException(this.declaringClass.getName(), e);
		}
	}

	
	public Method getMethod(String name, Class<?>... parameterTypes) {
		if (this.declaringClass == null) {
			return null;
		}
		return ReflectionUtils.getDeclaredMethods(this.declaringClass).all().find(name, parameterTypes);
	}

	public Object invoke(Method method, Object... args) {
		Assert.requiredArgument(isAvailable(), "declaringClass");
		Assert.requiredArgument(method != null, "method");
		ReflectionUtils.makeAccessible(method);
		return ReflectionUtils.invoke(method, Modifier.isStatic(method.getModifiers()) ? null : get(), args);
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

	private static final Function<Class<?>, Object, Throwable> UNSAFE_PROCESSOR = (C) -> {
		Field f = C.getDeclaredField("theUnsafe");
		ReflectionUtils.makeAccessible(f);
		return f.get(null);
	};

	/**
	 * 不安全的api
	 * 
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

	private static final Function<Class<?>, Object, Throwable> REFLECTION_FACTORY_PROCESSOR = (c) -> {
		Method method = c.getMethod("getReflectionFactory");
		return method.invoke(null);
	};

	/**
	 * 不安全的api
	 * 
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
	 * 获取无参的构造方法(来自序列化的方式)
	 * 
	 * @see #REFLECTION_FACTORY
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Constructor<T> getConstructorForSerialization(Class<T> type) {
		Constructor<T> constructor = (Constructor<T>) CONSTRUCTOR_MAP.get(type);
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
		return constructor;
	}

	/**
	 * 获取一个无参的构造方法, 无论对象是否存在无参的构造方法
	 * 
	 * @see Class#getDeclaredConstructor(Class...)
	 * @see #getConstructorForSerialization(Class)
	 * @param <T>
	 * @param type
	 * @return {@link REFLECTION_FACTORY}不可用时返回空
	 */
	
	public static <T> Constructor<T> getConstructor(Class<T> type) {
		Assert.requiredArgument(type != null, "type");
		Constructor<T> constructor = ReflectionUtils.getDeclaredConstructor(type);
		if (constructor == null) {
			constructor = getConstructorForSerialization(type);
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
		return true;
	}

	public static <T> T newInstance(Class<T> type) throws UnsupportedOperationException {
		Constructor<T> constructor = getConstructor(type);
		if (constructor != null) {
			try {
				return ReflectionUtils.newInstance(constructor);
			} catch (Exception e) {
				// 忽略
			}
		}

		if (UNSAFE.isAvailable()) {
			try {
				return allocateInstance(type);
			} catch (Exception e) {
				// 忽略
			}
		}

		return ReflectionUtils.newInstanceWithNullValues(type);
	}
}
