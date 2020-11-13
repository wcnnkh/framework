package scw.servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.application.Application;
import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanLifeCycleEvent.Step;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.event.EventListener;
import scw.http.HttpCookie;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.servlet.beans.ServletContextAware;
import scw.util.XUtils;

public final class ServletUtils {
	private static final boolean asyncSupport = ClassUtils.isPresent("javax.servlet.AsyncContext");// 是否支持异步处理
	public static final String ATTRIBUTE_FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
	private static final ServletOperations SERVLET_OPERATIONS = InstanceUtils.loadService(ServletOperations.class,
			"scw.servlet.DefaultServletOperations");

	private ServletUtils() {
	};

	/**
	 * 是否支持异步处理(实际是否支持还要判断request)
	 * 
	 * @return
	 */
	public static boolean isAsyncSupport() {
		return asyncSupport;
	}

	public static void jsp(ServletRequest request, ServletResponse response, String page)
			throws ServletException, IOException {
		RequestDispatcher dispatcher = request.getRequestDispatcher(page);
		dispatcher.forward(request, response);
	}

	public static String getForwardRequestUri(ServletRequest servletRequest) {
		return (String) servletRequest.getAttribute(ATTRIBUTE_FORWARD_REQUEST_URI);
	}

	public static HttpCookie wrapper(Cookie cookie) {
		return new HttpCookie(cookie.getName(), cookie.getValue()).setDomain(cookie.getDomain())
				.setMaxAge(cookie.getMaxAge()).setSecure(cookie.getSecure()).setPath(cookie.getPath());
	}

	public static HttpServletRequest getHttpServletRequest(ServerHttpRequest request) {
		return XUtils.getTarget(request, HttpServletRequest.class);
	}

	public static HttpServletResponse getHttpServletResponse(ServerHttpResponse response) {
		return XUtils.getTarget(response, HttpServletResponse.class);
	}

	public static synchronized void servletContextInitialization(final ServletContext servletContext,
			Application application) throws ServletException {
		String name = ServletContext.class.getName() + "-init";
		if (servletContext.getAttribute(name) != null) {
			return;
		}

		servletContext.setAttribute(name, true);

		application.getBeanFactory().getBeanLifeCycleEventDispatcher()
				.registerListener(new EventListener<BeanLifeCycleEvent>() {

					public void onEvent(BeanLifeCycleEvent event) {
						if (event.getStep() == Step.BEFORE_INIT) {
							Object source = event.getSource();
							if (source != null && source instanceof ServletContextAware) {
								((ServletContextAware) source).setServletContext(servletContext);
							}
						}
					}
				});

		SERVLET_OPERATIONS.servletContainerInitializer(servletContext, application);
	}
}
