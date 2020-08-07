package scw.core.instance;

import scw.lang.NotSupportedException;
import scw.util.UnsafeUtils;

public class UnsafeNoArgsInstanceFactory extends AbstractNoArgsInstanceFactory {
	static {
		if (!UnsafeUtils.isSupport()) {
			throw new NotSupportedException("UnsafeNoArgsInstanceFactory");
		}
	}

	public <T> T getInstance(Class<? extends T> type) {
		if (type == null) {
			return null;
		}
		
		if(!accept(type)){
			return null;
		}

		try {
			return type.cast(UnsafeUtils.allocateInstance(type));
		} catch (Exception e) {
			throw new InstanceException(e);
		}
	}

	public boolean isInstance(Class<?> clazz) {
		if (clazz == null) {
			return false;
		}

		return accept(clazz);
	}
}
