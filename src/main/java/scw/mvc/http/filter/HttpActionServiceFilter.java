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
import scw.mvc.SimpleFilterChain;
import scw.mvc.action.ActionFilter;
import scw.mvc.action.HttpAction;
import scw.mvc.action.HttpActionService;
import scw.mvc.action.HttpControllerConfig;
import scw.mvc.action.HttpParameterActionService;
import scw.mvc.action.HttpPathService;
import scw.mvc.action.HttpRestfulService;
import scw.mvc.action.SimpleHttpAction;
import scw.mvc.annotation.Controller;
import scw.mvc.http.HttpChannel;
import scw.mvc.http.HttpFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.servlet.FilterChainAction;
import scw.security.authority.http.HttpAuthorityManager;
import scw.security.authority.http.SimpleHttpAuthorityManager;

public final class HttpActionServiceFilter extends HttpFilter {
	private final Collection<Filter> filters = new LinkedList<Filter>();
	private final Collection<ActionFilter> actionFilters = new LinkedList<ActionFilter>();

	public HttpActionServiceFilter(BeanFactory beanFactory, PropertyFactory propertyFactory,
			Collection<Class<?>> classes) {
		SimpleHttpAuthorityManager simpleHttpAuthorityManager = null;
		if (beanFactory.isInstance(HttpAuthorityManager.class)
				&& beanFactory.isSingleton(HttpAuthorityManager.class.getName())) {
			HttpAuthorityManager httpAuthorityManager = beanFactory.getInstance(HttpAuthorityManager.class);
			if (httpAuthorityManager instanceof SimpleHttpAuthorityManager) {
				simpleHttpAuthorityManager = (SimpleHttpAuthorityManager) httpAuthorityManager;
			}
		}

		actionFilters.addAll(MVCUtils.getActionFilters(beanFactory, propertyFactory));

		filters.add(new RpcServletFilter(beanFactory, propertyFactory));
		if (MVCUtils.isSupportCorssDomain(propertyFactory)) {
			filters.add(new CrossDomainFilter(new CrossDomainDefinition(propertyFactory)));
		}

		String sourceRoot = MVCUtils.getSourceRoot(propertyFactory);
		String[] sourcePath = MVCUtils.getSourcePath(propertyFactory);
		if (!StringUtils.isEmpty(sourceRoot) && !ArrayUtils.isEmpty(sourcePath)) {
			filters.add(new ResourceServiceFilter(sourceRoot, sourcePath));
		}

		if (MVCUtils.isSupportHttpParameterAction(propertyFactory)) {
			filters.add(
					new HttpParameterActionService(actionFilters, MVCUtils.getHttpParameterActionKey(propertyFactory)));
		}

		filters.add(new HttpPathService(actionFilters));
		filters.add(new HttpRestfulService(actionFilters));

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
					if (filter instanceof HttpActionService) {
						HttpAction httpAction = new SimpleHttpAction(beanFactory, propertyFactory, clz, method);
						if (simpleHttpAuthorityManager != null && httpAction.getAuthority() != null) {
							simpleHttpAuthorityManager.addAuthroity(httpAction.getAuthority());
						}

						for (HttpControllerConfig config : httpAction.getControllerConfigs()) {
							((HttpActionService) filter).scanning(httpAction, config);
						}
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
