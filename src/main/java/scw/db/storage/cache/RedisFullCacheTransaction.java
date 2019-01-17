package scw.db.storage.cache;

import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.Logger;
import scw.common.transaction.AbstractTransaction;
import scw.database.DataBaseUtils;
import scw.database.TableInfo;
import scw.db.OperationBean;
import scw.db.storage.CacheUtils;
import scw.utils.redis.Redis;

public class RedisFullCacheTransaction extends AbstractTransaction{
	private final OperationBean operationBean;
	private final Redis redis;
	private final byte[] objectKey;
	private final byte[][] indexKeys;
	private final byte[] classNameKey;
	private byte[] updateOldBeanData;
	private final byte[] beanData;

	public RedisFullCacheTransaction(Redis redis, OperationBean operationBean, Charset keyCharset)
			throws IllegalArgumentException, IllegalAccessException {
		this.operationBean = operationBean;
		this.redis = redis;
		TableInfo tableInfo = DataBaseUtils.getTableInfo(operationBean.getBean().getClass());
		this.indexKeys = new byte[tableInfo.getPrimaryKeyColumns().length - 1][];
		this.classNameKey = tableInfo.getClassInfo().getName().getBytes(keyCharset);
		this.beanData = CacheUtils.encode(operationBean.getBean());
		
		StringBuilder indexKey = new StringBuilder();
		indexKey.append(tableInfo.getClassInfo().getName());
		indexKey.append(Cache.SPLIT);

		Object v = tableInfo.getPrimaryKeyColumns()[0].getFieldInfo().forceGet(operationBean.getBean());
		indexKey.append(v);

		StringBuilder sb = new StringBuilder();
		sb.append(v);

		for (int i = 1; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			v = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(operationBean.getBean());
			if (i <= indexKeys.length) {
				indexKeys[i - 1] = indexKeys.toString().getBytes(keyCharset);

				indexKey.append(Cache.SPLIT);
				indexKey.append(v);
			}
			sb.append(Cache.SPLIT);
			sb.append(v);
		}
		this.objectKey = sb.toString().getBytes(keyCharset);
	}

	private void save() {
		redis.hsetnx(classNameKey, objectKey, beanData);
		for (byte[] indexKey : indexKeys) {
			redis.sadd(indexKey, objectKey);
		}
	}

	private void update() {
		redis.hset(classNameKey, objectKey, beanData);
	}

	private void delete() {
		redis.hdel(classNameKey, objectKey);
		for (byte[] indexKey : indexKeys) {
			redis.srem(indexKey, objectKey);
		}
	}

	private void saveOrUpdate() {
		redis.hset(classNameKey, objectKey, beanData);
		for (byte[] indexKey : indexKeys) {
			redis.sadd(indexKey, objectKey);
		}
	}

	public void begin() throws Exception {
		switch (operationBean.getOperationType()) {
		case UPDATE:
		case SAVE_OR_UPDATE:
			updateOldBeanData = redis.hget(classNameKey, objectKey);
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
		Logger.debug("RedisFullCacheTransaction-rollback-" + operationBean.getOperationType().name(), sb.toString());
		
		switch (operationBean.getOperationType()) {
		case SAVE:
			delete();
			break;
		case UPDATE:
			redis.hset(classNameKey, objectKey, updateOldBeanData);
			break;
		case DELETE:
			save();
			break;
		case SAVE_OR_UPDATE:
			if(updateOldBeanData == null){//说明原来是保存
				delete();
			}else{
				redis.hset(classNameKey, objectKey, updateOldBeanData);
			}
			break;
		default:
			break;
		}
	}
}
