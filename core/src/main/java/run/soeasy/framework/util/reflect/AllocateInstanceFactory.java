package run.soeasy.framework.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import lombok.NonNull;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.collection.Provider;
import run.soeasy.framework.util.spi.ProviderFactory;

public class AllocateInstanceFactory implements ProviderFactory {
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

	@SuppressWarnings("unchecked")
	@Override
	public <S> Provider<S> getProvider(@NonNull Class<S> type) {
		if (type == null || type.isPrimitive() || type.isArray() || type.isAnnotation() || type.isInterface()
				|| Modifier.isAbstract(type.getModifiers()) || getAllocateInstanceMethod() == null
				|| getUnsafe() == null) {
			return Provider.empty();
		}

		return Provider.forSupplier(() -> (S) ReflectionUtils.invoke(getAllocateInstanceMethod(), getUnsafe(), type));
	}

}
