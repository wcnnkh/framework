package scw.boot.servlet.support;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanLifeCycleEvent.Step;
import scw.boot.Application;
import scw.boot.servlet.ServletApplicationStartup;
import scw.boot.servlet.ServletContextAware;
import scw.boot.servlet.ServletContextInitialization;
import scw.event.EventListener;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class DefaultServletApplicationStartup implements ServletApplicationStartup{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	protected StartUp getStartup(ServletContext servletContext) throws ServletException{
		Application application = ServletContextUtils.getApplication(servletContext);
		StartUp startUp;
		if(application == null){
			ServletContextUtils.startLogger(logger, servletContext, null, false);
			application = new ServletApplication(servletContext);
			try {
				application.init();
			} catch (Throwable e) {
				ServletContextUtils.startLogger(logger, servletContext, e, false);
			}
			ServletContextUtils.setApplication(servletContext, application);
			startUp = new StartUp(application, true);
		}else{
			startUp = new StartUp(application, false);
		}
		return startUp;
	}
	
	public StartUp start(ServletContext servletContext) throws ServletException {
		StartUp startUp = getStartup(servletContext);
		start(servletContext, startUp.getApplication());
		return startUp;
	}

	public final boolean start(final ServletContext servletContext,
			Application application) throws ServletException {
		String nameToUse = ServletApplicationStartup.class.getName();
		if (servletContext.getAttribute(nameToUse) != null) {
			return false;
		}

		servletContext.setAttribute(nameToUse, true);
		
		application.getBeanFactory().getBeanLifeCycleEventDispatcher().registerListener(new EventListener<BeanLifeCycleEvent>() {

			public void onEvent(BeanLifeCycleEvent event) {
				if (event.getStep() == Step.BEFORE_INIT) {
					Object source = event.getSource();
					if (source != null && source instanceof ServletContextAware) {
						((ServletContextAware) source).setServletContext(servletContext);
					}
				}
			}
		});
		
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
