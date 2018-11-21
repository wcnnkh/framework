package shuchaowen.cms.dao;

import java.util.List;

import shuchaowen.cms.pojo.ContentAttr;

public interface ContentAttrDao {
	ContentAttr getContentAttr(long id, String attr);
	
	List<ContentAttr> getContentAttrList(long id);
	
	ContentAttr saveOrUpdate(long id, String attr, String value, long modelId);
}
