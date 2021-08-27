package io.basc.framework.core;

import io.basc.framework.core.utils.ObjectUtils;

import java.lang.reflect.Method;

/**
 * A common key class for a method against a specific target class,
 * including {@link #toString()} representation and {@link Comparable}
 * support (as suggested for custom {@code HashMap} keys as of Java 8).
 *
 */
public final class MethodClassKey implements Comparable<MethodClassKey> {

	private final Method method;

	private final Class<?> targetClass;


	/**
	 * Create a key object for the given method and target class.
	 * @param method the method to wrap (must not be {@code null})
	 * @param targetClass the target class that the method will be invoked
	 * on (may be {@code null} if identical to the declaring class)
	 */
	public MethodClassKey(Method method, Class<?> targetClass) {
		this.method = method;
		this.targetClass = targetClass;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MethodClassKey)) {
			return false;
		}
		MethodClassKey otherKey = (MethodClassKey) other;
		return (this.method.equals(otherKey.method) &&
				ObjectUtils.nullSafeEquals(this.targetClass, otherKey.targetClass));
	}

	@Override
	public int hashCode() {
		return this.method.hashCode() + (this.targetClass != null ? this.targetClass.hashCode() * 29 : 0);
	}

	@Override
	public String toString() {
		return this.method + (this.targetClass != null ? " on " + this.targetClass : "");
	}

	public int compareTo(MethodClassKey other) {
		int result = this.method.getName().compareTo(other.method.getName());
		if (result == 0) {
			result = this.method.toString().compareTo(other.method.toString());
			if (result == 0 && this.targetClass != null && other.targetClass != null) {
				result = this.targetClass.getName().compareTo(other.targetClass.getName());
			}
		}
		return result;
	}

}
