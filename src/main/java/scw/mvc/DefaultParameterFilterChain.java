package scw.mvc;

import java.util.Collection;
import java.util.Iterator;

import scw.core.parameter.ParameterConfig;
import scw.core.utils.CollectionUtils;

public class DefaultParameterFilterChain implements ParameterFilterChain {
	private Iterator<ParameterFilter> iterator;
	private ParameterFilterChain chain;

	public DefaultParameterFilterChain(Collection<ParameterFilter> filters, ParameterFilterChain chain) {
		if (!CollectionUtils.isEmpty(filters)) {
			iterator = filters.iterator();
		}
		this.chain = chain;
	}

	public Object doFilter(Channel channel, ParameterConfig parameterConfig) throws Throwable {
		if (iterator == null) {
			return lastFilter(channel, parameterConfig);
		}

		if (iterator.hasNext()) {
			return iterator.next().doFilter(channel, parameterConfig, this);
		}

		return lastFilter(channel, parameterConfig);
	}

	private Object lastFilter(Channel channel, ParameterConfig parameterConfig) throws Throwable {
		return chain == null ? channel.getParameter(parameterConfig) : chain.doFilter(channel, parameterConfig);
	}
}
