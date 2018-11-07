package shuchaowen.core.db.storage.cache;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.OperationType;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.core.transaction.Transaction;
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

	public <T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters)
			throws Exception {
		byte[][] searchKeys = new byte[primaryKeyParameters.size()][];
		int index=0;
		Map<byte[], PrimaryKeyParameter> keyMap = new HashMap<byte[], PrimaryKeyParameter>();
		for (PrimaryKeyParameter parameter : primaryKeyParameters) {
			StringBuilder sb = new StringBuilder(128);
			for(int i=0; i<parameter.getParams().length; i++){
				if(i != 0){
					sb.append(SPLIT);
				}
				sb.append(parameter.getParams()[i]);
			}
			
			searchKeys[index] = sb.toString().getBytes(getCharset());
			keyMap.put(searchKeys[index], parameter);
			index++;
		}

		List<byte[]> dataList = redis.hmget(type.getName().getBytes(getCharset()), searchKeys);
		PrimaryKeyValue<T> value = new PrimaryKeyValue<T>();
		for(int i=0; i<searchKeys.length; i++){
			value.put(keyMap.get(searchKeys[i]), CacheUtils.decode(type, dataList.get(i)));
		}
		return value;
	}

	public <T> PrimaryKeyValue<T> getById(AbstractDB db, boolean checkKey, Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) throws Exception {
		Map<byte[], PrimaryKeyParameter> keyMap = new HashMap<byte[], PrimaryKeyParameter>();
		Map<byte[], byte[]> objToExistKeyMap = new HashMap<byte[], byte[]>();
		for (PrimaryKeyParameter parameter : primaryKeyParameters) {
			StringBuilder sb = new StringBuilder(256);
			sb.append(type.getName());
			for(Object v :  parameter.getParams()){
				sb.append(SPLIT);
				sb.append(v);
			}
			
			String keyStr = sb.toString();
			byte[] objectKey = keyStr.getBytes(getCharset());
			objToExistKeyMap.put(objectKey, (Cache.INDEX_PREFIX + keyStr).getBytes(getCharset()));
			keyMap.put(objectKey, parameter);
		}

		PrimaryKeyValue<T> primaryKeyValue = new PrimaryKeyValue<T>();
		Map<byte[], byte[]> cacheMap = redis.get(objToExistKeyMap.keySet().toArray(new byte[objToExistKeyMap.size()][]));
		if (cacheMap != null && !cacheMap.isEmpty()) {
			for (Entry<byte[], byte[]> entry : cacheMap.entrySet()) {
				primaryKeyValue.put(keyMap.get(entry.getKey()), CacheUtils.decode(type, entry.getValue()));
			}
		}

		if (cacheMap == null || cacheMap.size() != primaryKeyParameters.size()) {
			for(Entry<byte[], byte[]> entry : cacheMap.entrySet()){
				objToExistKeyMap.remove(entry.getKey());
			}

			// key是有缓存的
			if(checkKey){
				byte[] indexKey = type.getName().getBytes(getCharset());
				Iterator<Entry<byte[], byte[]>> iterator = objToExistKeyMap.entrySet().iterator();
				while(iterator.hasNext()){
					Entry<byte[], byte[]> entry = iterator.next();
					if(redis.sIsMember(indexKey, entry.getValue())){
						iterator.remove();
					}
				}
			}

			if(!objToExistKeyMap.isEmpty()){
				List<PrimaryKeyParameter> list = new ArrayList<PrimaryKeyParameter>();
				for(Entry<byte[], byte[]> entry : objToExistKeyMap.entrySet()){
					list.add(keyMap.get(entry.getKey()));
				}
				
				PrimaryKeyValue<T> dbMap = db.getByIdFromDB(type, null, list);
				if (dbMap != null) {
					primaryKeyValue.putAll(dbMap);
					//saveToCacheByKeys(bean, exp);
				}
			}
		}
		return primaryKeyValue;
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
