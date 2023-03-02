package io.basc.framework.lang;

import io.basc.framework.util.AnyInheriterRegistry;
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
	 * 
	 */
	public NamedInheritableThreadLocal(String name) {
		this(name, true);
	}

	/**
	 * Create a new NamedInheritableThreadLocal with the given name.
	 * 
	 * @param name     a descriptive name for this ThreadLocal
	 * @param register 是否注册到全局
	 * @see AnyInheriterRegistry#global()
	 */
	public NamedInheritableThreadLocal(String name, boolean register) {
		Assert.hasText(name, "Name must not be empty");
		this.name = name;
		if (register) {
			AnyInheriterRegistry.global().register(this);
		}
	}

	@Override
	public String toString() {
		return this.name;
	}

}
