package scw.instance.support;

import scw.instance.InstanceException;
import scw.lang.NotSupportedException;
import scw.util.UnsafeUtils;

public class UnsafeNoArgsInstanceFactory extends AbstractNoArgsInstanceFactory {
	static {
		if (!UnsafeUtils.isSupport()) {
			throw new NotSupportedException("UnsafeNoArgsInstanceFactory");
		}
	}

	public <T> T getInstance(Class<T> type) {
		if (type == null) {
			return null;
		}
		
		if(!isPresent(type)){
			return null;
		}

		try {
			return type.cast(UnsafeUtils.allocateInstance(type));
		} catch (Exception e) {
			throw new InstanceException(type.getName(), e);
		}
	}

	public boolean isInstance(Class<?> clazz) {
		return isPresent(clazz);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		return (T) getInstance(getClass(name));
	}

	public boolean isInstance(String name) {
		return isInstance(getClass(name));
	}
}
