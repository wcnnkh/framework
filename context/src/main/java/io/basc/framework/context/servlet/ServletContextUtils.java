package io.basc.framework.context.servlet;

import javax.servlet.ServletContext;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.logging.Logger;

public abstract class ServletContextUtils {
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

	public static ServletApplicationContext getServletApplicationContext(ServletContext servletContext) {
		return (ServletApplicationContext) servletContext.getAttribute(ServletApplicationContext.class.getName());
	}

	public static void setServletApplicationContext(ServletContext servletContext,
			ServletApplicationContext servletApplicationContext) {
		servletContext.setAttribute(ServletContext.class.getName(), servletApplicationContext);
	}
}
