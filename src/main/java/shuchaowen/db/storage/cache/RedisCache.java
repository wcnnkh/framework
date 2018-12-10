package shuchaowen.db.storage.cache;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import shuchaowen.common.transaction.Transaction;
import shuchaowen.db.AbstractDB;
import shuchaowen.db.OperationBean;
import shuchaowen.db.OperationType;
import shuchaowen.db.TableInfo;
import shuchaowen.db.storage.CacheUtils;
import shuchaowen.redis.Redis;

public class RedisCache implements Cache{
	private final Redis redis;
	private final Charset charset;
	
	public RedisCache(Redis redis, Charset charset){
		this.charset = charset;
		this.redis = redis;
	}
	
	public Redis getRedis() {
		return redis;
	}

	public Charset getCharset() {
		return charset;
	}

	public void loadFull(Object bean) throws Exception {
		RedisFullCacheTransaction cacheTransaction = new RedisFullCacheTransaction(redis, new OperationBean(OperationType.SAVE, bean), getCharset());
		cacheTransaction.execute();
	}

	public void loadKey(Object bean) throws Exception {
		String key = CacheUtils.getObjectKey(bean);
		redis.setnx((Cache.INDEX_PREFIX + key).getBytes(getCharset()), "".getBytes(getCharset()));
	}

	public <T> T getById(Class<T> type, Object... params) throws Exception {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<params.length; i++){
			if(i != 0){
				sb.append(SPLIT);
			}
			sb.append(params[i]);
		}
		
		byte[] data = redis.hget(type.getName().getBytes(getCharset()), sb.toString().getBytes(getCharset()));
		if(data == null){
			return null;
		}
		
		return CacheUtils.decode(type, data);
	}

	public <T> T getById(AbstractDB db, boolean checkKey, int exp, Class<T> type, Object... params) throws Exception {
		StringBuilder sb = new StringBuilder(128);
		sb.append(type.getName());
		for(Object v : params){
			sb.append(SPLIT);
			sb.append(v);
		}
		
		String keyStr = sb.toString();
		byte[] key = keyStr.getBytes(getCharset());
		byte[] data = redis.get(key);
		T t = null;
		if (data == null) {
			if (checkKey) {
				if(redis.exists((Cache.INDEX_PREFIX + keyStr).getBytes(getCharset()))){
					t = (T) db.getByIdFromDB(type, null, params);
					if (t != null) {
						opHotspot(new OperationBean(OperationType.SAVE, t), exp, true);
					}
				}
			} else {
				t = (T) db.getByIdFromDB(type, null, params);
				if (t != null) {
					opHotspot(new OperationBean(OperationType.SAVE, t), exp, false);
				}
			}
		} else {
			t = CacheUtils.decode(type, data);
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getByIdList(AbstractDB db, Class<T> type, Object... params) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(type);
		if (tableInfo.getPrimaryKeyColumns().length == params.length) {
			T t = getById(type, params);
			return t == null ? null : Arrays.asList(t);
		}

		if (tableInfo.getPrimaryKeyColumns().length == 1) {
			List<byte[]> dataList = redis.hvals(type.getName().getBytes(getCharset()));
			List<T> list = new ArrayList<T>();
			for(byte[] data : dataList){
				list.add(CacheUtils.decode(type, data));
			}
			return list;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(type.getName());
		for (Object v : params) {
			sb.append(SPLIT);
			sb.append(v);
		}
		
		Set<byte[]> dataSet = redis.smembers(sb.toString().getBytes(getCharset()));
		List<T> list = new ArrayList<T>();
		for(byte[] data : dataSet){
			list.add(CacheUtils.decode(type, data));
		}
		return list;
	}

	public Transaction opByFull(OperationBean operationBean) throws Exception {
		return new RedisFullCacheTransaction(redis, operationBean, getCharset());
	}

	public Transaction opHotspot(OperationBean operationBean, int exp, boolean keys) throws Exception {
		return new RedisHotspotCacheTransaction(redis, exp, keys, getCharset(), operationBean);
	}

	public void hostspotDataAsyncRollback(OperationBean operationBean, boolean keys, boolean exist) throws IllegalArgumentException, IllegalAccessException {
		String key = CacheUtils.getObjectKey(operationBean.getBean());
		redis.delete(key.getBytes(getCharset()));
		if(keys){
			byte[] indexKey = (Cache.INDEX_PREFIX + key).getBytes(getCharset());
			if(exist){
				redis.setnx(indexKey, "".getBytes(getCharset()));
			}else{
				redis.delete(indexKey);
			}
		}
	}
}
