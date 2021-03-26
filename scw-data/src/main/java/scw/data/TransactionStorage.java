package scw.data;

import java.util.HashMap;
import java.util.Map;

import scw.transaction.Transaction;
import scw.transaction.TransactionUtils;

/**
 * 事务缓存
 * 
 * @author shuchaowen
 *
 */
@SuppressWarnings("unchecked")
public final class TransactionStorage extends AbstractMapStorage implements Storage {
	private final Object name;
	
	public TransactionStorage(Object name){
		this.name = name;
	};
	
	public void clear(){
		Map<String, Object> map = getMap();
		if(map != null){
			map.clear();
		}
	}
	
	public Map<String, Object> getMap() {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			return null;
		}

		return (Map<String, Object>) transaction.getResource(name);
	}

	@Override
	protected Map<String, Object> createMap() {
		Transaction transaction = TransactionUtils.getManager().getTransaction();
		if (transaction == null) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>(8);
		Map<String, Object> cache = transaction.bindResource(name, map);
		if(cache == null){
			cache = map;
		}
		return cache;
	}
}
