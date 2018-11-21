package shuchaowen.cms.dao;

import java.util.List;

import shuchaowen.cms.pojo.Content;
import shuchaowen.cms.result.ContentTree;

public interface ContentDao {
	Content get(long id);
	
	List<Content> getContentList(long channelId, int channelStatus, int contentStatus, boolean isRecursion);
	
	Content create(long channelId, String title, String desc, int status, int weight);
	
	List<ContentTree> getContentTreeList(long channelId, int channelStatus, int contentStatus);
}
