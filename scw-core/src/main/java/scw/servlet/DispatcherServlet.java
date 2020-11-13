package scw.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.application.Application;
import scw.application.ApplicationAware;
import scw.application.ApplicationUtils;
import scw.application.CommonApplication;
import scw.beans.BeanLifeCycleEvent;
import scw.beans.BeanLifeCycleEvent.Step;
import scw.core.instance.annotation.Configuration;
import scw.event.EventListener;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.servlet.beans.ServletContextAware;
import scw.servlet.http.HttpServletService;

@Configuration(order = Integer.MIN_VALUE)
public class DispatcherServlet extends HttpServlet implements ApplicationAware {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils.getLogger(DispatcherServlet.class);
	private Application application;
	private HttpServletService httpServletService;
	private ServletContext servletContext;
	private boolean reference = false;

	public void setApplication(Application application) {
		reference = true;
		this.application = application;
	}

	public Application getApplication() {
		return application;
	}

	public HttpServletService getServletService() {
		return httpServletService;
	}

	public void setHttpServletService(HttpServletService httpServletService) {
		this.httpServletService = httpServletService;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (httpServletService == null) {
			// 未初始化或初始化错误
			resp.sendError(500, "Uninitialized or initialization error");
			return;
		}
		getServletService().service(req, resp);
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		this.servletContext = servletConfig.getServletContext();
		logger.info("Servlet context realPath / in {}", servletContext.getRealPath("/"));
		ServletConfigPropertyFactory propertyFactory = new ServletConfigPropertyFactory(servletConfig);
		try {
			if (application == null) {
				this.application = new CommonApplication(propertyFactory.getConfigXml());
			}

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
			application.getPropertyFactory().addLastBasePropertyFactory(propertyFactory);
			// 如果未初始化就在这里初始化
			if (!application.isInitialized()) {
				application.init();
			}

			for (ServletContextBootstrap bootstrap : ApplicationUtils.loadAllService(ServletContextBootstrap.class,
					application)) {
				bootstrap.init(servletContext);
			}

			if (httpServletService == null && application != null) {
				this.httpServletService = application.getBeanFactory().getInstance(HttpServletService.class);
			}
		} catch (Throwable e) {
			logger.error(e, "Initialization error");
		}
	}

	@Override
	public void destroy() {
		if (application != null && !reference && application.isInitialized()) {
			application.destroy();
		}
		super.destroy();
	}
}
