package scw.mvc.http.filter;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.mvc.Filter;
import scw.mvc.FilterChain;
import scw.mvc.MVCUtils;
import scw.mvc.MethodAction;
import scw.mvc.SimpleFilterChain;
import scw.mvc.annotation.Controller;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.servlet.FilterChainAction;

public final class HttpServiceFilter extends HttpFilter {
	private final Collection<Filter> filters;

	public HttpServiceFilter(BeanFactory beanFactory, PropertyFactory propertyFactory, Collection<Class<?>> classes,
			String actionKey) {
		filters = new LinkedList<Filter>();

		filters.add(new RpcServletFilter(beanFactory, propertyFactory));
		if (MVCUtils.isSupportCorssDomain(propertyFactory)) {
			filters.add(new CrossDomainFilter(new CrossDomainDefinition(propertyFactory)));
		}

		String sourceRoot = MVCUtils.getSourceRoot(propertyFactory);
		String[] sourcePath = MVCUtils.getSourcePath(propertyFactory);
		if (!StringUtils.isEmpty(sourceRoot) && !ArrayUtils.isEmpty(sourcePath)) {
			filters.add(new ResourceServiceFilter(sourceRoot, sourcePath));
		}

		filters.add(new ParameterActionServiceFilter(actionKey));
		filters.add(new PathServiceFilter());
		filters.add(new RestServiceFilter());

		for (Class<?> clz : classes) {
			Controller clzController = clz.getAnnotation(Controller.class);
			if (clzController == null) {
				continue;
			}

			for (Method method : clz.getDeclaredMethods()) {
				Controller methodController = method.getAnnotation(Controller.class);
				if (methodController == null) {
					continue;
				}

				for (Filter filter : filters) {
					if (filter instanceof AbstractHttpServiceFilter) {
						((AbstractHttpServiceFilter) filter).scanning(clz, method, clzController, methodController,
								new MethodAction(beanFactory, propertyFactory, clz, method));
					}
				}
			}
		}
	}

	@Override
	public Object doFilter(HttpChannel channel, HttpRequest httpRequest, HttpResponse httpResponse, FilterChain chain)
			throws Throwable {
		FilterChain filterChain = new SimpleFilterChain(filters, new FilterChainAction(chain));
		return filterChain.doFilter(channel);
	}

}
