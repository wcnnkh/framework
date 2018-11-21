package shuchaowen.cms.dao.impl;

import java.util.ArrayList;
import java.util.List;

import shuchaowen.cms.dao.ChannelDao;
import shuchaowen.cms.dao.ContentDao;
import shuchaowen.cms.pojo.Channel;
import shuchaowen.cms.pojo.Content;
import shuchaowen.cms.result.ContentTree;
import shuchaowen.core.beans.annotaion.Autowrite;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.db.DBManager;
import shuchaowen.core.db.id.TableLongIdGenerator;
import shuchaowen.core.db.sql.SimpleSQL;
import shuchaowen.core.util.id.IdGenerator;

@Service
public class ContentDaoImpl implements ContentDao{
	private IdGenerator<Long> idGenerator;
	@Autowrite
	private ChannelDao channelDao;
	
	@InitMethod
	private void init(){
		idGenerator = new TableLongIdGenerator(Content.class, "id");
	}
	
	public Content get(long id) {
		return DBManager.getById(Content.class, id);
	}

	public List<Content> getContentList(long channelId, int channelStatus, int contentStatus, boolean isRecursion) {
		StringBuilder sb = new StringBuilder();
		sb.append(channelId);
		if(isRecursion){
			List<Channel> channels = channelDao.getSubList(channelId, channelStatus, true);
			if(channels != null){
				for(Channel channel : channels){
					if(channel != null){
						sb.append(",").append(channel.getId());
					}
				}
			}
		}
		
		StringBuilder sql = new StringBuilder();
		sql.append("select * from content where channelId in (");
		sql.append(sb);
		sql.append(") where status=? order by weight desc");
		
		return DBManager.select(Content.class, new SimpleSQL(sql.toString(), contentStatus));
	}

	public Content create(long channelId, String title, String desc, int status, int weight) {
		Content content = new Content();
		content.setId(idGenerator.next());
		content.setChannelId(channelId);
		content.setTitle(title);
		content.setDesc(desc);
		content.setWeight(weight);
		content.setStatus(status);
		content.setCts(System.currentTimeMillis());
		DBManager.save(content);
		return content;
	}

	public List<ContentTree> getContentTreeList(long channelId, int channelStatus, int contentStatus) {
		List<ContentTree> trees = new ArrayList<ContentTree>();
		List<Content> contents = getContentList(channelId, channelStatus, contentStatus, false);
		if(contents != null){
			for(Content content : contents){
				ContentTree contentTree = new ContentTree();
				contentTree.setContent(content);
				contentTree.setSubList(getContentTreeList(channelId, channelStatus, contentStatus));
				trees.add(contentTree);
			}
		}
		return trees;
	}
}
