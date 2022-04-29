package io.basc.framework.env;

import io.basc.framework.core.reflect.ReflectionUtils;

public class BascObject extends Object {

	@Override
	public boolean equals(Object obj) {
		return ReflectionUtils.equals(obj, this);
	}

	@Override
	public int hashCode() {
		return ReflectionUtils.hashCode(this);
	}

	@Override
	public String toString() {
		return ReflectionUtils.toString(this);
	}
}
