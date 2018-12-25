package scw.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.common.ProcessorHashQueue;
import scw.common.utils.StringUtils;
import scw.servlet.service.AsyncRPCService;
import scw.servlet.service.AsyncRequestService;

public abstract class AbstractHashDispatcherServlet extends DispatcherServlet {
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
	protected void myService(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		try {
			if (getServletApplication().checkRPCRequest(httpServletRequest)) {
				queueHash.process(httpServletRequest.hashCode(),
						new AsyncRPCService(httpServletRequest, httpServletResponse, getServletApplication()));
			} else {
				AsyncRequestService asyncRequestService = new AsyncRequestService(httpServletRequest,
						httpServletResponse, getServletApplication());
				queueHash.process(hashcode(asyncRequestService.getRequest()), asyncRequestService);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected final int ipHashCode(Request request) {
		String ip = request.getIP();
		return ip == null ? 0 : ip.hashCode();
	}

	protected final int paramHashCode(Request request, String name) {
		String value = request.getString(name);
		return value == null ? 0 : value.hashCode();
	}

	protected final int servletPathHashCode(HttpServletRequest httpServletRequest) {
		String servletPath = httpServletRequest.getServletPath();
		return servletPath == null ? 0 : servletPath.hashCode();
	}

	protected abstract int hashcode(Request request);
}
