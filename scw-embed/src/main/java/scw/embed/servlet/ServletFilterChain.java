package scw.embed.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.core.utils.CollectionUtils;

public class ServletFilterChain extends AbstractServletFilterChain {
	private Iterator<Filter> iterator;

	public ServletFilterChain(Collection<Filter> filters,
			FilterChain filterChain) {
		super(filterChain);
		if (!CollectionUtils.isEmpty(filters)) {
			this.iterator = filters.iterator();
		}
	}

	@Override
	protected Filter getNextFilter(ServletRequest request,
			ServletResponse response) throws IOException, ServletException {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
