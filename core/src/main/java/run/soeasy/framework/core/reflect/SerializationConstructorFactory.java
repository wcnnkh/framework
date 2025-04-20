package run.soeasy.framework.core.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Provider;

/**
 * 使用系列化行为的构造方法，可以绕过必需使用参数构造对象的行为
 * 
 * @author soeasy.run
 *
 */
public class SerializationConstructorFactory implements ReflectionFactory<Constructor<?>> {
	private static Constructor<Object> objectConstructor;
	static {
		try {
			objectConstructor = Object.class.getConstructor();
		} catch (NoSuchMethodException e) {
			// 不可能出现object没有默认的构造方法
		}
	}

	private volatile Class<?> reflectionFactoryDeclaringClass;

	public Class<?> getReflectionFactoryDeclaringClass() {
		if (reflectionFactoryDeclaringClass == null) {
			synchronized (this) {
				if (reflectionFactoryDeclaringClass == null) {
					try {
						this.reflectionFactoryDeclaringClass = Class.forName("sun.reflect.ReflectionFactory");
					} catch (ClassNotFoundException e) {
					}
				}
			}
		}
		return reflectionFactoryDeclaringClass;
	}

	private volatile Method newConstructorForSerialization;

	public Method getNewConstructorForSerialization() {
		if (newConstructorForSerialization == null && getReflectionFactoryDeclaringClass() != null) {
			synchronized (this) {
				if (newConstructorForSerialization == null && getReflectionFactoryDeclaringClass() != null) {
					try {
						this.newConstructorForSerialization = getReflectionFactoryDeclaringClass()
								.getMethod("newConstructorForSerialization", Class.class, Constructor.class);
					} catch (NoSuchMethodException | SecurityException e) {
					}
				}
			}
		}
		return newConstructorForSerialization;
	}

	private volatile Object reflectionFactory;

	public Object getReflectionFactory() {
		if (reflectionFactory == null && getReflectionFactoryDeclaringClass() != null) {
			synchronized (this) {
				if (reflectionFactory == null || getReflectionFactoryDeclaringClass() != null) {
					Method getReflectionFactory;
					try {
						getReflectionFactory = getReflectionFactoryDeclaringClass().getMethod("getReflectionFactory");
					} catch (NoSuchMethodException | SecurityException e) {
						return null;
					}
					reflectionFactory = ReflectionUtils.invoke(getReflectionFactory, null);
				}
			}
		}
		return reflectionFactory;
	}

	@Override
	public Provider<Constructor<?>> getReflectionProvider(@NonNull Class<?> declaringClass) {
		if (getReflectionFactory() != null && getNewConstructorForSerialization() != null) {
			Constructor<?> constructor = (Constructor<?>) ReflectionUtils.invoke(getNewConstructorForSerialization(),
					getReflectionFactory(), declaringClass, objectConstructor);
			if (constructor != null) {
				return Provider.forSupplier(() -> constructor);
			}
		}
		return Provider.empty();
	}
}
