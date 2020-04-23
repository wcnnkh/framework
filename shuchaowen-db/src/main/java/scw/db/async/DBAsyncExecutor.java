package scw.db.async;

import scw.async.AsyncExecutor;
import scw.async.AsyncExecutorWrapper;
import scw.async.AsyncLifeCycle;
import scw.async.AsyncRunnable;
import scw.db.DB;

public class DBAsyncExecutor extends AsyncExecutorWrapper {
	private DB db;

	public DBAsyncExecutor(DB db, AsyncExecutor asyncExecutor) {
		super(asyncExecutor);
		this.db = db;
		addAsyncLifeCycle(new DBAsyncLifeCycle());
	}

	public DB getDb() {
		return db;
	}

	private final class DBAsyncLifeCycle implements AsyncLifeCycle {

		public void executeBefore(AsyncRunnable asyncRunnable) throws Exception {
			if (asyncRunnable instanceof AsyncRunnableDB) {
				((AsyncRunnableDB) asyncRunnable).setDb(db);
			}
		}

		public void executeAfter(AsyncRunnable asyncRunnable) throws Exception {
			// TODO Auto-generated method stub

		}

		public void executeError(AsyncRunnable asyncRunnable) throws Exception {
			// TODO Auto-generated method stub

		}

		public void executeComplete(AsyncRunnable asyncRunnable) throws Exception {
			// TODO Auto-generated method stub

		}

	}
}
