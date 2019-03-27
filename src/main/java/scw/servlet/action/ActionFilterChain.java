package scw.servlet.action;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import scw.beans.BeanFactory;
import scw.common.utils.CollectionUtils;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;

public final class ActionFilterChain implements FilterChain {
	private Iterator<String> iterator;
	private final Method method;
	private final Class<?> clz;
	private final BeanFactory beanFactory;
	private final MethodParameter[] paramInfos;
	private HashSet<Filter> cacheMap;

	public ActionFilterChain(BeanFactory beanFactory, Class<?> clz, Method method, MethodParameter[] paramInfos,
			List<String> filterList) {
		this.method = method;
		this.clz = clz;
		this.paramInfos = paramInfos;
		this.beanFactory = beanFactory;
		if (!CollectionUtils.isEmpty(filterList)) {
			iterator = filterList.iterator();
			cacheMap = new HashSet<Filter>(filterList.size(), 1);
		}
	}

	public void doFilter(Request request, Response response) throws Throwable {
		if (iterator == null) {
			invoke(request, response);
			return;
		}

		if (iterator.hasNext()) {
			Filter filter = beanFactory.get(iterator.next());
			if (cacheMap.add(filter)) {
				filter.doFilter(request, response, this);
			} else {
				doFilter(request, response);
			}
		} else {
			invoke(request, response);
		}
	}

	private void invoke(Request request, Response response) throws Throwable {
		Object[] args = new Object[paramInfos.length];
		for (int i = 0; i < paramInfos.length; i++) {
			args[i] = paramInfos[i].getParameter(request, response);
		}

		Object rtn;
		if (Modifier.isStatic(method.getModifiers())) {
			rtn = method.invoke(null, args);
		} else {
			rtn = method.invoke(beanFactory.get(clz), args);
		}
		response.write(rtn);
	}
}
