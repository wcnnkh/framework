package shuchaowen.cms.dao;

import shuchaowen.cms.pojo.ContentModel;

public interface ContentModelDao {
	ContentModel getContentModel(long modelId);
	
	ContentModel create(String name, int weight);
	
	void update(long modelId, String name, int weight);
}
