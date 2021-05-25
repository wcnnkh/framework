package scw.boot.servlet.support;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.boot.Application;
import scw.boot.ApplicationAware;
import scw.boot.ConfigurableApplication;
import scw.http.HttpStatus;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.util.Result;
import scw.web.servlet.ServletService;
import scw.web.servlet.ServletUtils;

public class DispatcherServlet extends HttpServlet implements ApplicationAware {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);
	private Application application;
	private ServletService servletService;
	private boolean reference = true;
	private volatile boolean initialized = false;

	public void setApplication(Application application) {
		reference = true;
		this.application = application;
		setServletService(ServletUtils.createServletService(application.getBeanFactory()));
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
					Result<ConfigurableApplication> startUp = ServletContextUtils.getServletApplicationStartup().start(servletConfig.getServletContext());
					this.application = startUp.getResult();
					if(startUp.isActive()){
						reference = false;
					}
				}

				if (servletService == null && application != null) {
					this.servletService = ServletUtils.createServletService(application.getBeanFactory());
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
