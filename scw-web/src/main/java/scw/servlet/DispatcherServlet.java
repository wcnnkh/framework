package scw.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.boot.Application;
import scw.boot.ApplicationAware;
import scw.boot.servlet.ServletApplicationStartup.StartUp;
import scw.boot.servlet.support.ServletContextUtils;
import scw.context.annotation.Provider;
import scw.http.HttpStatus;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

@Provider(order = Integer.MIN_VALUE)
public class DispatcherServlet extends HttpServlet implements ApplicationAware {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils.getLogger(DispatcherServlet.class);
	private Application application;
	private ServletService servletService;
	private boolean reference = true;
	private volatile boolean initialized = false;

	public void setApplication(Application application) {
		reference = true;
		this.application = application;
		if(application.getBeanFactory().isInstance(ServletService.class)){
			setServletService(application.getBeanFactory().getInstance(ServletService.class));
		}
	}

	public Application getApplication() {
		return application;
	}

	public ServletService getServletService() {
		return servletService;
	}

	public void setServletService(ServletService servletService) {
		this.servletService = servletService;
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (servletService == null) {
			//服务未初始化或初始化失败
			resp.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), "Uninitialized or initialization error");
			return;
		}
		getServletService().service(req, resp);
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		if (initialized) {
			return;
		}

		synchronized (this) {
			if (initialized) {
				return;
			}

			initialized = true;
			try {
				if(application == null){
					StartUp startUp = ServletContextUtils.getServletApplicationStartup().start(servletConfig.getServletContext());
					this.application = startUp.getApplication();
					if(startUp.isNew()){
						reference = false;
					}
				}

				if (servletService == null && application != null && application.getBeanFactory().isInstance(ServletService.class)) {
					this.servletService = application.getBeanFactory().getInstance(ServletService.class);
				}
			} catch (Throwable e) {
				ServletContextUtils.startLogger(logger, servletConfig.getServletContext(), e, false);
			}
		}
		super.init(servletConfig);
	}

	@Override
	public void destroy() {
		if (!initialized) {
			return;
		}

		synchronized (this) {
			if (!initialized) {
				return;
			}

			initialized = false;
			if (application != null && !reference) {
				try {
					application.destroy();
				} catch (Throwable e) {
					ServletContextUtils.destroyLogger(logger, getServletContext(), e, false);
				}
			}
			super.destroy();
		}
	}
}
