package shuchaowen.core.db.storage.cache;

public class CacheHelper {
	private final Cache cache;
	
	public CacheHelper(Cache cache){
		this.cache = cache;
	}
	
	public void saveToCache(Object bean, CacheConfig cacheConfig){
		try {
			switch (cacheConfig.getCacheType()) {
			case lazy:
				cache.saveBean(bean, cacheConfig.getExp());
				break;
			case keys:
				cache.saveBeanAndKey(bean, cacheConfig.getExp());
				break;
			case full:
				cache.saveBeanAndIndex(bean);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateToCache(Object bean, CacheConfig cacheConfig){
		try {
			switch (cacheConfig.getCacheType()) {
			case lazy:
				cache.updateBean(bean, cacheConfig.getExp());
				break;
			case keys:
				cache.updateBeanAndKey(bean, cacheConfig.getExp());
				break;
			case full:
				cache.updateBeanAndIndex(bean);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteToCache(Object bean, CacheConfig cacheConfig){
		try {
			switch (cacheConfig.getCacheType()) {
			case lazy:
				cache.deleteBean(bean);
				break;
			case keys:
				cache.deleteBeanAndKey(bean);
				break;
			case full:
				cache.deleteBeanAndIndex(bean);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveOrUpdateToCache(Object bean, CacheConfig cacheConfig){
		try {
			switch (cacheConfig.getCacheType()) {
			case lazy:
				cache.saveOrUpdateBean(bean, cacheConfig.getExp());
				break;
			case keys:
				cache.saveOrUpdateBeanAndKey(bean, cacheConfig.getExp());
				break;
			case full:
				cache.saveOrUpdateBeanAndIndex(bean);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
