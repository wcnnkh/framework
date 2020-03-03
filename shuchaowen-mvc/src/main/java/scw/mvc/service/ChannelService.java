package scw.mvc.service;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;
import scw.mvc.handler.HandlerChain;

@AutoImpl(ConfigurationChannelService.class)
public interface ChannelService extends HandlerChain{
	public void doHandler(Channel channel);
}
