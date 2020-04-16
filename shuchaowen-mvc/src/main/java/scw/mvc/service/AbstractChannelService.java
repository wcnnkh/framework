package scw.mvc.service;

import java.io.IOException;

import scw.core.utils.XUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.AsyncEvent;
import scw.mvc.AsyncListener;
import scw.mvc.Channel;
import scw.mvc.exception.ExceptionHandlerChain;
import scw.mvc.output.Output;

public abstract class AbstractChannelService implements ChannelService,
		AsyncListener {
	protected final Logger logger = LoggerUtils.getLogger(getClass());

	protected abstract FilterChain getHandlerChain();
	
	protected abstract ExceptionHandlerChain getExceptionHandlerChain();

	protected abstract long getWarnExecuteMillisecond();
	
	protected abstract Output getOutput();

	public final void service(Channel channel) {
		try {
			output(channel, ContextManager.doFilter(channel, getHandlerChain()));
		} catch (Throwable e) {
			output(channel, doError(channel, e));
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
	
	public void output(Channel channel, Object body){
		try {
			getOutput().write(channel, body);
		} catch (Throwable e) {
			logger.error(e, "output error for channel:{}", channel.toString());
		}
	}
	
	public void onComplete(AsyncEvent event) throws IOException {
		try {
			XUtils.destroy(event.getAsyncControl().getChannel());
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		try {
			XUtils.destroy(channel);
		} catch (Exception e) {
			logger.error(e, "destroy error for channel:{}", channel.toString());
		}
	}

	protected Object doError(Channel channel, Throwable error) {
		logger.error(error, channel.toString());
		return getExceptionHandlerChain().doHandler(channel, error);
	}

	protected void executeOvertime(Channel channel, long millisecond) {
		if (logger.isWarnEnabled()) {
			logger.warn("execute：{}, use time:{}ms", channel.toString(),
					millisecond);
		}
	}
}
