package shuchaowen.cms.dao;

import java.util.List;

import shuchaowen.cms.pojo.Channel;
import shuchaowen.cms.result.ChannelTree;

public interface ChannelDao {
	Channel getChannel(long id);
	
	/**
	 * 获取子列表
	 * @param parentId
	 * @param isRecursion 是否递归向下查找
	 * @return
	 */
	List<Channel> getSubList(long parentId, int status, boolean isRecursion);
	
	List<ChannelTree> getChannelTree(long parentId, int status);
	
	Channel createChannel(long parentId, String name, int status, int weight);
}
