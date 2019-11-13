package scw.mvc;

import java.util.Collection;
import java.util.Iterator;

import scw.core.parameter.ParameterConfig;
import scw.core.utils.CollectionUtils;

public class DefaultParameterFilterChain implements ParameterFilterChain {
	private Iterator<ParameterFilter> iterator;

	public DefaultParameterFilterChain(Collection<ParameterFilter> collection) {
		if (!CollectionUtils.isEmpty(collection)) {
			iterator = collection.iterator();
		}
	}

	public Object doFilter(Channel channel, ParameterConfig parameterConfig) throws Throwable {
		if (iterator == null) {
			return lastFilter(channel, parameterConfig);
		}

		if (iterator.hasNext()) {
			return iterator.next().filter(channel, parameterConfig, this);
		}

		return lastFilter(channel, parameterConfig);
	}
	
	protected Object lastFilter(Channel channel, ParameterConfig parameterConfig) throws Throwable{
		return null;
	}
}
