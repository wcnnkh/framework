package scw.servlet.mvc;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.mvc.service.ChannelService;
import scw.servlet.mvc.http.HttpServletChannelFactory;

public class AsyncServletService extends DefaultServletService implements scw.core.Destroy {
	private final ThreadPoolExecutor threadPoolExecutor;
	
	public AsyncServletService(BeanFactory beanFactory, PropertyFactory propertyFactory){
		super(beanFactory, propertyFactory);
		this.threadPoolExecutor = getThreadPoolExecutor(beanFactory, propertyFactory);
	}

	public AsyncServletService(HttpServletChannelFactory httpServletChannelFactory, String charsetName, ChannelService channelService, ThreadPoolExecutor threadPoolExecutor){
		super(httpServletChannelFactory, charsetName, channelService);
		this.threadPoolExecutor = threadPoolExecutor;
	}
	
	private static ThreadPoolExecutor getThreadPoolExecutor(BeanFactory beanFactory, PropertyFactory propertyFactory){
		ThreadPoolExecutor threadPoolExecutor = null;
		if(!StringUtils.parseBoolean(propertyFactory.getProperty("servlet.thread.container"))){
			int coreSize = StringUtils.parseInt(propertyFactory.getProperty("servlet.thread.core.size"), 16);
			int maxSize = StringUtils.parseInt(propertyFactory.getProperty("servlet.thread.max.size"), 512);
			threadPoolExecutor = new ThreadPoolExecutor(coreSize, maxSize, 10, TimeUnit.MINUTES,
					new LinkedBlockingQueue<Runnable>());
		}
		return threadPoolExecutor;
	}

	@Override
	public void service(ServletRequest req, ServletResponse resp) {
		if (req.isAsyncSupported() && !req.isAsyncStarted()) {
			Execute command = new Execute(req, resp);
			if (threadPoolExecutor == null) {
				command.defaultExecute();
			} else {
				threadPoolExecutor.execute(command);
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
		if (threadPoolExecutor != null) {
			threadPoolExecutor.shutdownNow();
		}
	}

}
