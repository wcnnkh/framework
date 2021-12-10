package io.basc.framework.lang;

import io.basc.framework.util.Assert;

/**
 * {@link InheritableThreadLocal} subclass that exposes a specified name as
 * {@link #toString()} result (allowing for introspection).
 *
 * @see NamedThreadLocal
 */
public class NamedInheritableThreadLocal<T> extends InheritableThreadLocal<T> {

	private final String name;

	/**
	 * Create a new NamedInheritableThreadLocal with the given name.
	 * 
	 * @param name a descriptive name for this ThreadLocal
	 */
	public NamedInheritableThreadLocal(String name) {
		Assert.hasText(name, "Name must not be empty");
		this.name = name;
	}

	@Override
	public String toString() {
		return this.name;
	}

}
