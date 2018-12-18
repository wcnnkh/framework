package scw.servlet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.common.utils.XTime;
import scw.servlet.service.AsyncRPCService;
import scw.servlet.service.AsyncRequestService;

public class AsyncDispatcherServlet extends DispatcherServlet {
	private static final long serialVersionUID = 1L;
	private ExecutorService service;
	
	@Override
	protected void myService(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		Runnable runnable;
		if(getHttpServerApplication().checkRPCRequest(httpServletRequest)){
			runnable = new AsyncRPCService(httpServletRequest, httpServletResponse, getHttpServerApplication());
		}else{
			runnable = new AsyncRequestService(httpServletRequest, httpServletResponse,  getHttpServerApplication());
		}
		service.submit(runnable);
	}

	public void setService(ExecutorService service) {
		this.service = service;
	}

	@Override
	public void init() throws ServletException {
		if (service == null) {
			service = new ThreadPoolExecutor(20, 200, XTime.ONE_HOUR,
					TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
							1000));
		}
		super.init();
	}

	@Override
	public void destroy() {
		service.shutdownNow();
		super.destroy();
	}
}