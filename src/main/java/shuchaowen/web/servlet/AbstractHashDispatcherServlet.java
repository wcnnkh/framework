package shuchaowen.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shuchaowen.common.utils.ProcessorHashQueue;
import shuchaowen.core.util.StringUtils;

public abstract class AbstractHashDispatcherServlet extends DispatcherServlet{
	private static final long serialVersionUID = 1L;
	private ProcessorHashQueue<Integer> queueHash;
	
	@Override
	public void init() throws ServletException {
		int poolSize = StringUtils.conversion(getConfig("poolSize", 50 + ""), int.class);
		queueHash = new ProcessorHashQueue<Integer>(poolSize, 10000);
		queueHash.start();
		super.init();
	}
	
	@Override
	public void destroy() {
		queueHash.destroy();
		super.destroy();
	}
	
	@Override
	public void controller(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws IOException, ServletException {
		Request request = wrapperRequest(httpServletRequest, httpServletResponse);
		if(httpServletRequest.isAsyncSupported()){
			httpServletRequest.startAsync(httpServletRequest, httpServletResponse);
		}
		
		try {
			queueHash.process(hashcode(request), new AsyncHashController(request, httpServletResponse, this));
		} catch (InterruptedException e) {
			throw new ServletException(e);
		}
	}
	
	protected final int ipHashCode(Request request){
		String ip = request.getIP();
		return ip == null? 0:ip.hashCode();
	}
	
	protected final int paramHashCode(Request request, String name){
		String value = request.getString(name);
		return value == null? 0:value.hashCode();
	}
	
	protected final int servletPathHashCode(HttpServletRequest httpServletRequest){
		String servletPath = httpServletRequest.getServletPath();
		return servletPath == null? 0:servletPath.hashCode();
	}
	
	protected abstract int hashcode(Request request);
}

class AsyncHashController implements Runnable{
	private Request request;
	private HttpServletResponse httpServletResponse;
	private DispatcherServlet dispatcherServlet;
	
	public AsyncHashController(Request request, HttpServletResponse httpServletResponse, DispatcherServlet dispatcherServlet) {
		this.request = request;
		this.httpServletResponse = httpServletResponse;
		this.dispatcherServlet = dispatcherServlet;
	}
	
	public void run() {
		try {
			if(!dispatcherServlet.getHttpServerApplication().service(request, new Response(request, httpServletResponse))){
				httpServletResponse.sendError(404, request.getServletPath());
			}
		} catch (Throwable e) {
			try {
				httpServletResponse.sendError(500, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}finally{
			if (request.isAsyncStarted()) {
				request.getAsyncContext().complete();
			}
		}
	}
}
