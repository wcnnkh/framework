package scw.servlet;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.application.CommonApplication;
import scw.servlet.action.SearchAction;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.request.RequestFactory;
import scw.servlet.rpc.RPCServer;

public class DefaultService extends AbstractService {
	private final RPCServer rpcServer;
	private final SearchAction searchAction;
	private final RequestBeanFactory requestBeanFactory;
	private final RequestFactory requestFactory;
	private final Collection<Filter> filters;

	public DefaultService(CommonApplication commonApplication) throws Throwable {
		this(new DefaultServiceConfig(commonApplication));
	}

	public DefaultService(ServiceConfig serviceConfig) {
		this(serviceConfig.getRPCServer(), serviceConfig.getRequestBeanFactory(), serviceConfig.getRequestFactory(),
				serviceConfig.getSearchAction(), serviceConfig.getFilters());
	}

	public DefaultService(RPCServer rpcServer, RequestBeanFactory requestBeanFactory, RequestFactory requestFactory,
			SearchAction searchAction, Collection<Filter> filters) {
		this.rpcServer = rpcServer;
		this.requestBeanFactory = requestBeanFactory;
		this.requestFactory = requestFactory;
		this.searchAction = searchAction;
		this.filters = filters;
	}

	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws Throwable {
		if (rpcServer != null && rpcServer.isRPC(httpServletRequest, httpServletResponse)) {
			rpc(httpServletRequest, httpServletResponse);
			return;
		}

		Request request = requestFactory.format(requestBeanFactory, httpServletRequest, httpServletResponse);
		doAction(searchAction, filters, request, request.getResponse());
	}

	protected void rpc(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws Throwable {
		rpcServer.service(httpServletRequest, httpServletResponse);
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}
}
