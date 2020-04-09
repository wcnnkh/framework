package scw.core.instance.support;

import scw.core.instance.InstanceException;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.utils.UnsafeUtils;
import scw.lang.UnsupportedException;

public class UnsafeNoArgsInstanceFactory implements NoArgsInstanceFactory {
	static {
		if (!UnsafeUtils.isSupport()) {
			throw new UnsupportedException("UnsafeNoArgsInstanceFactory");
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
		return (T) getInstance(ReflectionInstanceFactory.forName(name));
	}

	public boolean isInstance(String name) {
		Class<?> clazz = ReflectionInstanceFactory.forName(name);
		if (clazz == null) {
			return false;
		}

		return true;
	}

	public boolean isInstance(Class<?> clazz) {
		if(clazz == null){
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
