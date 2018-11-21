package shuchaowen.cms.dao;

import java.util.List;

import shuchaowen.cms.pojo.ContentKey;

public interface ContentKeyDao {
	List<ContentKey> getContentKeyList(long id);
	
	ContentKey saveOrUpdate(long contentId, String key);
}
