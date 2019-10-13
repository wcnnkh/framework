package scw.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Consumer;
import scw.core.utils.CollectionUtils;
import scw.db.async.AsyncInfo;
import scw.db.async.MultipleOperation;
import scw.db.cache.LazyCacheManager;
import scw.mq.MQ;
import scw.sql.Sql;
import scw.transaction.DefaultTransactionDefinition;
import scw.transaction.Transaction;
import scw.transaction.TransactionManager;

public abstract class AbstractLazyCacheDB extends AbstractDB<LazyCacheManager> implements Consumer<AsyncInfo> {
	private final LazyCacheManager lazyCacheManager;
	private final MQ<AsyncInfo> mq;
	private final String queueName;
	private boolean inIdAppendCache = true;// 使用inId查询结果是否添加到缓存

	public AbstractLazyCacheDB(LazyCacheManager lazyCacheManager, MQ<AsyncInfo> mq, String queueName) {
		this.lazyCacheManager = lazyCacheManager;
		this.mq = mq;
		this.queueName = queueName;
		mq.bindConsumer(queueName, this);
		logger.info("异步队列名称：{}", queueName);
	}

	protected final boolean isInIdAppendCache() {
		return inIdAppendCache;
	}

	protected void setInIdAppendCache(boolean inIdAppendCache) {
		this.inIdAppendCache = inIdAppendCache;
	}

	public final MQ<AsyncInfo> getMQ() {
		return mq;
	}

	public final String getQueueName() {
		return queueName;
	}

	@Override
	public LazyCacheManager getCacheManager() {
		return lazyCacheManager;
	}

	public final void consume(AsyncInfo message) {
		Transaction transaction = TransactionManager.getTransaction(new DefaultTransactionDefinition());
		Collection<Sql> sqls = message.getSqls();
		MultipleOperation multipleOperation = message.getMultipleOperation();
		try {
			if (sqls != null) {
				for (Sql sql : sqls) {
					if (sql == null) {
						continue;
					}

					execute(sql);
				}
			}

			if (multipleOperation != null) {
				List<Sql> list = multipleOperation.format(getSqlFormat());
				if (list != null) {
					for (Sql sql : list) {
						if (sql == null) {
							continue;
						}

						execute(sql);
					}
				}
			}

			TransactionManager.commit(transaction);
		} catch (Throwable e) {
			TransactionManager.rollback(transaction);
			e.printStackTrace();
		}
	}

	@Override
	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params) {
		LazyCacheManager lazyCacheManager = getCacheManager();
		if (lazyCacheManager == null) {
			return super.getInIdList(type, tableName, inIds, params);
		}

		if (inIds == null || inIds.isEmpty()) {
			return null;
		}

		Map<K, V> map = lazyCacheManager.getInIdList(type, inIds, params);
		if (CollectionUtils.isEmpty(map)) {
			Map<K, V> valueMap = super.getInIdList(type, tableName, inIds, params);
			if (inIdAppendCache) {
				if (!CollectionUtils.isEmpty(valueMap)) {
					for (Entry<K, V> entry : valueMap.entrySet()) {
						lazyCacheManager.save(entry.getValue());
					}
				}
			}
			return valueMap;
		}

		if (map.size() == inIds.size()) {
			return map;
		}

		List<K> notFoundList = new ArrayList<K>(inIds.size());
		for (K k : inIds) {
			if (k == null) {
				continue;
			}

			if (map.containsKey(k)) {
				continue;
			}

			notFoundList.add(k);
		}

		Map<K, V> dbMap = super.getInIdList(type, tableName, notFoundList, params);
		if (dbMap == null || dbMap.isEmpty()) {
			return map;
		}

		if (inIdAppendCache) {
			for (Entry<K, V> entry : dbMap.entrySet()) {
				lazyCacheManager.save(entry.getValue());
			}
		}

		map.putAll(dbMap);
		return map;
	}

	@Override
	public void async(AsyncInfo asyncInfo) {
		mq.push(getQueueName(), asyncInfo);
	}
}