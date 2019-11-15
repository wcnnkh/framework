package scw.data;

import java.util.HashMap;
import java.util.Map;

import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

/**
 * 事务缓存
 * 
 * @author shuchaowen
 *
 */
@SuppressWarnings("unchecked")
public final class TransactionContextCache extends AbstractMapCache implements Cache {
	public static final TransactionContextCache TRANSACTION_CONTEXT_CACHE = new TransactionContextCache();

	public static Cache getInstance() {
		return TRANSACTION_CONTEXT_CACHE;
	}

	public Map<String, Object> getMap() {
		Transaction transaction = TransactionManager.getCurrentTransaction();
		if (transaction == null) {
			return null;
		}

		return (Map<String, Object>) transaction.getResource(TransactionContextCache.class);
	}

	@Override
	protected Map<String, Object> createMap() {
		Transaction transaction = TransactionManager.getCurrentTransaction();
		if (transaction == null) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>(8);
		transaction.bindResource(TransactionContextCache.class, map);
		return map;
	}
}
