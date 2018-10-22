package shuchaowen.core.db.storage.async;

import java.io.IOException;
import java.util.Collection;

import shuchaowen.core.cache.Memcached;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DBUtils;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.storage.AbstractAsyncStorage;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.util.IOUtils;
import shuchaowen.core.util.MemcachedQueue;

public class MemcachedAsyncStorage extends AbstractAsyncStorage{
	private MemcachedQueue memcachedQueue;
	private final boolean sqlDebug;

	public MemcachedAsyncStorage(AbstractDB db, Memcached memcached, String queueKey) {
		this(db, memcached, queueKey, true);
	}

	public MemcachedAsyncStorage(AbstractDB db, final Memcached memcached, final String queueKey, final boolean sqlDebug) {
		super(db);
		this.sqlDebug = sqlDebug;
		memcachedQueue = new MemcachedQueue(queueKey, memcached);
		new Thread(new Runnable() {

			public void run() {
				while (!Thread.interrupted()) {
					try {
						Thread.sleep(100L);
						byte[] data = memcachedQueue.lockRead();
						if (data == null) {
							continue;
						}

						try {
							ExecuteInfo executeInfo = IOUtils.byteToJavaObject(data);
							next(executeInfo);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						Thread.sleep(1L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void next(ExecuteInfo executeInfo) {
		if (executeInfo == null) {
			return;
		}

		Collection<SQL> sqls = getSqlList(executeInfo);
		if (sqlDebug) {
			for (SQL sql : sqls) {
				logger(sql);
			}
		}

		try {
			DBUtils.execute(getDb(), sqls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute(ExecuteInfo executeInfo) {
		// 这里使用jdk的序列化方式 ，因为不会出现各种没有经历过的问题
		try {
			byte[] data = IOUtils.javaObjectToByte(executeInfo);
			memcachedQueue.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Memcached getMemcached(){
		return memcachedQueue.getMemcached();
	}
}
