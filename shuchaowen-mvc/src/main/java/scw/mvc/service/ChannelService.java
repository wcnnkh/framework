package scw.mvc.service;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;

@AutoImpl(ConfigurationChannelService.class)
public interface ChannelService{
	void service(Channel channel);
}
