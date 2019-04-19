package scw.servlet;

import javax.servlet.ServletConfig;

import scw.aop.jdk.ConstructorInvoker;
import scw.application.CommonApplication;
import scw.common.utils.StringUtils;

public final class ServletUtils {
	private static final String ASYNCCONTEXT_NAME = "javax.servlet.AsyncContext";
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

	public static CommonApplication createCommonApplication(ServletConfig config) {
		ServletConfigPropertiesFactory propertiesFactory = new ServletConfigPropertiesFactory(config);
		String initStaticStr = propertiesFactory.getServletConfig("init-static");
		if (StringUtils.isNull(initStaticStr)) {
			return new CommonApplication(propertiesFactory.getConfigXml(), false, propertiesFactory);
		} else {
			return new CommonApplication(propertiesFactory.getConfigXml(), Boolean.parseBoolean(initStaticStr),
					propertiesFactory);
		}
	}

	public static Service createService(CommonApplication commonApplication) {
		try {
			if (isAsyncSupport()) {
				ConstructorInvoker invoker = new ConstructorInvoker("scw.servlet.AsyncService",
						CommonApplication.class);
				return (Service) invoker.invoke(commonApplication);
			} else {
				return new DefaultService(commonApplication);
			}
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
