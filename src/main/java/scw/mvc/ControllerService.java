package scw.mvc;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.PropertyFactory;

public final class ControllerService {
	private final Collection<Filter> filters;
	private final long warnExecuteTime;
	private final Collection<ExceptionHandler> exceptionHandlers;

	public ControllerService(BeanFactory beanFactory, PropertyFactory propertyFactory) throws Throwable {
		this(MVCUtils.getFilters(beanFactory, propertyFactory), MVCUtils.getWarnExecuteTime(propertyFactory),
				MVCUtils.getExceptionHandlers(beanFactory, propertyFactory));
	}

	public ControllerService(Collection<Filter> filters, long warnExecuteTime,
			Collection<ExceptionHandler> exceptionHandlers) {
		this.filters = filters;
		this.exceptionHandlers = exceptionHandlers;
		this.warnExecuteTime = warnExecuteTime;
	}

	public void service(Channel channel) {
		MVCUtils.service(channel, filters, warnExecuteTime, exceptionHandlers);
	}
}
