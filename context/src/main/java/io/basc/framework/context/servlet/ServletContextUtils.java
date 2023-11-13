package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.boot.Application;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.env.Sys;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;

public abstract class ServletContextUtils {
	private static final Class<?> SERVLET3_APPLICATION_STARTUP_CLASS = ClassUtils
			.findClass(ServletApplicationStartup.class.getPackage().getName() + ".support.Servlet3ApplicationStartup",
					null)
			.filter((e) -> ReflectionUtils.isAvailable(e)).orElse(null);
	private static final Class<?> SERVLET_APPLICATION_STARTUP_CLASS = ClassUtils
			.findClass(ServletApplicationStartup.class.getPackage().getName()
					+ ".support.DefaultServletApplicationStartup", null)
			.filter((e) -> ReflectionUtils.isAvailable(e)).orElse(null);

	private static final ServletApplicationStartup SERVLET_APPLICATION_STARTUP = Sys.getEnv()
			.getServiceLoader(ServletApplicationStartup.class, SERVLET3_APPLICATION_STARTUP_CLASS,
					SERVLET_APPLICATION_STARTUP_CLASS)
			.getServices().first();

	public static ServletApplicationStartup getServletApplicationStartup() {
		return SERVLET_APPLICATION_STARTUP;
	}

	public static Application getApplication(ServletContext servletContext) {
		return (Application) servletContext.getAttribute(Application.class.getName());
	}

	public static void setApplication(ServletContext servletContext, Application application) {
		servletContext.setAttribute(Application.class.getName(), application);
	}

	public static void startLogger(Logger logger, ServletContext servletContext, @Nullable Throwable e,
			boolean initialized) {
		String message = (initialized ? "Started" : "Start") + " servlet context[{}] realPath / in {}";
		if (e == null) {
			logger.info(message, servletContext.getContextPath(), getWebRoot(servletContext));
		} else {
			logger.error(e, message, servletContext.getContextPath(), getWebRoot(servletContext));
		}
	}

	public static void destroyLogger(Logger logger, ServletContext servletContext, @Nullable Throwable e,
			boolean destroyed) {
		String message = (destroyed ? "Destroyed" : "Destroy") + "Destroy servlet context[{}] realPath / in {}";
		if (e == null) {
			logger.info(message, servletContext.getContextPath(), getWebRoot(servletContext));
		} else {
			logger.error(e, message, servletContext.getContextPath(), getWebRoot(servletContext));
		}
	}

	public static String getWebRoot(ServletContext servletContext) {
		return servletContext.getRealPath("/");
	}

	public static Scope getScope(ServletContext servletContext) {
		String scope = servletContext.getInitParameter("scope");
		return StringUtils.isEmpty(scope) ? Scope.DEFAULT : Scope.getUniqueScope(scope);
	}
}
