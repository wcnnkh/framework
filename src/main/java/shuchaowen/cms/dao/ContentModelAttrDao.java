package shuchaowen.cms.dao;

import java.util.List;

import shuchaowen.cms.pojo.ContentModelAttr;

public interface ContentModelAttrDao {
	ContentModelAttr getContentModelAttr(long modelId, String attr);
	
	List<ContentModelAttr> getContentModelAttrList(long modelId);
	
	ContentModelAttr saveOrUpdate(long modelId, String attr, String attrName, int weight);
}
