package shuchaowen.cms.dao.impl;

import shuchaowen.cms.dao.ContentModelDao;
import shuchaowen.cms.pojo.ContentModel;
import shuchaowen.core.beans.annotaion.InitMethod;
import shuchaowen.core.beans.annotaion.Service;
import shuchaowen.core.db.DBManager;
import shuchaowen.core.db.id.TableLongIdGenerator;
import shuchaowen.core.util.id.IdGenerator;

@Service
public class ContentModelDaoImpl implements ContentModelDao{
	private IdGenerator<Long> idGenerator;
	
	@InitMethod
	private void init(){
		idGenerator = new TableLongIdGenerator(ContentModel.class, "id");
	}
	
	public ContentModel getContentModel(long modelId) {
		return DBManager.getById(ContentModel.class, modelId);
	}

	public ContentModel create(String name, int weight) {
		ContentModel contentModel = new ContentModel();
		contentModel.setId(idGenerator.next());
		contentModel.setName(name);
		contentModel.setWeight(weight);
		DBManager.save(contentModel);
		return contentModel;
	}

	public void update(long modelId, String name, int weight) {
		ContentModel contentModel = getContentModel(modelId);
		if(contentModel != null){
			contentModel.setName(name);
			contentModel.setWeight(weight);
			DBManager.update(contentModel);
		}
	}

}
