package shuchaowen.cms.dao.impl;

import java.util.List;

import shuchaowen.cms.dao.ContentAttrDao;
import shuchaowen.cms.pojo.ContentAttr;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.db.DBManager;

@Service
public class ContentAttrDaoImpl implements ContentAttrDao{

	public ContentAttr getContentAttr(long id, String attr) {
		return DBManager.getById(ContentAttr.class, id, attr);
	}

	public List<ContentAttr> getContentAttrList(long id) {
		return DBManager.getByIdList(ContentAttr.class, id);
	}

	public ContentAttr saveOrUpdate(long id, String attr, String value, long modelId) {
		ContentAttr contentAttr = new ContentAttr();
		contentAttr.setId(id);
		contentAttr.setAttr(attr);
		contentAttr.setValue(value);
		contentAttr.setModelId(modelId);
		DBManager.saveOrUpdate(contentAttr);
		return contentAttr;
	}
}
