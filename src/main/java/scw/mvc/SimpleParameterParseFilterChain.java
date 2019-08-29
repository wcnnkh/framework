package scw.mvc;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;

public class SimpleParameterParseFilterChain implements ParameterFilterChain {
	private Iterator<ParameterFilter> iterator;

	public SimpleParameterParseFilterChain(Collection<ParameterFilter> collection) {
		if (!CollectionUtils.isEmpty(collection)) {
			iterator = collection.iterator();
		}
	}

	public Object doFilter(Channel channel, ParameterDefinition parameterDefinition) throws Throwable {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next().filter(channel, parameterDefinition, this);
		}

		return null;
	}

}
