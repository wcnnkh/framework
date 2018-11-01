package shuchaowen.core.db.storage.cache;

import java.nio.charset.Charset;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.db.transaction.AbstractTransaction;
import shuchaowen.redis.Redis;

public class RedisHotspotCacheTransaction extends AbstractTransaction{
	private static final String EX = "EX";
	private static final String NX = "NX";
	private static final String XX = "XX";
	
	private final Redis redis;
	private final int exp;
	private final boolean keys;
	private final OperationBean operationBean;
	private final byte[] objectKey;
	private final byte[] beanData;
	private final Charset charset;
	private byte[] indexObjectKey;
	private boolean keyExist;
	private final byte[] null_data;
	
	public RedisHotspotCacheTransaction(Redis redis, int exp, boolean keys, Charset charset, OperationBean operationBean)
			throws IllegalArgumentException, IllegalAccessException {
		this.null_data = "".getBytes(charset);
		this.redis = redis;
		this.exp = exp;
		this.keys = keys;
		this.operationBean = operationBean;
		this.charset = charset;
		this.beanData = CacheUtils.encode(operationBean.getBean());
		TableInfo tableInfo = AbstractDB.getTableInfo(operationBean.getBean().getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
			sb.append(Cache.SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(operationBean.getBean()));
		}
		
		String k = sb.toString();
		this.objectKey = k.getBytes(charset);
		
		if(keys){
			this.indexObjectKey = (Cache.INDEX_PREFIX + k).getBytes(charset);
			this.keyExist = redis.exists(indexObjectKey);
		}
	}

	private void save() {
		redis.set(objectKey, beanData, NX.getBytes(charset), EX.getBytes(charset), exp);
		if(keys){
			redis.setnx(indexObjectKey, null_data);
		}
	}

	private void update() {
		redis.set(objectKey, beanData, XX.getBytes(charset), EX.getBytes(charset), exp);
	}

	private void delete() {
		redis.delete(objectKey);
		if(keys){
			redis.delete(indexObjectKey);
		}
	}

	private void saveOrUpdate() {
		redis.setex(objectKey, exp, beanData);
		if(keys){
			redis.setnx(indexObjectKey, null_data);
		}
	}

	public void begin() throws Exception {
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
		redis.delete(objectKey);
		switch (operationBean.getOperationType()) {
		case SAVE:
			if(keys){
				redis.delete(indexObjectKey);
			}
			break;
		case SAVE_OR_UPDATE:
			if(keys && !keyExist){
				redis.delete(indexObjectKey);
			}
			break;
		default:
			break;
		}
	}
}
