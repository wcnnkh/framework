package scw.core.instance;

import scw.core.instance.annotation.Configuration;
import scw.core.utils.ClassUtils;
import scw.core.utils.UnsafeUtils;
import scw.lang.NotSupportedException;

@Configuration(order = Integer.MIN_VALUE)
public class UnsafeNoArgsInstanceFactory implements NoArgsInstanceFactory {
	static {
		if (!UnsafeUtils.isSupport()) {
			throw new NotSupportedException("UnsafeNoArgsInstanceFactory");
		}
	}

	public <T> T getInstance(Class<? extends T> type) {
		if (type == null) {
			return null;
		}

		try {
			return type.cast(UnsafeUtils.allocateInstance(type));
		} catch (Exception e) {
			throw new InstanceException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		return (T) getInstance(ClassUtils.forNameNullable(name));
	}

	public boolean isInstance(String name) {
		return ClassUtils.forNameNullable(name) != null;
	}

	public boolean isInstance(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}

		return true;
	}

	public boolean isSingleton(String name) {
		return false;
	}

	public boolean isSingleton(Class<?> clazz) {
		return false;
	}

}
