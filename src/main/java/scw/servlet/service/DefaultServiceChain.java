package scw.servlet.service;

import java.util.Collection;
import java.util.Iterator;

import scw.common.utils.CollectionUtils;
import scw.servlet.Request;
import scw.servlet.Response;

public class DefaultServiceChain implements ServiceChain {
	private Iterator<Service> iterator;

	public DefaultServiceChain(Collection<Service> services) {
		if (!CollectionUtils.isEmpty(services)) {
			iterator = services.iterator();
		}
	}

	public void service(Request request, Response response) throws Throwable {
		if (iterator.hasNext()) {
			iterator.next().service(request, response, this);
		}
		return;
	}

}
