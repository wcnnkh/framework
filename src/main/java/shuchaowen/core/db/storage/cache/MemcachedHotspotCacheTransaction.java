package shuchaowen.core.db.storage.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.common.utils.Logger;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.transaction.AbstractTransaction;
import shuchaowen.memcached.Memcached;

public class MemcachedHotspotCacheTransaction extends AbstractTransaction{
	private final Memcached memcached;
	private final int exp;
	private final boolean keys;
	private final OperationBean operationBean;
	private final String objectKey;
	private final byte[] beanData;
	private boolean exist;//原来是否存在

	public MemcachedHotspotCacheTransaction(Memcached memcached, int exp, boolean keys, OperationBean operationBean)
			throws IllegalArgumentException, IllegalAccessException {
		this.memcached = memcached;
		this.exp = exp;
		this.keys = keys;
		this.operationBean = operationBean;
		this.beanData = CacheUtils.encode(operationBean.getBean());
		TableInfo tableInfo = AbstractDB.getTableInfo(operationBean.getBean().getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			Object v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(operationBean.getBean());
			sb.append(Cache.SPLIT);
			sb.append(v);
		}
		this.objectKey = sb.toString();
		
		if(keys){
			this.exist = memcached.get(Cache.INDEX_PREFIX + objectKey) != null;
		}
	}
	
	public void loadKeys(){
		memcached.add(Cache.INDEX_PREFIX + objectKey, "");
	}

	private void save() {
		if(memcached.add(objectKey, exp, beanData)){
			memcached.add(Cache.INDEX_PREFIX + objectKey, "");
		}
	}

	private void update() {
		memcached.set(objectKey, exp, beanData);
	}

	private void delete() {
		if(keys){
			memcached.delete(Cache.INDEX_PREFIX + objectKey);
		}
		memcached.delete(objectKey);
	}

	private void saveOrUpdate() {
		if(keys){
			memcached.add(Cache.INDEX_PREFIX + objectKey, "");
		}
		memcached.set(objectKey, exp, beanData);
	}

	public void begin() throws Exception {
		// TODO Auto-generated method stub
	}

	public void process() throws Exception {
		switch (operationBean.getOperationType()) {
		case SAVE:
			save();
			break;
		case UPDATE:
			update();
			break;
		case DELETE:
			delete();
			break;
		case SAVE_OR_UPDATE:
			saveOrUpdate();
			break;
		default:
			break;
		}
	}

	public void end() throws Exception {
		// TODO Auto-generated method stub
	}

	public void rollback() throws Exception {
		Map<String, Object> map = CacheUtils.getObjectProperties(operationBean.getBean());
		StringBuilder sb = new StringBuilder();
		sb.append(operationBean.getBean().getClass().getName());
		sb.append("{");
		if (map != null) {
			Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				sb.append(entry.getKey());
				sb.append("=");
				sb.append(entry.getValue());

				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append("}");
		}
		Logger.debug("MemcachedHotspotCacheTransaction-rollback-" + operationBean.getOperationType().name(), sb.toString());
		
		memcached.delete(objectKey);
		if(keys){
			switch (operationBean.getOperationType()) {
			case SAVE:
					memcached.delete(Cache.INDEX_PREFIX + objectKey);
				break;
			case DELETE:
					memcached.add(Cache.INDEX_PREFIX + objectKey, "");
				break;
			case SAVE_OR_UPDATE:
				if(!exist){
					memcached.delete(Cache.INDEX_PREFIX + objectKey);
				}
				break;
			default:
				break;
			}
		}
	}
}
