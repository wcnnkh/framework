package run.soeasy.framework.core.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import lombok.NonNull;

public class AllocateInstanceFactory implements InstanceFactory {
	private volatile Class<?> unsafeClass;

	public Class<?> getUnsafeClass() {
		if (unsafeClass == null) {
			synchronized (this) {
				if (unsafeClass == null) {
					unsafeClass = ClassUtils.getClass("sun.misc.Unsafe", null);
				}
			}
		}
		return unsafeClass;
	}

	private volatile Object unsafe;

	public Object getUnsafe() {
		if (unsafe == null && getUnsafeClass() != null) {
			synchronized (this) {
				if (unsafe == null && getUnsafeClass() != null) {
					try {
						Field field = getUnsafeClass().getDeclaredField("theUnsafe");
						this.unsafe = ReflectionUtils.get(field, null);
					} catch (NoSuchFieldException | SecurityException e) {
					}
				}
			}
		}
		return unsafe;
	}

	private volatile Method allocateInstanceMethod;

	public Method getAllocateInstanceMethod() {
		if (allocateInstanceMethod == null && getUnsafeClass() != null) {
			synchronized (this) {
				if (allocateInstanceMethod == null && getUnsafeClass() != null) {
					try {
						this.allocateInstanceMethod = getUnsafeClass().getMethod("allocateInstance", Class.class);
					} catch (NoSuchMethodException | SecurityException e) {
					}
				}
			}
		}
		return allocateInstanceMethod;
	}

	@Override
	public boolean canInstantiated(@NonNull ResolvableType requiredType) {
		Class<?> type = requiredType.getRawType();
		if (type == null || type.isPrimitive() || type.isArray() || type.isAnnotation() || type.isInterface()
				|| Modifier.isAbstract(type.getModifiers()) || getAllocateInstanceMethod() == null
				|| getUnsafe() == null) {
			return false;
		}
		return true;
	}

	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		return ReflectionUtils.invoke(getAllocateInstanceMethod(), getUnsafe(), requiredType.getType());
	}

}
