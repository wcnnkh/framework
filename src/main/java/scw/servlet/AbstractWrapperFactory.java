package scw.servlet;

import scw.servlet.beans.RequestBeanFactory;

public abstract class AbstractWrapperFactory implements WrapperFactory {
	private final RequestBeanFactory requestBeanFactory;
	private final boolean debug;

	public AbstractWrapperFactory(RequestBeanFactory requestBeanFactory, boolean debug) {
		this.requestBeanFactory = requestBeanFactory;
		this.debug = debug;
	}

	public final RequestBeanFactory getRequestBeanFactory() {
		return requestBeanFactory;
	}

	public final boolean isDebug() {
		return debug;
	}
}
