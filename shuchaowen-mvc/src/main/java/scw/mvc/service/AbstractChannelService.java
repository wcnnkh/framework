package scw.mvc.service;

import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.Channel;
import scw.mvc.context.ContextManager;
import scw.mvc.handler.HandlerChain;

public abstract class AbstractChannelService implements ChannelService {
	protected final Logger logger = LoggerUtils.getLogger(getClass());
	
	public abstract HandlerChain getHandlerChain();
	
	public abstract long getWarnExecuteMillisecond();
	
	public void doHandler(Channel channel) {
		try {
			ContextManager.doHandler(channel, getHandlerChain());
		} catch (Throwable e) {
			doError(channel, e);
		}finally{
			long millisecond = channel.getCreateTime() - System.currentTimeMillis();
			if(millisecond > getWarnExecuteMillisecond()){
				executeOvertime(channel, millisecond);
			}else{
				if(logger.isTraceEnabled()){
					logger.trace("execute：{}, use time:{}ms", channel.toString(), millisecond);
				}
			}
		}
	}

	protected void doError(Channel channel, Throwable error){
		logger.error(error, channel.toString());
	}
	
	protected void executeOvertime(Channel channel, long millisecond){
		if(logger.isWarnEnabled()){
			logger.warn("execute：{}, use time:{}ms", channel.toString(), millisecond);
		}
	}
}
