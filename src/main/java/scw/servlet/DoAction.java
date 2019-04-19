package scw.servlet;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.common.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.http.enums.Method;
import scw.servlet.action.SearchAction;

public class DoAction implements FilterChain {
	private static Logger logger = LoggerFactory.getLogger(DoAction.class);
	private final SearchAction searchAction;
	private Iterator<Filter> iterator;

	public DoAction(SearchAction searchAction, Collection<Filter> filters) {
		this.searchAction = searchAction;
		if (!CollectionUtils.isEmpty(filters)) {
			iterator = filters.iterator();
		}
	}

	public void doFilter(Request request, Response response) throws Throwable {
		if (iterator == null) {
			doAction(request, response);
			return;
		}

		if (iterator.hasNext()) {
			iterator.next().doFilter(request, response, this);
		} else {
			doAction(request, response);
		}
	}

	private void doAction(Request request, Response response) throws Throwable {
		Action action = searchAction.getAction(request);
		if (action == null) {
			if (request.getMethod().equals(Method.OPTIONS.name())) {
				// 判断是否是跨域校验
				if (optionsCrossDomain(request, response)) {
					return;
				}
			}
			sendError(request, response, 404, "not found action");
			return;
		}

		action.doAction(request, response);
	}

	private boolean optionsCrossDomain(Request request, Response response) {
		// TODO 返回跨域信息
		return true;
	}

	private void sendError(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, int code,
			String msg) {
		StringBuilder sb = new StringBuilder();
		sb.append("servletPath=").append(httpServletRequest.getServletPath());
		sb.append(",method=").append(httpServletRequest.getMethod());
		sb.append(",status=").append(code);
		sb.append(",msg=").append(msg);
		logger.error(sb.toString());

		if (!httpServletResponse.isCommitted()) {
			try {
				httpServletResponse.sendError(code, msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
