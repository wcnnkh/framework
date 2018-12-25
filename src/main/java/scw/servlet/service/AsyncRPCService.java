package scw.servlet.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.servlet.ServletApplication;

public final class AsyncRPCService implements Runnable{
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	private ServletApplication servletApplication;
	
	public AsyncRPCService(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, ServletApplication servletApplication){
		this.httpServletRequest = httpServletRequest;
		this.httpServletResponse = httpServletResponse;
		this.servletApplication = servletApplication;
	}
	
	public void run() {
		try {
			servletApplication.rpc(httpServletRequest.getInputStream(), httpServletResponse.getOutputStream());
		} catch (Throwable e) {
			e.printStackTrace();
			servletApplication.sendError(httpServletRequest, httpServletResponse, 500, "system error");
		}finally{
			if (httpServletRequest.isAsyncStarted()) {
				httpServletRequest.getAsyncContext().complete();
			}
		}
	}
}
