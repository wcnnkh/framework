package shuchaowen.cms.dao;

import java.util.List;

import shuchaowen.cms.pojo.ChannelModelAttr;

public interface ChannelModelAttrDao {
	ChannelModelAttr get(long modelId, String attr);
	
	List<ChannelModelAttr> getChannelModeAttrList(long modelId);
	
	ChannelModelAttr saveOrUpdate(long modelId, String attr, String attrName, String defaultValue, int attrType, int weight);
}
