package scw.core.instance.support;

import scw.core.exception.NotSupportException;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.UnsafeUtils;

public class UnsafeNoArgsInstanceFactory implements NoArgsInstanceFactory {
	static {
		if (!UnsafeUtils.isSupport()) {
			throw new NotSupportException("UnsafeNoArgsInstanceFactory");
		}
	}

	public <T> T getInstance(Class<T> type) {
		if (type == null) {
			return null;
		}

		try {
			return type.cast(UnsafeUtils.allocateInstance(type));
		} catch (Throwable e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		return (T) getInstance(ReflectionInstanceFactory.forName(name));
	}

	public boolean isInstance(String name) {
		Class<?> clazz = ReflectionInstanceFactory.forName(name);
		if (clazz == null) {
			return false;
		}

		return isInstance(clazz);
	}

	public boolean isInstance(Class<?> clazz) {
		if(clazz == null){
			return false;
		}
		
		return ReflectUtils.isInstance(clazz, false);
	}

}
