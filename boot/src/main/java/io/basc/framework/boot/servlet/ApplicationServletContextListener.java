package io.basc.framework.boot.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import io.basc.framework.boot.Application;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

/**
 * 对于旧版本的servlet窗口，推荐使用此方式来初始化，而不是在servlet中进行初始化
 * 
 * @author wcnnkh
 *
 */
public class ApplicationServletContextListener implements ServletContextListener {
	private static Logger logger = LoggerFactory.getLogger(ApplicationServletContextListener.class);

	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		try {
			ServletContextUtils.getServletApplicationStartup().load(servletContext);
		} catch (ServletException e) {
			ServletContextUtils.startLogger(logger, servletContext, e, false);
		}
	}

	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		Application application = ServletContextUtils.getApplication(servletContext);
		if (application != null) {
			ServletContextUtils.startLogger(logger, servletContext, null, false);
			try {
				application.destroy();
			} catch (Throwable e) {
				ServletContextUtils.destroyLogger(logger, servletContext, e, false);
			}
		}
	}

}
