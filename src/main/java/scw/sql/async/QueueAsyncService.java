package scw.sql.async;

import scw.beans.annotaion.Bean;
import scw.beans.annotaion.Destroy;
import scw.sql.SqlOperations;
import scw.sql.Sqls;
import scw.sql.orm.SqlFormat;
import scw.transaction.DefaultTransactionLifeCycle;
import scw.transaction.TransactionManager;
import scw.utils.queue.Queue;

@Bean(proxy = false)
public class QueueAsyncService extends AbstractAsyncService implements Runnable {
	private Queue<Sqls> queue;
	private Thread thread;

	public QueueAsyncService(Queue<Sqls> queue, SqlOperations sqlOperations, SqlFormat sqlFormat) {
		super(sqlOperations, sqlFormat);
		this.queue = queue;
		thread = new Thread(this);
		thread.start();
	}

	public void run() {
		try {
			while (!thread.isInterrupted()) {
				Sqls sqls = queue.take();
				try {
					sqls.transactionExecute(sqlOperations);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
		}
	}

	@Destroy
	public void destroy() {
		thread.interrupt();
	}

	public void execute(final Sqls sqls) {
		if (TransactionManager.hasTransaction()) {
			TransactionManager.transactionLifeCycle(new DefaultTransactionLifeCycle() {
				@Override
				public void afterProcess() {
					queue.offer(sqls);
					super.afterProcess();
				}
			});
		} else {
			queue.offer(sqls);
		}
	}
}
