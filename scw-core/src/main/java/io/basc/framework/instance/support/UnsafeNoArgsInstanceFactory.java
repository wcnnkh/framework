package io.basc.framework.instance.support;

import io.basc.framework.instance.InstanceException;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.util.UnsafeUtils;
import io.basc.framework.util.XUtils;

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
		
		if(!XUtils.isAvailable(type)){
			return null;
		}

		try {
			return type.cast(UnsafeUtils.allocateInstance(type));
		} catch (Exception e) {
			throw new InstanceException(type.getName(), e);
		}
	}

	public boolean isInstance(Class<?> clazz) {
		return XUtils.isAvailable(clazz);
	}
}
