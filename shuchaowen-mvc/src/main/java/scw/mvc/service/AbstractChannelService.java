package scw.mvc.service;

import java.io.IOException;

import scw.core.utils.XUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.AsyncEvent;
import scw.mvc.AsyncListener;
import scw.mvc.Channel;
import scw.mvc.context.ContextManager;
import scw.mvc.exception.ExceptionHandlerChain;
import scw.mvc.handler.HandlerChain;
import scw.mvc.output.Output;

public abstract class AbstractChannelService implements ChannelService,
		AsyncListener {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected abstract HandlerChain getHandlerChain();
	
	protected abstract ExceptionHandlerChain getExceptionHandlerChain();

	protected abstract long getWarnExecuteMillisecond();
	
	protected abstract Output getOutput();

	public final void doHandler(Channel channel) {
		try {
			ContextManager.doHandler(channel, getHandlerChain());
		} catch (Throwable e) {
			doError(channel, e);
		} finally {
			try {
				long millisecond = System.currentTimeMillis()
						- channel.getCreateTime();
				if (millisecond > getWarnExecuteMillisecond()) {
					executeOvertime(channel, millisecond);
				} else {
					if (logger.isTraceEnabled()) {
						logger.trace("execute：{}, use time:{}ms",
								channel.toString(), millisecond);
					}
				}
			} finally {
				destroyChannel(channel);
			}
		}
	}
	
	public void onComplete(AsyncEvent event) throws IOException {
		XUtils.destroy(event.getAsyncControl().getChannel());
	}
	
	public void onError(AsyncEvent event) throws IOException {
	}
	
	public void onStartAsync(AsyncEvent event) throws IOException {
	}
	
	public void onTimeout(AsyncEvent event) throws IOException {
	}

	protected void destroyChannel(Channel channel) {
		if (channel.isCompleted()) {
			return;
		}

		if (channel.isSupportAsyncControl()
				&& channel.getAsyncControl().isStarted()) {
			channel.getAsyncControl().addListener(this);
			return;
		}

		XUtils.destroy(channel);
	}

	protected void doError(Channel channel, Throwable error) {
		logger.error(error, channel.toString());
		Object value = getExceptionHandlerChain().doHandler(channel, error);
		try {
			getOutput().write(channel, value);
		} catch (Throwable e) {
			logger.error(e, "output error for channel:{}", channel.toString());
		}
	}

	protected void executeOvertime(Channel channel, long millisecond) {
		if (logger.isWarnEnabled()) {
			logger.warn("execute：{}, use time:{}ms", channel.toString(),
					millisecond);
		}
	}
}
