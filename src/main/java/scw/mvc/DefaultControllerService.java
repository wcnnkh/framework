package scw.mvc;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.core.Destroy;
import scw.core.PropertyFactory;
import scw.core.context.Context;
import scw.core.context.ContextExecute;

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
		try {
			MVCUtils.execute(new MVCContextExecute(channel));
		} catch (Throwable e) {
			channel.getLogger().error(e, channel.toString());
		}
	}

	final class MVCContextExecute implements ContextExecute<Void> {
		private Channel channel;

		public MVCContextExecute(Channel channel) {
			this.channel = channel;
		}

		public Void execute(Context context) throws Throwable {
			long t = System.currentTimeMillis();
			context.bindResource(Channel.class, channel);
			FilterChain filterChain = new SimpleFilterChain(filters);
			try {
				channel.write(filterChain.doFilter(channel));
			} catch (Throwable e) {
				ExceptionHandlerChain exceptionHandlerChain = new ExceptionHandlerChain(exceptionHandlers);
				Object errorResult = exceptionHandlerChain.doHandler(channel, e);
				try {
					channel.write(errorResult);
				} catch (Throwable e2) {
					throw e2;
				}
			} finally {
				try {
					if (channel instanceof Destroy) {
						((Destroy) channel).destroy();
					}
				} finally {
					t = System.currentTimeMillis() - t;
					if (t > warnExecuteTime) {
						channel.getLogger().warn("执行{}超时，用时{}ms", channel.toString(), t);
					}
				}
			}
			return null;
		}
	}
}
