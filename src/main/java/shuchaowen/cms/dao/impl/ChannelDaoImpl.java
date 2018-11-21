package shuchaowen.cms.dao.impl;

import java.util.ArrayList;
import java.util.List;

import shuchaowen.cms.dao.ChannelDao;
import shuchaowen.cms.pojo.Channel;
import shuchaowen.cms.result.ChannelTree;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.db.DBManager;
import shuchaowen.core.db.id.TableLongIdGenerator;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.sql.SimpleSQL;
import shuchaowen.core.util.id.IdGenerator;

@Service
public class ChannelDaoImpl implements ChannelDao{
	private IdGenerator<Long> idGenerator;
	
	@InitMethod
	private void init(){
		idGenerator = new TableLongIdGenerator(Channel.class, "id");
	}
	
	public Channel getChannel(long id) {
		return DBManager.getById(Channel.class, id);
	}
	
	public List<Channel> getSubList(long parentId, int status){
		SQL sql = new SimpleSQL("select * from channel where parentId=? and status=? order by weight desc", parentId, status);
		return DBManager.select(Channel.class, sql);
	}
	
	public void appendChannleList(List<Channel> channelList, long parentId, int status){
		List<Channel> cs = getSubList(parentId, status);
		if(cs != null && !cs.isEmpty()){
			channelList.addAll(cs);
			for(Channel channel : cs){
				appendChannleList(channelList, channel.getId(), status);
			}
		}
	}
	
	public List<Channel> getSubList(long parentId, int status, boolean isRecursion) {
		if(isRecursion){
			List<Channel> channelList = new ArrayList<Channel>();
			appendChannleList(channelList, parentId, status);
			return channelList;
		}else{
			return getSubList(parentId, status);
		}
	}

	public List<ChannelTree> getChannelTree(long parentId, int status) {
		List<ChannelTree> trees = new ArrayList<ChannelTree>();
		List<Channel> channels = getSubList(parentId, status);
		if(channels != null && !channels.isEmpty()){
			for(Channel channel : channels){
				ChannelTree channelTree = new ChannelTree();
				channelTree.setChannel(channel);
				channelTree.setSubList(getChannelTree(channel.getId(), status));
				trees.add(channelTree);
			}
		}
		return trees;
	}

	public Channel createChannel(long parentId, String name, int status, int weight) {
		Channel channel = new Channel();
		channel.setId(idGenerator.next());
		channel.setName(name);
		channel.setParentId(parentId);
		channel.setStatus(status);
		channel.setWeight(weight);
		channel.setCts(System.currentTimeMillis());
		DBManager.save(channel);
		return channel;
	}
}
