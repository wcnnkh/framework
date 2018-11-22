package shuchaowen.cms.dao.impl;

import java.util.List;

import shuchaowen.cms.dao.ContentModelAttrDao;
import shuchaowen.cms.pojo.ContentModelAttr;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.db.DBManager;

@Service
public class ContentModelAttrDaoImpl implements ContentModelAttrDao{

	public ContentModelAttr getContentModelAttr(long modelId, String attr) {
		return DBManager.getById(ContentModelAttr.class, modelId, attr);
	}

	public List<ContentModelAttr> getContentModelAttrList(long modelId) {
		return DBManager.getByIdList(ContentModelAttr.class, modelId);
	}

	public ContentModelAttr saveOrUpdate(long modelId, String attr, String attrName, String defaultValue, int attrType, int weight) {
		ContentModelAttr contentModelAttr = new ContentModelAttr();
		contentModelAttr.setModelId(modelId);
		contentModelAttr.setAttr(attr);
		contentModelAttr.setAttrName(attrName);
		contentModelAttr.setWeight(weight);
		contentModelAttr.setDefaultValue(defaultValue);
		contentModelAttr.setAttrType(attrType);
		DBManager.saveOrUpdate(contentModelAttr);
		return contentModelAttr;
	}
}
