package run.soeasy.framework.lang;

import run.soeasy.framework.util.Assert;

/**
 * {@link ThreadLocal} subclass that exposes a specified name as
 * {@link #toString()} result (allowing for introspection).
 *
 * @see NamedInheritableThreadLocal
 */
public class NamedThreadLocal<T> extends ThreadLocal<T> {

	private final String name;

	/**
	 * Create a new NamedThreadLocal with the given name.
	 * 
	 * @param name a descriptive name for this ThreadLocal
	 */
	public NamedThreadLocal(String name) {
		Assert.hasText(name, "Name must not be empty");
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return this.name;
	}
}
