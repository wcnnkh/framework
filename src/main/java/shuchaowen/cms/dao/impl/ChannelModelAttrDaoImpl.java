package shuchaowen.cms.dao.impl;

import java.util.List;

import shuchaowen.cms.dao.ChannelModelAttrDao;
import shuchaowen.cms.pojo.ChannelModelAttr;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.db.DBManager;

@Service
public class ChannelModelAttrDaoImpl implements ChannelModelAttrDao{

	public ChannelModelAttr get(long modelId, String attr) {
		return DBManager.getById(ChannelModelAttr.class, modelId, attr);
	}

	public List<ChannelModelAttr> getChannelModeAttrList(long modelId) {
		return DBManager.getByIdList(ChannelModelAttr.class, modelId);
	}

	public ChannelModelAttr saveOrUpdate(long modelId, String attr, String attrName, String defaultValue, int attrType, int weight) {
		ChannelModelAttr channelModelAttr = new ChannelModelAttr();
		channelModelAttr.setModelId(modelId);
		channelModelAttr.setAttr(attr);
		channelModelAttr.setAttrName(attrName);
		channelModelAttr.setWeight(weight);
		channelModelAttr.setDefaultValue(defaultValue);
		channelModelAttr.setAttrType(attrType);
		DBManager.saveOrUpdate(channelModelAttr);
		return channelModelAttr;
	}

}
