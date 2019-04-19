package scw.servlet;

import java.nio.charset.Charset;
import java.util.Collection;

import scw.servlet.action.SearchAction;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.request.RequestFactory;
import scw.servlet.rpc.RPCServer;

public interface ServiceConfig {
	Charset getCharset();

	RPCServer getRPCServer();

	SearchAction getSearchAction();

	RequestFactory getRequestFactory();

	Collection<Filter> getFilters();

	RequestBeanFactory getRequestBeanFactory();
}
