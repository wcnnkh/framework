package scw.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.aop.annotation.AopEnable;
import scw.application.Application;
import scw.application.ApplicationAware;
import scw.application.ApplicationCountLatchAware;
import scw.core.instance.annotation.Configuration;
import scw.http.HttpStatus;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.servlet.http.HttpServletService;
import scw.util.concurrent.CountLatch;

@Configuration(order = Integer.MIN_VALUE)
@AopEnable(false)
public class DispatcherServlet extends HttpServlet implements ApplicationAware, ApplicationCountLatchAware {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerUtils.getLogger(DispatcherServlet.class);
	private Application application;
	private HttpServletService httpServletService;
	private ServletContext servletContext;
	private boolean reference = false;
	private CountLatch countLatch;
	private volatile boolean initialized = false;

	public void setApplication(Application application) {
		reference = true;
		this.application = application;
	}

	public Application getApplication() {
		return application;
	}

	public void setInitializationCountLatch(CountLatch countLatch) {
		this.countLatch = countLatch;
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
			this.servletContext = servletConfig.getServletContext();
			logger.info("Servlet context realPath / in {}", servletContext.getRealPath("/"));
			try {
				if (application == null) {
					application = (Application) servletConfig.getServletContext()
							.getAttribute(Application.class.getName());
					if (application == null) {
						application = new ServletApplication(servletConfig);
					} else {
						reference = true;
					}
				}

				// 如果未初始化就在这里初始化
				if (!application.isInitialized()) {
					application.init();
				}

				ServletUtils.servletContextInitialization(servletContext, application);

				if (httpServletService == null && application != null) {
					this.httpServletService = application.getBeanFactory().getInstance(HttpServletService.class);
				}

				if (countLatch != null) {
					countLatch.countDown();
				}
			} catch (Throwable e) {
				logger.error(e, "Initialization error");
			}
		}
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
			if (application != null && !reference && application.isInitialized()) {
				application.destroy();
			}
			super.destroy();
		}
	}
}
