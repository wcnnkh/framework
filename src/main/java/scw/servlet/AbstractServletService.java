package scw.servlet;

import java.util.LinkedList;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import scw.core.Destroy;

public abstract class AbstractServletService extends LinkedList<Filter> implements ServletService {
	private static final long serialVersionUID = 1L;

	protected abstract WrapperFactory getWrapperFactory();

	public void service(ServletRequest req, ServletResponse resp) {
		Request request = null;
		Response response = null;
		FilterChain filterChain = new scw.servlet.DefaultFilterChain(this, null);
		try {
			request = getWrapperFactory().wrapperRequest(req, resp);
			if (request == null) {
				return;
			}

			response = getWrapperFactory().wrapperResponse(request, resp);
			if (response == null) {
				return;
			}

			filterChain.doFilter(request, response);
		} catch (Throwable e) {
			error(request, response, e);
		} finally {
			if (request != null) {
				if (request instanceof Destroy) {
					((Destroy) request).destroy();
				}
			}
		}
	}

	protected abstract void error(ServletRequest request, ServletResponse response, Throwable e);
}
