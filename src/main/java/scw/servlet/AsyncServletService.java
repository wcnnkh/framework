package scw.servlet;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.beans.BeanFactory;
import scw.beans.annotation.Destroy;
import scw.beans.property.PropertiesFactory;

public class AsyncServletService extends DefaultServletService {
	private ThreadPoolExecutor executor;

	public AsyncServletService(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String configPath,
			String[] rootBeanFilters) throws Throwable {
		super(beanFactory, propertiesFactory, configPath, rootBeanFilters);
	}

	@Override
	public void service(ServletRequest req, ServletResponse resp) {
		if (req.isAsyncSupported() && !req.isAsyncStarted()) {
			if (executor == null) {
				synchronized (this) {
					if (executor == null) {
						executor = new ThreadPoolExecutor(1, 500, 1, TimeUnit.MINUTES,
								new LinkedBlockingQueue<Runnable>());
					}
				}
			}

			executor.execute(new Execute(this, req, resp));
			return;
		}
		super.service(req, resp);
	}

	final class Execute implements Runnable {
		private final ServletService servletService;
		private final ServletRequest req;
		private final ServletResponse resp;
		private final AsyncContext asyncContext;

		public Execute(ServletService servletService, ServletRequest req, ServletResponse resp) {
			this.servletService = servletService;
			this.req = req;
			this.resp = resp;
			this.asyncContext = req.startAsync(req, resp);
		}

		public void run() {
			try {
				servletService.service(req, resp);
			} finally {
				asyncContext.complete();
			}
		}
	}

	@Destroy
	public void destroy() {
		if (executor != null) {
			executor.shutdownNow();
		}
	}
}
