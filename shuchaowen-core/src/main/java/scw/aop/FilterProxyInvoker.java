package scw.aop;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

import scw.aop.ProxyInvoker.ProxyInvokerWrapper;
import scw.core.instance.NoArgsInstanceFactory;

public final class FilterProxyInvoker extends ProxyInvokerWrapper {
	private Enumeration<Filter> enumeration;

	public FilterProxyInvoker(ProxyInvoker invoker, Filter[] filters) {
		this(invoker, filters == null ? null : Arrays.asList(filters));
	}

	public FilterProxyInvoker(ProxyInvoker invoker, Collection<Filter> filters) {
		this(invoker, filters == null ? null : Collections.enumeration(filters));
	}

	public FilterProxyInvoker(ProxyInvoker invoker,
			NoArgsInstanceFactory instanceFactory,
			Collection<String> filterNames) {
		this(invoker, new FilterNamesEnumeration(instanceFactory, filterNames));
	}

	public FilterProxyInvoker(ProxyInvoker invoker,
			Enumeration<Filter> enumeration) {
		super(invoker);
		this.enumeration = enumeration;
	}

	@Override
	public Object invoke(Object... args) throws Throwable {
		if (enumeration == null) {
			return super.invoke(args);
		}

		if (enumeration.hasMoreElements()) {
			return enumeration.nextElement().doFilter(this, args);
		}
		return super.invoke(args);
	}

	public static final class FilterNamesEnumeration implements
			Enumeration<Filter> {
		private final NoArgsInstanceFactory instanceFactory;
		private final Iterator<String> iterator;
		private String name;
		private boolean next = false;

		public FilterNamesEnumeration(NoArgsInstanceFactory instanceFactory,
				Collection<String> filterNames) {
			this.instanceFactory = instanceFactory;
			this.iterator = filterNames == null ? null : filterNames.iterator();
		}

		public boolean hasMoreElements() {
			if (next) {
				return true;
			}

			if (iterator == null || !iterator.hasNext()) {
				return false;
			}

			name = iterator.next();
			if (instanceFactory.isInstance(name)) {
				next = true;
				return true;
			}

			return false;
		}

		public Filter nextElement() {
			if (!next) {
				throw new NoSuchElementException();
			}

			next = false;
			return instanceFactory.getInstance(name);
		}
	}
}
