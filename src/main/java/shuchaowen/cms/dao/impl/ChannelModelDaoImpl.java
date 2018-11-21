package shuchaowen.cms.dao.impl;

import shuchaowen.cms.dao.ChannelModelDao;
import shuchaowen.cms.pojo.ChannelModel;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.db.DBManager;
import shuchaowen.core.db.id.TableLongIdGenerator;
import shuchaowen.core.util.id.IdGenerator;

@Service
public class ChannelModelDaoImpl implements ChannelModelDao{
	private IdGenerator<Long> idGenerator;
	
	@InitMethod
	private void init(){
		idGenerator = new TableLongIdGenerator(ChannelModel.class, "id");
	}
	
	public ChannelModel get(long id) {
		return DBManager.getById(ChannelModel.class, id);
	}

	public ChannelModel create(String name, int weight) {
		ChannelModel channelModel = new ChannelModel();
		channelModel.setId(idGenerator.next());
		channelModel.setName(name);
		channelModel.setWeight(weight);
		DBManager.save(channelModel);
		return channelModel;
	}

}
