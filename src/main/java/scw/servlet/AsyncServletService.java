package scw.servlet;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.beans.BeanFactory;
import scw.beans.annotation.Bean;
import scw.beans.property.ValueWiredManager;
import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;

@Bean(proxy = false)
public class AsyncServletService extends DefaultServletService implements scw.core.Destroy {
	private ThreadPoolExecutor executor;
	/**
	 * 把线程交给servlet容器来管理
	 */
	private final boolean containerThreadManager;

	public AsyncServletService(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory, String configPath,
			String[] rootBeanFilters) throws Throwable {
		super(valueWiredManager, beanFactory, propertyFactory, configPath, rootBeanFilters);
		int coreSize = StringUtils.parseInt(propertyFactory.getProperty("servlet.thread.core.size"), 16);
		int maxSize = StringUtils.parseInt(propertyFactory.getProperty("servlet.thread.max.size"), 512);
		this.containerThreadManager = StringUtils.parseBoolean(propertyFactory.getProperty("servlet.thread.container"));
		if (!containerThreadManager) {
			executor = new ThreadPoolExecutor(coreSize, maxSize, 10, TimeUnit.MINUTES,
					new LinkedBlockingQueue<Runnable>());
		}
	}

	@Override
	public void service(ServletRequest req, ServletResponse resp) {
		if (req.isAsyncSupported() && !req.isAsyncStarted()) {
			Execute command = new Execute(req, resp);
			if (containerThreadManager) {
				command.defaultExecute();
			} else {
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

	public void destroy() {
		if (executor != null) {
			executor.shutdownNow();
		}
	}

}
