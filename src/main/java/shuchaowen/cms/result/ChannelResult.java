package shuchaowen.cms.result;

import java.io.Serializable;
import java.util.List;

import shuchaowen.cms.pojo.Channel;
import shuchaowen.cms.pojo.ChannelAttr;

public class ChannelResult implements Serializable{
	private static final long serialVersionUID = 1L;
	private Channel channel;
	private List<ChannelAttr> channelAttrs;
	
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public List<ChannelAttr> getChannelAttrs() {
		return channelAttrs;
	}
	public void setChannelAttrs(List<ChannelAttr> channelAttrs) {
		this.channelAttrs = channelAttrs;
	}
}
