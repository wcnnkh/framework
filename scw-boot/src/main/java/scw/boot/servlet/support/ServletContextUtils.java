package scw.boot.servlet.support;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import scw.beans.BeanFactory;
import scw.boot.Application;
import scw.boot.servlet.ServletApplicationStartup;
import scw.env.Sys;
import scw.lang.Nullable;
import scw.logger.Logger;

public abstract class ServletContextUtils {
	private static final ServletApplicationStartup SERVLET_APPLICATION_STARTUP = Sys.getInstanceFactory().getServiceLoader(ServletApplicationStartup.class,
			"scw.boot.servlet.support.Servlet3ApplicationStartup", "scw.boot.servlet.support.DefaultServletApplicationStartup").getFirst();
	
	public static ServletApplicationStartup getServletApplicationStartup() {
		return SERVLET_APPLICATION_STARTUP;
	}
	
	public static Application getApplication(ServletContext servletContext){
		return (Application) servletContext.getAttribute(Application.class.getName());
	}
	
	public static void setApplication(ServletContext servletContext, Application application){
		servletContext.setAttribute(Application.class.getName(), application);
	}
	
	public static void startLogger(Logger logger, ServletContext servletContext, @Nullable Throwable e, boolean initialized){
		String message = (initialized? "Started":"Start") + " servlet context[{}] realPath / in {}";
		if(e == null){
			logger.info(message, servletContext.getContextPath(), getWebRoot(servletContext));
		}else{
			logger.error(e, message, servletContext.getContextPath(), getWebRoot(servletContext));
		}
	}
	
	public static void destroyLogger(Logger logger, ServletContext servletContext, @Nullable Throwable e, boolean destroyed){
		String message = (destroyed? "Destroyed":"Destroy") + "Destroy servlet context[{}] realPath / in {}";
		if(e == null){
			logger.info(message, servletContext.getContextPath(), getWebRoot(servletContext));
		}else{
			logger.error(e, message, servletContext.getContextPath(), getWebRoot(servletContext));
		}
	}
	
	public static String getWebRoot(ServletContext servletContext){
		return servletContext.getRealPath("/");
	}
	
	public static Servlet createServlet(BeanFactory beanFactory){
		if(beanFactory.isInstance(Servlet.class)){
			return beanFactory.getInstance(Servlet.class);
		}
		return new DispatcherServlet();
	}
}
