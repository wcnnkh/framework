package scw.servlet;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.application.CommonApplication;

public class AsyncService extends DefaultService {
	private ThreadPoolExecutor executor;

	public AsyncService(CommonApplication commonApplication) throws Throwable {
		super(commonApplication);
	}

	@Override
	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws Throwable {
		if (httpServletRequest.isAsyncSupported() && !httpServletRequest.isAsyncStarted()) {
			if (executor == null) {
				synchronized (this) {
					if (executor == null) {
						executor = new ThreadPoolExecutor(1, 500, 1, TimeUnit.MINUTES,
								new LinkedBlockingQueue<Runnable>());
					}
				}
			}

			executor.execute(new Execute(this, httpServletRequest, httpServletResponse));
			return;
		}
		super.service(httpServletRequest, httpServletResponse);
	}

	final class Execute implements Runnable {
		private final Service service;
		private final HttpServletRequest httpServletRequest;
		private final HttpServletResponse httpServletResponse;
		private final AsyncContext asyncContext;

		public Execute(Service service, HttpServletRequest httpServletRequest,
				HttpServletResponse httpServletResponse) {
			this.service = service;
			this.httpServletRequest = httpServletRequest;
			this.httpServletResponse = httpServletResponse;
			this.asyncContext = httpServletRequest.startAsync(httpServletRequest, httpServletResponse);
		}

		public void run() {
			try {
				service.service(httpServletRequest, httpServletResponse);
			} catch (Throwable e) {
				service.sendError(httpServletRequest, httpServletResponse, e);
			} finally {
				asyncContext.complete();
			}
		}
	}

	@Override
	public void destroy() {
		if (executor != null) {
			executor.shutdownNow();
		}
		super.destroy();
	}
}
