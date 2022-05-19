package io.basc.framework.env;

import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.NotSupportedException;

/**
 * 1.使用反射重写equals hashCode toString方法
 * 
 * @author wcnnkh
 *
 */
public class BascObject extends Object implements Cloneable {

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

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new NotSupportedException(toString());
		}
	}
}
