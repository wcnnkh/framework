package scw.db.async;

import scw.async.AsyncExecutor;
import scw.async.AsyncExecutorWrapper;
import scw.async.AsyncLifeCycle;
import scw.async.AsyncRunnable;
import scw.db.DB;
import scw.sql.Sql;
import scw.sql.orm.enums.OperationType;

public class AsyncDB extends AsyncExecutorWrapper {
	private DB db;

	public AsyncDB(DB db, AsyncExecutor asyncExecutor) {
		super(asyncExecutor);
		this.db = db;
		addAsyncLifeCycle(new DBAsyncLifeCycle());
	}

	public DB getDb() {
		return db;
	}

	public void execute(Sql sql) {
		execute(new SqlAsyncExecute(sql));
	}

	public void execute(Object bean, String name, OperationType operationType) {
		execute(new BeanAsyncExecute(bean, name, operationType));
		;
	}

	public void execute(Object bean, OperationType operationType) {
		execute(new BeanAsyncExecute(bean, operationType));
		;
	}

	public void save(Object bean) {
		execute(bean, OperationType.SAVE);
	}

	public void update(Object bean) {
		execute(bean, OperationType.UPDATE);
	}

	public void delete(Object bean) {
		execute(bean, OperationType.DELETE);
	}

	public void saveOrUpdate(Object bean) {
		execute(bean, OperationType.SAVE_OR_UPDATE);
	}

	private final class DBAsyncLifeCycle implements AsyncLifeCycle {

		public void executeBefore(AsyncRunnable asyncRunnable) throws Exception {
			if (asyncRunnable instanceof AsyncExecute) {
				((AsyncExecute) asyncRunnable).setDb(db);
			}
		}

		public void executeAfter(AsyncRunnable asyncRunnable) throws Exception {
			// TODO Auto-generated method stub

		}

		public void executeError(Throwable error, AsyncRunnable asyncRunnable) throws Exception {
			// TODO Auto-generated method stub

		}

		public void executeComplete(AsyncRunnable asyncRunnable)
				throws Exception {
			// TODO Auto-generated method stub

		}

	}
}
