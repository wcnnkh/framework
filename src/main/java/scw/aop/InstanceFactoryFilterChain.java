package scw.aop;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import scw.core.instance.InstanceFactory;
import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public final class InstanceFactoryFilterChain extends AbstractFilterChain {
	private static Logger logger = LoggerFactory.getLogger(InstanceFactoryFilterChain.class);
	private Iterator<String> iterator;
	private final InstanceFactory instanceFactory;

	public InstanceFactoryFilterChain(InstanceFactory instanceFactory, Collection<String> filterNames,
			FilterChain chain) {
		super(chain);
		this.instanceFactory = instanceFactory;
		if (!CollectionUtils.isEmpty(filterNames)) {
			iterator = filterNames.iterator();
		}
	}

	@Override
	protected Filter getNextFilter(Invoker invoker, Object proxy, Class<?> targetClass, Method method, Object[] args)
			throws Throwable {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			String name = iterator.next();
			if (instanceFactory.isInstance(name)) {
				return instanceFactory.getInstance(name);
			} else {
				logger.warn("{}无法被实例化，已忽略使用此filter", name);
				return getNextFilter(invoker, proxy, targetClass, method, args);
			}
		}
		return null;
	}

}
