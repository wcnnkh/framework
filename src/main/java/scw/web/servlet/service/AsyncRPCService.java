package scw.web.servlet.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.web.servlet.HttpServerApplication;

public final class AsyncRPCService implements Runnable{
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private HttpServerApplication httpServerApplication;
	
	public AsyncRPCService(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, HttpServerApplication httpServerApplication){
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;
		this.httpServerApplication = httpServerApplication;
	}
	
	public void run() {
		try {
			httpServerApplication.rpc(httpServletRequest.getInputStream(), httpServletResponse.getOutputStream());
		} catch (Throwable e) {
			e.printStackTrace();
			httpServerApplication.sendError(httpServletRequest, httpServletResponse, 500, "system error");
		}finally{
			if (httpServletRequest.isAsyncStarted()) {
				httpServletRequest.getAsyncContext().complete();
			}
		}
	}
}
