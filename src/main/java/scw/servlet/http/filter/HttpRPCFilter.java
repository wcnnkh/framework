package scw.servlet.http.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.beans.annotation.Bean;
import scw.beans.rpc.http.RpcService;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;

@Bean(proxy = false)
public class HttpRPCFilter implements Filter {
	private final String path;
	private final RpcService rpcService;

	public HttpRPCFilter(String path, RpcService rpcService) {
		this.path = path;
		this.rpcService = rpcService;
	}

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		if (!ServletUtils.isHttpServlet(request, response)) {
			filterChain.doFilter(request, response);
			return;
		}

		if (rpc((HttpServletRequest) request, (HttpServletResponse) response)) {
			return;
		}

		filterChain.doFilter(request, response);
	}

	private boolean rpc(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		if (rpcService == null) {
			return false;
		}

		if (!"POST".equals(request.getMethod())) {
			return false;
		}

		if (!request.getServletPath().equals(path)) {
			return false;
		}

		rpcService.service(request.getInputStream(), response.getOutputStream());
		return true;
	}
}
