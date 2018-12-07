package shuchaowen.db.storage.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import shuchaowen.common.Logger;
import shuchaowen.common.transaction.AbstractTransaction;
import shuchaowen.db.AbstractDB;
import shuchaowen.db.OperationBean;
import shuchaowen.db.TableInfo;
import shuchaowen.db.storage.CacheUtils;
import shuchaowen.memcached.CAS;
import shuchaowen.memcached.Memcached;

public class MemcachedFullCacheTransaction extends AbstractTransaction {
	private final OperationBean operationBean;
	private CAS<byte[]> updateOldBean;// 如果是update,这里保留了更新前的一份数据
	private final Memcached memcached;
	private final String[] indexKeys;
	private final String objectKey;
	private final byte[] beanData;

	public MemcachedFullCacheTransaction(Memcached memcached, OperationBean operationBean)
			throws IllegalArgumentException, IllegalAccessException {
		TableInfo tableInfo = AbstractDB.getTableInfo(operationBean.getBean().getClass());
		StringBuilder sb = new StringBuilder();
		this.indexKeys = new String[tableInfo.getPrimaryKeyColumns().length - 1];
		sb.append(tableInfo.getClassInfo().getName());
		Object v = tableInfo.getPrimaryKeyColumns()[0].getFieldInfo().forceGet(operationBean.getBean());
		sb.append(Cache.SPLIT);
		sb.append(v);

		for (int i = 1; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			if (i <= indexKeys.length) {
				indexKeys[i - 1] = sb.toString();
			}

			sb.append(Cache.SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(operationBean.getBean()));
		}
		this.objectKey = sb.toString();

		this.operationBean = operationBean;
		this.memcached = memcached;
		this.beanData = CacheUtils.encode(operationBean.getBean());
	}

	public void begin() throws Exception {
		switch (operationBean.getOperationType()) {
		case UPDATE:// 如果是update 应该把早的数据备份一下
		case SAVE_OR_UPDATE:
			updateOldBean = memcached.gets(objectKey);
			break;
		default:
			break;
		}
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
		Logger.debug("MemcachedFullCacheTransaction-rollback-" + operationBean.getOperationType().name(), sb.toString());
		
		switch (operationBean.getOperationType()) {
		case SAVE:
			delete();
			break;
		case UPDATE:
			memcached.cas(objectKey, updateOldBean.getValue(), updateOldBean.getCas() + 1);
			break;
		case DELETE:
			save();
			break;
		case SAVE_OR_UPDATE:
			if(updateOldBean == null || updateOldBean.getValue() == null){//原来的数据不存在说明原来进行的是保存操作
				deleteCas();
			}else{
				memcached.cas(objectKey, updateOldBean.getValue(), updateOldBean.getCas() + 1);
			}
			break;
		default:
			break;
		}
	}

	private void casSaveIndex(String indexKey, String objectKey) {
		boolean b = false;
		while (!b) {
			CAS<LinkedHashMap<String, Byte>> map = memcached.gets(indexKey);
			if (map == null || map.getValue() == null) {
				LinkedHashMap<String, Byte> valueMap = new LinkedHashMap<String, Byte>();
				valueMap.putIfAbsent(objectKey, (byte) 0);
				b = memcached.add(indexKey, valueMap);
			} else {
				map.getValue().putIfAbsent(objectKey, (byte) 0);
				b = memcached.cas(indexKey, map.getValue(), map.getCas());
			}
		}
	}

	private void casDeleteIndex(String indexKey, String objectKey) {
		boolean b = false;
		while (!b) {
			CAS<LinkedHashMap<String, byte[]>> map = memcached.get(indexKey);
			if (map != null && map.getValue() != null) {
				map.getValue().remove(objectKey);
			}
			b = memcached.cas(indexKey, map.getValue(), map.getCas());
		}
	}

	private void save() {
		if(memcached.add(objectKey, beanData)){
			for (String indexKey : indexKeys) {
				casSaveIndex(indexKey, objectKey);
			}
		}
	}

	private void update(){
		memcached.set(objectKey, beanData);
	}

	private void delete() {
		if(memcached.delete(objectKey)){
			for (String indexKey : indexKeys) {
				casDeleteIndex(indexKey, objectKey);
			}
		}
	}
	
	private void deleteCas(){
		if(memcached.delete(objectKey, 1, 1000L)){
			for (String indexKey : indexKeys) {
				casDeleteIndex(indexKey, objectKey);
			}
		}
	}

	private void saveOrUpdate() {
		if(memcached.set(objectKey, beanData)){
			for (String indexKey : indexKeys) {
				casSaveIndex(indexKey, objectKey);
			}
		}
	}
}
