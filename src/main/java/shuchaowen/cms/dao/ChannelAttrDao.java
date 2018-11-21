package shuchaowen.cms.dao;

import java.util.List;

import shuchaowen.cms.pojo.ChannelAttr;

public interface ChannelAttrDao {
	ChannelAttr getChannelAttr(long id, String attr);
	
	List<ChannelAttr> getChannelAttrList(long id);
	
	ChannelAttr saveOrUpdate(long id, String attr, String value, long modelId);
}
