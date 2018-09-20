package shuchaowen.web.servlet;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shuchaowen.core.util.ProcessorQueueHash;
import shuchaowen.core.util.StringUtils;

public abstract class AbstractHashDispatcherServlet extends DispatcherServlet{
	private ProcessorQueueHash<Integer> queueHash;
	private static final long serialVersionUID = 1L;
	public ExecutorService threadPool;
	
	@Override
	public void init() throws ServletException {
		int poolSize = StringUtils.conversion(getConfig("poolSize", 50 + ""), int.class);
		threadPool = Executors.newFixedThreadPool(poolSize);
		queueHash = new ProcessorQueueHash<Integer>(threadPool, poolSize, 10000);
		super.init();
	}
	
	@Override
	public void destroy() {
		threadPool.shutdownNow();
		super.destroy();
	}
	
	@Override
	public void controller(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws IOException, ServletException {
		WebRequest request = wrapperRequest(httpServletRequest, httpServletResponse);
		if(httpServletRequest.isAsyncSupported()){
			httpServletRequest.startAsync(httpServletRequest, httpServletResponse);
		}
		
		queueHash.process(hashcode(request), new AsyncHashController(request, httpServletResponse, this));
	}
	
	protected final int ipHashCode(WebRequest request){
		String ip = request.getIP();
		return ip == null? 0:ip.hashCode();
	}
	
	protected final int paramHashCode(WebRequest request, String name){
		String value = request.getString(name);
		return value == null? 0:value.hashCode();
	}
	
	protected final int servletPathHashCode(HttpServletRequest httpServletRequest){
		String servletPath = httpServletRequest.getServletPath();
		return servletPath == null? 0:servletPath.hashCode();
	}
	
	protected abstract int hashcode(WebRequest request);
}

class AsyncHashController implements Runnable{
	private WebRequest request;
	private HttpServletResponse httpServletResponse;
	private DispatcherServlet dispatcherServlet;
	
	public AsyncHashController(WebRequest request, HttpServletResponse httpServletResponse, DispatcherServlet dispatcherServlet) {
		this.request = request;
		this.httpServletResponse = httpServletResponse;
		this.dispatcherServlet = dispatcherServlet;
	}
	
	public void run() {
		try {
			if(!dispatcherServlet.getHttpServerApplication().service(request, new WebResponse(request, httpServletResponse))){
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
