package io.basc.framework.util.transmittable;

import io.basc.framework.util.exchange.Registration;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class AnyInheriterRegistry extends InheriterRegistry<Object, Object> {
	private static final AnyInheriterRegistry GLOBAL = new AnyInheriterRegistry();

	public final Registration register(ThreadLocal threadLocal) {
		return register(threadLocal, false);
	}

	public final void unregister(ThreadLocal threadLocal) {
		unregister(threadLocal, false);
	}

	public final Registration register(ThreadLocal threadLocal, boolean nullable) {
		ThreadLocalInheriter inheriter = new ThreadLocalInheriter<>(threadLocal, nullable);
		return register(inheriter);
	}

	public final void unregister(ThreadLocal threadLocal, boolean nullable) {
		ThreadLocalInheriter inheriter = new ThreadLocalInheriter<>(threadLocal, nullable);
		deregister(inheriter);
	}

	public static AnyInheriterRegistry global() {
		return GLOBAL;
	}
}
