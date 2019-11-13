package scw.mvc;

import java.util.Collection;

import scw.core.context.Context;
import scw.core.context.ContextExecute;
import scw.core.utils.XUtils;

public class MvcExecute extends AbstractFilterChain implements ContextExecute<Void> {
	private final Channel channel;
	private final long warnExecuteTime;
	private final Collection<ExceptionHandler> exceptionHandlers;

	public MvcExecute(Channel channel, Collection<Filter> filters, long warnExecuteTime,
			Collection<ExceptionHandler> exceptionHandlers) {
		super(filters);
		this.channel = channel;
		this.warnExecuteTime = warnExecuteTime;
		this.exceptionHandlers = exceptionHandlers;
	}

	public Void execute(Context context) throws Throwable {
		channel.write(service(context));
		return null;
	}

	private Object service(Context context) {
		context.bindResource(Channel.class, channel);
		try {
			return doFilter(channel);
		} catch (Throwable e) {
			return error(channel, e);
		} finally {
			try {
				XUtils.destroy(channel);
			} finally {
				long t = System.currentTimeMillis() - channel.getCreateTime();
				if (t > warnExecuteTime) {
					channel.getLogger().warn("执行{}超时，用时{}ms", channel.toString(), t);
				}
			}
		}
	}

	private Object error(Channel channel, Throwable e) {
		ExceptionHandlerChain exceptionHandlerChain = new ExceptionHandlerChain(exceptionHandlers);
		return exceptionHandlerChain.doHandler(channel, e);
	}

	@Override
	protected Object lastFilter(Channel channel) throws Throwable {
		return null;
	}
};
