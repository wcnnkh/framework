package scw.servlet.http.filter;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.servlet.Action;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.IteratorFilterChain;
import scw.servlet.MethodAction;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.annotation.Controller;

public final class HttpServiceFilter implements Filter {
	private final Collection<Filter> filters;

	public HttpServiceFilter(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Collection<Class<?>> classes,
			String actionKey) {
		filters = new LinkedList<Filter>();

		if (StringUtils.parseBoolean(propertyFactory
				.getProperty("http.cross-domain"))) {
			filters.add(new CrossDomainFilter(new CrossDomainDefinition(
					propertyFactory)));
		}

		filters.add(beanFactory.getInstance(HeaderSpreadFilter.class));
		filters.add(new ParameterActionServiceFilter(actionKey));
		filters.add(new ServletPathServiceFilter());
		filters.add(new RestServiceFilter());

		for (Class<?> clz : classes) {
			Controller clzController = clz.getAnnotation(Controller.class);
			if (clzController == null) {
				continue;
			}

			for (Method method : clz.getDeclaredMethods()) {
				Controller methodController = method
						.getAnnotation(Controller.class);
				if (methodController == null) {
					continue;
				}

				for (Filter filter : filters) {
					if (filter instanceof AbstractHttpServiceFilter) {
						((AbstractHttpServiceFilter) filter).scanning(clz,
								method, clzController, methodController,
								new MethodAction(beanFactory, propertyFactory,
										clz, method));
					}
				}
			}
		}
	}

	public void doFilter(Request request, Response response,
			final FilterChain filterChain) throws Throwable {
		FilterChain chain = new IteratorFilterChain(filters, new Action() {

			public void doAction(Request request, Response response)
					throws Throwable {
				filterChain.doFilter(request, response);
			}
		});
		chain.doFilter(request, response);
	}
}
