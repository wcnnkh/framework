package scw.servlet;

import java.util.Set;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import scw.application.Application;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.logger.SplitLineAppend;
import scw.servlet.ServletApplicationStartup.StartUp;

public class ApplicationServletContainerInitializer implements ServletContainerInitializer, ServletContextListener{
	private static Logger logger = LoggerFactory.getLogger(ApplicationServletContainerInitializer.class);
	
	public void onStartup(Set<Class<?>> classes, ServletContext servletContext)
			throws ServletException {
		logger.info("Start up servlet context[{}]", servletContext.getContextPath());
		StartUp startUp = ServletUtils.getServletApplicationStartup().start(classes, servletContext);
		if(startUp.isNew()){
			servletContext.addListener(this);
		}
	}

	public void contextInitialized(ServletContextEvent sce) {
	}

	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		logger.info(new SplitLineAppend("Servlet context[{}] destroyed"), servletContext.getContextPath());
		Application application = ServletUtils.getApplication(servletContext);
		if(application != null){
			application.destroy();
		}
	}
}
