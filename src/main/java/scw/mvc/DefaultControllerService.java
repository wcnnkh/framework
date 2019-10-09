package scw.mvc;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public final class DefaultControllerService implements ControllerService {
	private final Collection<Filter> filters;
	private final int warnExecuteTime;
	private final Collection<ExceptionHandler> exceptionHandlers;

	public DefaultControllerService(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Throwable {
		this.warnExecuteTime = MVCUtils.getWarnExecuteTime(propertyFactory);
		this.filters = MVCUtils.getFilters(beanFactory, propertyFactory);
		this.exceptionHandlers = MVCUtils.getExceptionHandlers(beanFactory, propertyFactory);
	}

	public void service(Channel channel) {
		MVCUtils.service(filters, channel, warnExecuteTime, exceptionHandlers);
	}
}
