package scw.servlet;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.beans.annotation.Destroy;
import scw.beans.property.PropertiesFactory;
import scw.core.utils.StringParseUtils;

@Bean(proxy = false)
public class AsyncServletService extends DefaultServletService {
	private static final long serialVersionUID = 1L;
	private ThreadPoolExecutor executor;
	private final int coreSize;
	private final int maxSize;
	/**
	 * 把线程交给servlet容器来管理
	 */
	private final boolean containerThreadManager;

	public AsyncServletService(BeanFactory beanFactory, PropertiesFactory propertiesFactory, String configPath,
			String[] rootBeanFilters) throws Throwable {
		super(beanFactory, propertiesFactory, configPath, rootBeanFilters);
		this.coreSize = StringParseUtils.parseInt(propertiesFactory.getValue("servlet.thread.core.size"), 16);
		this.maxSize = StringParseUtils.parseInt(propertiesFactory.getValue("servlet.thread.max.size"), 256);
		this.containerThreadManager = StringParseUtils
				.parseBoolean(propertiesFactory.getValue("servlet.thread.container"), false);
	}

	@Override
	public void service(ServletRequest req, ServletResponse resp) {
		if (req.isAsyncSupported() && !req.isAsyncStarted()) {
			Execute command = new Execute(req, resp);
			if (containerThreadManager) {
				command.defaultExecute();
			} else {
				if (executor == null) {
					synchronized (this) {
						if (executor == null) {
							executor = new ThreadPoolExecutor(coreSize, maxSize, 1, TimeUnit.MINUTES,
									new LinkedBlockingQueue<Runnable>());
						}
					}
				}
				executor.execute(command);
			}
			return;
		}
		super.service(req, resp);
	}

	protected void asyncService(AsyncContext asyncContext) {
		try {
			super.service(asyncContext.getRequest(), asyncContext.getResponse());
		} finally {
			asyncContext.complete();
		}
	}

	final class Execute implements Runnable {
		private final AsyncContext asyncContext;

		public Execute(ServletRequest req, ServletResponse resp) {
			this.asyncContext = req.startAsync(req, resp);
		}

		public void run() {
			asyncService(asyncContext);
		}

		public void defaultExecute() {
			asyncContext.start(this);
		}
	}

	@Destroy
	public void destroy() {
		if (executor != null) {
			executor.shutdownNow();
		}
	}

}
