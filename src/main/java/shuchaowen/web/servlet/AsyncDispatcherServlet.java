package shuchaowen.web.servlet;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shuchaowen.core.util.XTime;

public class AsyncDispatcherServlet extends DispatcherServlet{
	private static final long serialVersionUID = 1L;
	private ExecutorService service;
	
	@Override
	public void controller(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws IOException, ServletException {
		if(httpServletRequest.isAsyncSupported()){
				httpServletRequest.startAsync(httpServletRequest, httpServletResponse);
		}
		service.submit(new AsyncController(httpServletRequest, httpServletResponse, this));
	}
	
	public void setService(ExecutorService service) {
		this.service = service;
	}
	
	@Override
	public void init() throws ServletException {
		if(service == null){
			service = new ThreadPoolExecutor(20, 200, XTime.ONE_HOUR, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1000));
		}
		super.init();
	}
	
	@Override
	public void destroy() {
		service.shutdownNow();
		super.destroy();
	}
}

class AsyncRPC implements Runnable{
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private DispatcherServlet dispatcherServlet;
	

	public AsyncRPC(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, DispatcherServlet dispatcherServlet) {
		this.dispatcherServlet = dispatcherServlet;
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;
	}

	public void run() {
		try {
			dispatcherServlet.getHttpServerApplication().rpc(httpServletRequest.getInputStream(), httpServletResponse.getOutputStream());
		} catch (Throwable e) {
			e.printStackTrace();
		}finally {
			if(httpServletRequest.isAsyncStarted()){
				httpServletRequest.getAsyncContext().complete();
			}
		}
	}
}

class AsyncController implements Runnable{
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private DispatcherServlet dispatcherServlet;
	
	public AsyncController(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, DispatcherServlet dispatcherServlet) {
		this.dispatcherServlet = dispatcherServlet;
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;
	}
	
	public void run() {
		WebRequest request;
		try {
			request = dispatcherServlet.wrapperRequest(httpServletRequest, httpServletResponse);
			if(!dispatcherServlet.getHttpServerApplication().service(request, new WebResponse(request, httpServletResponse))){
				httpServletResponse.sendError(404, request.getServletPath());
			}
 		} catch (Throwable e) {
			try {
				httpServletResponse.sendError(500);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally {
			if (httpServletRequest.isAsyncStarted()) {
				httpServletRequest.getAsyncContext().complete();
			}
		}
	}
}
