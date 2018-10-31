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
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.TableInfo;
import shuchaowen.core.db.storage.CacheUtils;
import shuchaowen.redis.Redis;

public class RedisCache implements Cache{
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private static final String SPLIT = "#";
	private static final String EX = "EX";
	private static final String NX = "NX";
	private static final String XX = "XX";
	
	private final Redis redis;
	private final Charset charset;
	
	public RedisCache(Redis redis){
		this(redis, DEFAULT_CHARSET);
	}
	
	public RedisCache(Redis redis, String charsetName){
		this.charset = Charset.forName(charsetName);
		this.redis = redis;
	}
	
	public RedisCache(Redis redis, Charset charset){
		this.redis = redis;
		this.charset = charset;
	}
	
	public Redis getRedis() {
		return redis;
	}

	public Charset getCharset() {
		return charset == null? DEFAULT_CHARSET:charset;
	}

	public void saveBeanAndIndex(Object bean) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		Object[] values = new Object[tableInfo.getPrimaryKeyColumns().length];
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			values[i] = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
			if(i != 0){
				sb.append(SPLIT);
			}
			sb.append(values[i]);
		}
		
		byte[] objectKey = sb.toString().getBytes(getCharset());
		redis.hsetnx(tableInfo.getClassInfo().getName().getBytes(getCharset()), objectKey, CacheUtils.encode(bean));
		if(values.length > 1){
			sb = new StringBuilder();
			sb.append(tableInfo.getClassInfo().getName());
			sb.append(SPLIT);
			sb.append(values[0]);
			for(int i=1; i<values.length - 1; i++){
				byte[] indexKey = sb.toString().getBytes(getCharset());
				sb.append(SPLIT);
				sb.append(values[i]);
				redis.sadd(indexKey, objectKey);
			}
		}
	}

	public void updateBeanAndIndex(Object bean) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			if(i != 0){
				sb.append(SPLIT);
			}
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		redis.hset(tableInfo.getClassInfo().getName().getBytes(getCharset()), sb.toString().getBytes(getCharset()), CacheUtils.encode(bean));
	}

	public void deleteBeanAndIndex(Object bean) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		Object[] values = new Object[tableInfo.getPrimaryKeyColumns().length];
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			values[i] = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
			if(i != 0){
				sb.append(SPLIT);
			}
			sb.append(values[i]);
		}
		
		byte[] objectKey = sb.toString().getBytes(getCharset());
		redis.hdel(tableInfo.getClassInfo().getName().getBytes(getCharset()), objectKey);
		if(values.length > 1){
			sb = new StringBuilder();
			sb.append(tableInfo.getClassInfo().getName());
			sb.append(SPLIT);
			sb.append(values[0]);
			for(int i=1; i<values.length - 1; i++){
				byte[] indexKey = sb.toString().getBytes(getCharset());
				sb.append(SPLIT);
				sb.append(values[i]);
				redis.sadd(indexKey, objectKey);
			}
		}
	}

	public void saveOrUpdateBeanAndIndex(Object bean) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		Object[] values = new Object[tableInfo.getPrimaryKeyColumns().length];
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			values[i] = tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean);
			if(i != 0){
				sb.append(SPLIT);
			}
			sb.append(values[i]);
		}
		
		byte[] objectKey = sb.toString().getBytes(getCharset());
		redis.hset(tableInfo.getClassInfo().getName().getBytes(getCharset()), objectKey, CacheUtils.encode(bean));
		if(values.length > 1){
			sb = new StringBuilder();
			sb.append(tableInfo.getClassInfo().getName());
			sb.append(SPLIT);
			sb.append(values[0]);
			for(int i=1; i<values.length - 1; i++){
				byte[] indexKey = sb.toString().getBytes(getCharset());
				sb.append(SPLIT);
				sb.append(values[i]);
				redis.sadd(indexKey, objectKey);
			}
		}
	}

	public void saveBean(Object bean, int exp) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		redis.set(sb.toString().getBytes(getCharset()), CacheUtils.encode(bean), NX.getBytes(getCharset()), EX.getBytes(getCharset()), exp);
	}

	public void updateBean(Object bean, int exp) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		redis.set(sb.toString().getBytes(getCharset()), CacheUtils.encode(bean), XX.getBytes(getCharset()), EX.getBytes(getCharset()), exp);
	}

	public void deleteBean(Object bean) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		redis.delete(sb.toString().getBytes(getCharset()));
	}

	public void saveOrUpdateBean(Object bean, int exp) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		sb.append(tableInfo.getClassInfo().getName());
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		redis.setex(sb.toString().getBytes(getCharset()), exp, CacheUtils.encode(bean));
	}

	public void saveBeanAndKey(Object bean, int exp) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		
		redis.set((tableInfo.getClassInfo().getName() + sb.toString()).getBytes(getCharset()), CacheUtils.encode(bean), NX.getBytes(getCharset()), EX.getBytes(getCharset()), exp);
		redis.sadd(tableInfo.getClassInfo().getName().getBytes(getCharset()), sb.toString().getBytes(getCharset()));
	}

	public void updateBeanAndKey(Object bean, int exp) throws Exception {
		updateBean(bean, exp);
	}

	public void deleteBeanAndKey(Object bean) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		
		redis.delete((tableInfo.getClassInfo().getName() + sb.toString()).getBytes(getCharset()));
		redis.srem(tableInfo.getClassInfo().getName().getBytes(getCharset()), sb.toString().getBytes(getCharset()));
	}

	public void saveOrUpdateBeanAndKey(Object bean, int exp) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		
		redis.setex((tableInfo.getClassInfo().getName() + sb.toString()).getBytes(getCharset()), exp, CacheUtils.encode(bean));
		redis.sadd(tableInfo.getClassInfo().getName().getBytes(getCharset()), sb.toString().getBytes(getCharset()));
	}

	public void loadBeanAndIndex(Object bean) throws Exception {
		saveBeanAndIndex(bean);
	}

	public void loadBeanAndKey(Object bean) throws Exception {
		TableInfo tableInfo = AbstractDB.getTableInfo(bean.getClass());
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<tableInfo.getPrimaryKeyColumns().length; i++){
			sb.append(SPLIT);
			sb.append(tableInfo.getPrimaryKeyColumns()[i].getFieldInfo().forceGet(bean));
		}
		redis.sadd(tableInfo.getClassInfo().getName().getBytes(getCharset()), sb.toString().getBytes(getCharset()));
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
		for(Object v : params){
			sb.append(SPLIT);
			sb.append(v);
		}
		
		String keySuffix = sb.toString();
		byte[] key = (type.getName() + keySuffix).getBytes(getCharset());
		byte[] data = redis.get(key);
		T t = null;
		if (data == null) {
			if (checkKey) {
				if(redis.sIsMember(type.getName().getBytes(getCharset()), keySuffix.getBytes(getCharset()))){
					t = (T) db.getByIdFromDB(type, null, params);
					if (t != null) {
						saveBeanAndKey(t, exp);
					}
				}
			} else {
				t = (T) db.getByIdFromDB(type, null, params);
				if (t != null) {
					saveBean(t, exp);
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
			for(Object v :  parameter.getParams()){
				sb.append(SPLIT);
				sb.append(v);
			}
			
			String keySuffix = sb.toString();
			byte[] objectKey = (type.getName() + keySuffix).getBytes(getCharset());
			objToExistKeyMap.put(objectKey, keySuffix.getBytes(getCharset()));
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
}
