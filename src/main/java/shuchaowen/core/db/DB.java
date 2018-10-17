package shuchaowen.core.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.core.db.sql.format.SQLFormat;
import shuchaowen.core.db.storage.CommonStorage;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.util.ClassUtils;

public abstract class DB extends AbstractDB {
	private Map<String, Storage> storageMap = new HashMap<String, Storage>();
	private Storage storage;
	
	public DB(){
		super(null);
		this.storage = new CommonStorage(this, null, null);
	}
	
	public DB(Storage storage, SQLFormat sqlFormat){
		super(sqlFormat);
		this.storage = storage;
	}
	
	protected void registerStorage(Class<?> tableClass, Storage storage){
		synchronized (storageMap) {
			storageMap.put(ClassUtils.getCGLIBRealClassName(tableClass), storage);
		}
	}
	
	protected void removeStorage(Class<?> ...tableClass){
		synchronized (storageMap) {
			for(Class<?> clz : tableClass){
				storageMap.remove(ClassUtils.getCGLIBRealClassName(clz));
			}
		}
	}
	
	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	
	public Storage getStorage(Class<?> tableClass){
		Storage storage = storageMap.get(ClassUtils.getCGLIBRealClassName(tableClass));
		return storage == null? this.storage:storage;
	}
	
	private Map<Storage, List<Object>> getStorageBeanMap(Collection<?> beanList){
		Map<Storage, List<Object>> map = new HashMap<Storage, List<Object>>();
		for(Object bean : beanList){
			Storage storage = getStorage(bean.getClass());
			List<Object> list = map.get(storage);
			if(list == null){
				list = new ArrayList<Object>();
				list.add(bean);
				map.put(storage, list);
			}else{
				list.add(bean);
			}
		}
		return map;
	}

	//storage
	public <T> T getById(Class<T> type, Object... params) {
		return getStorage(type).getById(type, params);
	}
	
	public <T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters){
		return getStorage(type).getById(type, primaryKeyParameters);
	}
	
	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return getStorage(type).getByIdList(type, params);
	}

	/** 保存  **/
	public void save(Object... beans) {
		save(Arrays.asList(beans));
	}
	
	public void save(Collection<?> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		if(storageMap.isEmpty()){
			storage.save(beans);
		}else if(beans.size() == 1){
			for(Object bean : beans){
				getStorage(bean.getClass()).save(beans);
				break;
			}
		}else{
			Map<Storage, List<Object>> map = getStorageBeanMap(beans);
			for(Entry<Storage, List<Object>> entry : map.entrySet()){
				entry.getKey().save(entry.getValue());
			}
		}
	}

	/**删除**/
	public void delete(Object... beans) {
		delete(Arrays.asList(beans));
	}
	
	public void delete(Collection<?> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		if(storageMap.isEmpty()){
			storage.delete(beans);
		}else if(beans.size() == 1){
			for(Object bean : beans){
				getStorage(bean.getClass()).delete(beans);
				break;
			}
		}else{
			Map<Storage, List<Object>> map = getStorageBeanMap(beans);
			for(Entry<Storage, List<Object>> entry : map.entrySet()){
				entry.getKey().delete(entry.getValue());
			}
		}
	}
	
	/**更新**/
	public void update(Object... beans) {
		update(Arrays.asList(beans));
	}
	
	public void update(Collection<?> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		if(storageMap.isEmpty()){
			storage.update(beans);
		}else if(beans.size() == 1){
			for(Object bean : beans){
				getStorage(bean.getClass()).update(beans);
				break;
			}
		}else{
			Map<Storage, List<Object>> map = getStorageBeanMap(beans);
			for(Entry<Storage, List<Object>> entry : map.entrySet()){
				entry.getKey().update(entry.getValue());
			}
		}
	}
	
	/**保存或更新**/
	public void saveOrUpdate(Object ...beans){
		saveOrUpdate(Arrays.asList(beans));
	}
	
	public void saveOrUpdate(Collection<?> beans){
		if(beans == null || beans.isEmpty()){
			return ;
		}
		
		if(storageMap.isEmpty()){
			storage.saveOrUpdate(beans);
		}else if(beans.size() == 1){
			for(Object bean : beans){
				getStorage(bean.getClass()).saveOrUpdate(beans);
				break;
			}
		}else{
			Map<Storage, List<Object>> map = getStorageBeanMap(beans);
			for(Entry<Storage, List<Object>> entry : map.entrySet()){
				entry.getKey().saveOrUpdate(entry.getValue());
			}
		}
	}
}