package scw.boot.servlet.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.boot.Application;
import scw.boot.ConfigurableApplication;
import scw.boot.servlet.ServletApplicationStartup;
import scw.boot.servlet.ServletContextInitialization;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.Result;

public class DefaultServletApplicationStartup implements ServletApplicationStartup{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	protected Result<ConfigurableApplication> getStartup(ServletContext servletContext) throws ServletException{
		ConfigurableApplication application = ServletContextUtils.getApplication(servletContext);
		if(application == null){
			ServletContextUtils.startLogger(logger, servletContext, null, false);
			application = new ServletApplication(servletContext);
			try {
				application.init();
			} catch (Throwable e) {
				ServletContextUtils.startLogger(logger, servletContext, e, false);
			}
			ServletContextUtils.setApplication(servletContext, application);
			return new Result<ConfigurableApplication>(true, application);
		}else{
			return new Result<ConfigurableApplication>(false, application);
		}
	}
	
	public Result<ConfigurableApplication> start(ServletContext servletContext) throws ServletException {
		Result<ConfigurableApplication> startUp = getStartup(servletContext);
		start(servletContext, startUp.getResult());
		return startUp;
	}

	public final boolean start(final ServletContext servletContext,
			ConfigurableApplication application) throws ServletException {
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
