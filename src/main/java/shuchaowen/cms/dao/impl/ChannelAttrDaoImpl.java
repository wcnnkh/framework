package shuchaowen.cms.dao.impl;

import java.util.List;

import shuchaowen.cms.dao.ChannelAttrDao;
import shuchaowen.cms.pojo.ChannelAttr;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.db.DBManager;

@Service
public class ChannelAttrDaoImpl implements ChannelAttrDao{

	public ChannelAttr getChannelAttr(long id, String attr) {
		return DBManager.getById(ChannelAttr.class, id, attr);
	}

	public List<ChannelAttr> getChannelAttrList(long id) {
		return DBManager.getByIdList(ChannelAttr.class, id);
	}

	public ChannelAttr saveOrUpdate(long id, String attr, String value, long modelId) {
		ChannelAttr channelAttr = new ChannelAttr();
		channelAttr.setChannelId(id);
		channelAttr.setAttr(attr);
		channelAttr.setValue(value);
		channelAttr.setModelId(modelId);
		DBManager.save(channelAttr);
		return channelAttr;
	}
}
