package run.soeasy.framework.lang;

import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.transmittable.AnyInheriterRegistry;

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
		this(name, false);
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
