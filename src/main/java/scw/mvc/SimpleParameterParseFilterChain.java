package scw.mvc;

import java.util.Collection;
import java.util.Iterator;

import scw.core.reflect.ParameterConfig;
import scw.core.utils.CollectionUtils;

public class SimpleParameterParseFilterChain implements ParameterFilterChain {
	private Iterator<ParameterFilter> iterator;

	public SimpleParameterParseFilterChain(Collection<ParameterFilter> collection) {
		if (!CollectionUtils.isEmpty(collection)) {
			iterator = collection.iterator();
		}
	}

	public Object doFilter(Channel channel, ParameterConfig parameterConfig) throws Throwable {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next().filter(channel, parameterConfig, this);
		}

		return null;
	}

}
