package scw.aop;

import java.util.Collection;
import java.util.Iterator;

import scw.core.instance.NoArgsInstanceFactory;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

final class InstanceFactoryIteratorFilterChain extends
		AbstractIteratorFilterChain {
	private static Logger logger = LoggerUtils
			.getLogger(InstanceFactoryIteratorFilterChain.class);
	private Iterator<String> iterator;
	private final NoArgsInstanceFactory instanceFactory;

	public InstanceFactoryIteratorFilterChain(
			NoArgsInstanceFactory instanceFactory,
			Collection<String> filterNames, FilterChain chain) {
		super(chain);
		this.instanceFactory = instanceFactory;
		iterator = filterNames.iterator();
	}

	@Override
	protected Filter getNextFilter(ProxyInvoker invoker, Object[] args)
			throws Throwable {
		if (iterator.hasNext()) {
			String name = iterator.next();
			if (instanceFactory.isInstance(name)) {
				return instanceFactory.getInstance(name);
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("{}无法被实例化，已忽略使用此filter", name);
				}
				return getNextFilter(invoker, args);
			}
		}
		return null;
	}

}
