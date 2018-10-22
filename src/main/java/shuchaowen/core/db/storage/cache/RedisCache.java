package shuchaowen.core.db.storage.cache;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;

//TODO 还未实现
public class RedisCache implements Cache{
	private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
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
		//TODO 
	}

	public void updateBeanAndIndex(Object bean) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void deleteBeanAndIndex(Object bean) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void saveOrUpdateBeanAndIndex(Object bean) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void saveBean(Object bean, int exp) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateBean(Object bean, int exp) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void deleteBean(Object bean) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void saveOrUpdateBean(Object bean, int exp) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void saveBeanAndKey(Object bean, int exp) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void updateBeanAndKey(Object bean, int exp) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void deleteBeanAndKey(Object bean) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void saveOrUpdateBeanAndKey(Object bean, int exp) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void loadBeanAndIndex(Object bean) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public void loadBeanAndKey(Object bean) throws Exception {
		// TODO Auto-generated method stub
		
	}

	public <T> T getById(Class<T> type, Object... params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getById(AbstractDB db, boolean checkKey, int exp, Class<T> type, Object... params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> List<T> getByIdList(AbstractDB db, Class<T> type, Object... params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> PrimaryKeyValue<T> getById(AbstractDB db, boolean checkKey, Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
