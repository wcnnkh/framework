package scw.mvc.service;

import scw.core.utils.XUtils;
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
			try {
				long millisecond = System.currentTimeMillis() - channel.getCreateTime();
				if(millisecond > getWarnExecuteMillisecond()){
					executeOvertime(channel, millisecond);
				}else{
					if(logger.isTraceEnabled()){
						logger.trace("execute：{}, use time:{}ms", channel.toString(), millisecond);
					}
				}
			} finally{
				destroyChannel(channel);
			}
		}
	}
	
	protected void destroyChannel(Channel channel){
		XUtils.destroy(channel);
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
