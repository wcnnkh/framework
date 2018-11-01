package shuchaowen.core.db.storage.async;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.PrimaryKeyParameter;
import shuchaowen.core.db.PrimaryKeyValue;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.storage.Storage;
import shuchaowen.core.util.Logger;

/**
 * @author shuchaowen
 *
 */
public abstract class AbstractAsyncStorage implements Storage{
	private final AbstractDB db;
	private final AsyncConsumer asyncConsumer;
	
	public AbstractAsyncStorage(AbstractDB db, AsyncConsumer asyncConsumer){
		this.db = db;
		this.asyncConsumer = asyncConsumer;
	}
	
	public AbstractDB getDb() {
		return db;
	}

	public AsyncConsumer getAsyncConsumer() {
		return asyncConsumer;
	}

	protected void logger(SQL sql) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(sql.getSql());
		sb.append("]");
		sb.append(" - ");
		sb.append(sql.getParams() == null ? "[]" : Arrays.toString(sql.getParams()));
		Logger.debug(this.getClass().getSimpleName(), sb.toString());
	}

	public <T> T getById(Class<T> type, Object... params) {
		return db.getByIdFromDB(type, null, params);
	}

	public <T> PrimaryKeyValue<T> getById(Class<T> type,
			Collection<PrimaryKeyParameter> primaryKeyParameters) {
		return db.getByIdFromDB(type, null, primaryKeyParameters);
	}

	public <T> List<T> getByIdList(Class<T> type, Object... params) {
		return db.getByIdListFromDB(type, null, params);
	}
}
