package io.basc.framework.util.transmittable;

import io.basc.framework.util.registry.Registration;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class AnyInheriterRegistry extends InheriterRegistry<Object, Object> {
	private static final AnyInheriterRegistry GLOBAL = new AnyInheriterRegistry();

	public final Registration register(ThreadLocal threadLocal) {
		return register(threadLocal, false);
	}

	public final Registration unregister(ThreadLocal threadLocal) {
		return unregister(threadLocal, false);
	}

	public final Registration register(ThreadLocal threadLocal, boolean nullable) {
		ThreadLocalInheriter inheriter = new ThreadLocalInheriter<>(threadLocal, nullable);
		return register(inheriter);
	}

	public final Registration unregister(ThreadLocal threadLocal, boolean nullable) {
		ThreadLocalInheriter inheriter = new ThreadLocalInheriter<>(threadLocal, nullable);
		return unregister(inheriter);
	}

	@Override
	public Registration register(Inheriter inheriter) {
		return super.register(inheriter);
	}

	@Override
	public Registration unregister(Inheriter inheriter) {
		return super.unregister(inheriter);
	}

	public static AnyInheriterRegistry global() {
		return GLOBAL;
	}
}
