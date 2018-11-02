package shuchaowen.core.db.storage.cache;

import java.util.Collection;
import java.util.List;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.OperationBean;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.transaction.Transaction;

public interface Cache {
	public static final String SPLIT = "#";
	public static final String INDEX_PREFIX = "index#";
	
	Transaction opByFull(OperationBean operationBean) throws Exception;
	
	Transaction opHotspot(OperationBean operationBean, int exp, boolean keys) throws Exception;
	
	void loadFull(Object bean) throws Exception;
	
	void loadKey(Object bean) throws Exception;
	
	<T> T getById(Class<T> type, Object ...params) throws Exception;
	
	<T> T getById(AbstractDB db, boolean checkKey, int exp, Class<T> type, Object ...params) throws Exception;

	<T> List<T> getByIdList(AbstractDB db, Class<T> type, Object ...params) throws Exception;
	
	<T> PrimaryKeyValue<T> getById(Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters) throws Exception;
	
	<T> PrimaryKeyValue<T> getById(AbstractDB db, boolean checkKey, Class<T> type, Collection<PrimaryKeyParameter> primaryKeyParameters) throws Exception;

	/**
	 * 热点数据异步回滚
	 * @param operationBean
	 * @param keys
	 * @param exist 数据在数据库是否存在
	 */
	void hostspotDataAsyncRollback(OperationBean operationBean, boolean keys, boolean exist) throws Exception;
}
 