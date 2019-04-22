package scw.servlet;

import scw.beans.BeanFactory;
import scw.beans.property.PropertiesFactory;

public final class ServletUtils {
	private static final String ASYNCCONTEXT_NAME = "javax.servlet.AsyncContext";
	private static final String SERVLET_SERVICE_BEAN_NAME = "scw.servlet.DefaultServletService";
	private static final String ASYNC_SERVLET_SERVICE_BEAN_ANEM = "scw.servlet.AsyncServletService";
	private static boolean asyncSupport = true;// 是否支持异步处理

	static {
		try {
			Class.forName(ASYNCCONTEXT_NAME);
		} catch (Throwable e) {
			asyncSupport = false;// 不支持
		}
	}

	private ServletUtils() {
	};

	/**
	 * 是否支持异步处理
	 * 
	 * @return
	 */
	public static boolean isAsyncSupport() {
		return asyncSupport;
	}

	public static ServletService getServletService(BeanFactory beanFactory, PropertiesFactory propertiesFactory,
			String configPath, String[] rootBeanFilters) {
		if (isAsyncSupport()) {
			return beanFactory.get(ASYNC_SERVLET_SERVICE_BEAN_ANEM, beanFactory, propertiesFactory, configPath,
					rootBeanFilters);
		} else {
			return beanFactory.get(SERVLET_SERVICE_BEAN_NAME, beanFactory, propertiesFactory, configPath,
					rootBeanFilters);
		}
	}
}
