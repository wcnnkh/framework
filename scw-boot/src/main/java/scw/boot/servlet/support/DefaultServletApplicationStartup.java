package scw.boot.servlet.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.boot.Application;
import scw.boot.servlet.ServletApplicationStartup;
import scw.boot.servlet.ServletContextInitialization;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.DefaultStatus;
import scw.util.Status;

public class DefaultServletApplicationStartup implements ServletApplicationStartup{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	protected Status<Application> getStartup(ServletContext servletContext) throws ServletException{
		Application application = ServletContextUtils.getApplication(servletContext);
		if(application == null){
			ServletContextUtils.startLogger(logger, servletContext, null, false);
			application = new ServletApplication(servletContext);
			try {
				application.init();
			} catch (Throwable e) {
				ServletContextUtils.startLogger(logger, servletContext, e, false);
			}
			ServletContextUtils.setApplication(servletContext, application);
			return new DefaultStatus<Application>(true, application);
		}else{
			return new DefaultStatus<Application>(false, application);
		}
	}
	
	public Status<Application> start(ServletContext servletContext) throws ServletException {
		Status<Application> startUp = getStartup(servletContext);
		start(servletContext, startUp.get());
		return startUp;
	}

	public final boolean start(final ServletContext servletContext,
			Application application) throws ServletException {
		String nameToUse = ServletApplicationStartup.class.getName();
		if (servletContext.getAttribute(nameToUse) != null) {
			return false;
		}

		servletContext.setAttribute(nameToUse, true);
		afterStarted(servletContext, application);
		ServletContextUtils.startLogger(logger, servletContext, null, true);
		return true;
	}
	
	protected void afterStarted(ServletContext servletContext,
			Application application) throws ServletException{
		for(ServletContextInitialization initialization : application.getBeanFactory().getServiceLoader(ServletContextInitialization.class)){
			initialization.init(application, servletContext);
		}
	}
}
