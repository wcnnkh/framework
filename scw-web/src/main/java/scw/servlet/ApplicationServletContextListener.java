package scw.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import scw.boot.Application;
import scw.boot.servlet.support.ServletContextUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class ApplicationServletContextListener implements ServletContextListener{
	private static Logger logger = LoggerFactory.getLogger(ApplicationServletContextListener.class);
	
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		try {
			ServletContextUtils.getServletApplicationStartup().start(servletContext);
		} catch (ServletException e) {
			ServletContextUtils.startLogger(logger, servletContext, e, false);
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		Application application = ServletContextUtils.getApplication(servletContext);
		if(application != null){
			ServletContextUtils.startLogger(logger, servletContext, null, false);
			try {
				application.destroy();
			} catch (Throwable e) {
				ServletContextUtils.destroyLogger(logger, servletContext, e, false);
			}
		}
	}

}
