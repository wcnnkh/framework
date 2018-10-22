package shuchaowen.core.db.storage.cache;

import java.util.Collection;
import java.util.List;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;

public interface Cache {
	void saveBeanAndIndex(Object bean) throws Exception;
	
	void updateBeanAndIndex(Object bean) throws Exception;
	
	void deleteBeanAndIndex(Object bean) throws Exception;
	
	void saveOrUpdateBeanAndIndex(Object bean) throws Exception;
	
	void saveBean(Object bean, int exp) throws Exception;
	
	void updateBean(Object bean, int exp) throws Exception;
	
	void deleteBean(Object bean) throws Exception;
	
	void saveOrUpdateBean(Object bean, int exp) throws Exception;
	
	void saveBeanAndKey(Object bean, int exp) throws Exception ;
	
	void updateBeanAndKey(Object bean, int exp) throws Exception;
	
	void deleteBeanAndKey(Object bean) throws Exception;
	
	void saveOrUpdateBeanAndKey(Object bean, int exp) throws Exception;
	
	void loadBeanAndIndex(Object bean) throws Exception;
	
	void loadBeanAndKey(Object bean) throws Exception;
	
	<T> T getById(Class<T> type, Object ...params) throws Exception;
	
	<T> T getById(AbstractDB db, boolean checkKey, int exp, Class<T> type, Object ...params) throws Exception;

	<T> List<T> getByIdList(AbstractDB db, Class<T> type, Object ...params) throws Exception;
	
	<T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters) throws Exception;
	
	<T> PrimaryKeyValue<T> getById(AbstractDB db, boolean checkKey, Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters) throws Exception;
}
 