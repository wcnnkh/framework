package scw.core.resource;

import scw.core.Verification;
import scw.core.reflect.AnnotationUtils;

public class IgnoreClassVerification implements Verification<Class<?>> {
	private final boolean ignoreEmptyMethod;
	private final boolean interfaceClass;

	public IgnoreClassVerification(boolean ignoreEmptyMethod, boolean interfaceClass) {
		this.ignoreEmptyMethod = ignoreEmptyMethod;
		this.interfaceClass = interfaceClass;
	}

	public boolean verification(Class<?> data) {
		if (data == null) {
			return true;
		}

		if (AnnotationUtils.isIgnore(data)) {
			return true;
		}

		if (interfaceClass && !data.isInterface()) {
			return true;
		}

		if (ignoreEmptyMethod && data.getMethods().length == 0) {
			return true;
		}

		return false;
	}

}
