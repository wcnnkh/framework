package shuchaowen.cms.dao;

import shuchaowen.cms.pojo.ChannelModel;

public interface ChannelModelDao {
	ChannelModel get(long id);
	
	ChannelModel create(String name, int weight);
}
