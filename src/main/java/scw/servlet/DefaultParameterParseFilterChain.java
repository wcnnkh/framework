package scw.servlet;

import java.util.Collection;
import java.util.Iterator;

import scw.core.utils.CollectionUtils;
import scw.mvc.ParameterDefinition;

public class DefaultParameterParseFilterChain implements ParameterFilterChain {
	private Iterator<ParameterFilter> iterator;

	public DefaultParameterParseFilterChain(Collection<ParameterFilter> collection) {
		if (!CollectionUtils.isEmpty(collection)) {
			iterator = collection.iterator();
		}
	}

	public Object doFilter(Request request, ParameterDefinition parameterDefinition) throws Exception {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next().filter(request, parameterDefinition, this);
		}

		return null;
	}

}
