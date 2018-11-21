package shuchaowen.cms.dao.impl;

import java.util.List;

import shuchaowen.cms.dao.ContentKeyDao;
import shuchaowen.cms.pojo.ContentKey;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.db.DBManager;

@Service
public class ContentKeyDaoImpl implements ContentKeyDao{

	public List<ContentKey> getContentKeyList(long id) {
		return DBManager.getByIdList(ContentKey.class, id);
	}

	public ContentKey saveOrUpdate(long contentId, String key) {
		ContentKey contentKey = new ContentKey();
		contentKey.setId(contentId);
		contentKey.setKey(key);
		DBManager.saveOrUpdate(contentKey);
		return contentKey;
	}
}
