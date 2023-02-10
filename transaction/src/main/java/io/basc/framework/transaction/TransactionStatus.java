package io.basc.framework.transaction;

import java.io.Serializable;
import java.util.TreeMap;
import java.util.Map.Entry;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ParentDiscover;

public final class TransactionStatus implements Serializable, ParentDiscover<TransactionStatus> {
	private static final long serialVersionUID = 1L;

	private static final TreeMap<Integer, TreeMap<Integer, TransactionStatus>> STATUSES = new TreeMap<>();

	public static TransactionStatus of(int order, int status, @Nullable String describe) {
		TreeMap<Integer, TransactionStatus> map = STATUSES.get(order);
		if (map != null) {
			TransactionStatus transactionStatus = map.get(status);
			if (transactionStatus != null) {
				return transactionStatus;
			}
		}

		if (describe == null) {
			return null;
		}

		synchronized (STATUSES) {
			map = STATUSES.get(order);
			if (map != null) {
				TransactionStatus transactionStatus = map.get(status);
				if (transactionStatus != null) {
					return transactionStatus;
				}
			}

			if (map == null) {
				map = new TreeMap<>();
				STATUSES.put(order, map);
			}

			TransactionStatus transactionStatus = new TransactionStatus(order, status, describe);
			map.put(status, transactionStatus);
			return transactionStatus;
		}
	}

	public static final TransactionStatus CREATED = of(0, 0, "已创建");

	public static final TransactionStatus BEFORE_COMMIT = of(10000, 1000, "事务提交前");
	public static final TransactionStatus AFTER_COMMIT = of(10000, 2000, "事务提交后");

	public static final TransactionStatus BEFORE_ROLLBACK = of(10000, 3000, "事务回滚前");
	public static final TransactionStatus AFTER_ROLLBACK = of(10000, 4000, "事务回滚后");

	public static final TransactionStatus BEFORE_COMPLETION = of(Integer.MAX_VALUE, 5000, "事务完成前");
	public static final TransactionStatus AFTER_COMPLETION = of(Integer.MAX_VALUE, 6000, "事务完成后");

	private final String describe;
	private final int order;

	private final int status;

	private TransactionStatus(int order, int status, String describe) {
		this.order = order;
		this.status = status;
		this.describe = describe;
	}

	public final String getDescribe() {
		return describe;
	}

	public final int getOrder() {
		return order;
	}

	@Override
	public TransactionStatus getParent() {
		TreeMap<Integer, TransactionStatus> map = STATUSES.get(order);
		if (map == null) {
			// 不会到这里
			return null;
		}

		// 返回与严格小于给定键的最大键关联的键值映射，如果没有该键，则返回null。
		Entry<Integer, TransactionStatus> entry = map.lowerEntry(status);
		if (entry == null) {
			// 同一个order下不存在上一个状态了
			Entry<Integer, TreeMap<Integer, TransactionStatus>> parentMap = STATUSES.lowerEntry(order);
			if (parentMap == null) {
				// 不存在上一个order了
				return null;
			}

			return parentMap.getValue().lastEntry().getValue();
		}
		return entry.getValue();
	}

	public final int getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return this.describe;
	}
}
