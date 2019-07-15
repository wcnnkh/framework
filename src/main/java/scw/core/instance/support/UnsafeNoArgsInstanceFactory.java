package scw.core.instance.support;

import scw.core.instance.NoArgsInstanceFactory;
import scw.core.utils.UnsafeUtils;

public class UnsafeNoArgsInstanceFactory implements NoArgsInstanceFactory {

	@SuppressWarnings("restriction")
	public <T> T getInstance(Class<T> type) {
		if (type == null) {
			return null;
		}

		try {
			return type.cast(UnsafeUtils.getUnsafe().allocateInstance(type));
		} catch (InstantiationException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		return (T) getInstance(ReflectionInstanceFactory.forName(name));
	}

}
