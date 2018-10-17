package shuchaowen.core.db.storage.async;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import shuchaowen.core.cache.Redis;
import shuchaowen.core.db.AbstractDB;
import shuchaowen.core.db.DBUtils;
import shuchaowen.core.db.sql.SQL;
import shuchaowen.core.db.storage.AbstractAsyncStorage;
import shuchaowen.core.db.storage.ExecuteInfo;
import shuchaowen.core.util.IOUtils;

/**
 * 使用redis的队列实现异步存盘,该方案可用于集群
 * 
 * @author shuchaowen
 *
 */
public class RedisAsyncStorage extends AbstractAsyncStorage {
	private final static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private final Redis redis;
	private final byte[] queueKey;
	private final boolean sqlDebug;

	public RedisAsyncStorage(AbstractDB db, Redis redis) {
		this(db, redis, db.getClass().getName(), true);
	}

	public RedisAsyncStorage(AbstractDB db, final Redis redis, String queueKey,
			final boolean sqlDebug) {
		super(db);
		this.sqlDebug = sqlDebug;
		this.redis = redis;
		final byte[] key = queueKey.getBytes(DEFAULT_CHARSET);
		this.queueKey = key;
		new Thread(new Runnable() {

			public void run() {
				while (!Thread.interrupted()) {
					try {
						List<byte[]> dataList = redis.brpop(key);
						for(byte[] data : dataList){
							try {
								ExecuteInfo executeInfo = IOUtils.byteToJavaObject(data);
								next(executeInfo);
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							} catch (IOException e) {
								e.printStackTrace();
							}
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
			redis.lpush(queueKey, data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
