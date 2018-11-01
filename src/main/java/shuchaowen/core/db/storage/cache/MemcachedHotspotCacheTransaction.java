package shuchaowen.core.db.storage.cache;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.db.transaction.AbstractTransaction;
import shuchaowen.memcached.Memcached;

public class MemcachedHotspotCacheTransaction extends AbstractTransaction{
	private final Memcached memcached;
	private final int exp;
	private final boolean keys;
	private final OperationBean operationBean;
	private final String objectKey;
	private final byte[] beanData;

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
		switch (operationBean.getOperationType()) {
		case SAVE:
			delete();
			break;
		case SAVE_OR_UPDATE:
			memcached.delete(Cache.INDEX_PREFIX + objectKey, 1, 1000L);
			break;
		default:
			memcached.delete(objectKey);
			break;
		}
	}
}
