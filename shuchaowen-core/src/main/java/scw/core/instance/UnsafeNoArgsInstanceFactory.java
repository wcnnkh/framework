package scw.core.instance;

import scw.core.instance.annotation.Configuration;
import scw.core.utils.UnsafeUtils;
import scw.lang.NotSupportedException;

@Configuration(order = Integer.MIN_VALUE)
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

		return true;
	}
}
