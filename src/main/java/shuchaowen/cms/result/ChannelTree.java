package shuchaowen.cms.result;

import java.io.Serializable;
import java.util.List;

import shuchaowen.cms.pojo.Channel;

public class ChannelTree implements Serializable{
	private static final long serialVersionUID = 1L;
	private Channel channel;
	private List<ChannelTree> subList;
	public Channel getChannel() {
		return channel;
	}
	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	public List<ChannelTree> getSubList() {
		return subList;
	}
	public void setSubList(List<ChannelTree> subList) {
		this.subList = subList;
	}
	}
