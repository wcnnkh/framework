package io.basc.framework.transaction;

import java.io.Serializable;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.StringUtils;

public final class TransactionStatus implements Serializable, ParentDiscover<TransactionStatus> {
	private static final long serialVersionUID = 1L;

	public static final TransactionStatus UNKNOWN = new TransactionStatus(null, 0, "未知");

	public static final TransactionStatus ACTIVE = new TransactionStatus(UNKNOWN, 1, "活跃的");
	public static final TransactionStatus MARKED_ROLLBACK = new TransactionStatus(UNKNOWN, 2, "标记为已回滚");

	public static final TransactionStatus COMMITTING = new TransactionStatus(UNKNOWN, 3, "提交中");
	public static final TransactionStatus COMMITTED = new TransactionStatus(UNKNOWN, 4, "已提交");

	public static final TransactionStatus ROLLING_BACK = new TransactionStatus(UNKNOWN, 5, "回滚中");
	public static final TransactionStatus ROLLED_BACK = new TransactionStatus(UNKNOWN, 6, "已回滚");

	public static final TransactionStatus COMPLETED = new TransactionStatus(UNKNOWN, 7, "已完成");

	private final TransactionStatus parent;
	private final int code;
	private final String describe;

	public TransactionStatus(@Nullable TransactionStatus parent, int code, String describe) {
		Assert.requiredArgument(StringUtils.isNotEmpty(describe), "describe");
		this.parent = parent;
		this.code = code;
		this.describe = describe;
	}

	@Override
	public TransactionStatus getParent() {
		return parent;
	}

	public String getDescribe() {
		return describe;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String toString() {
		return describe + "(" + code + ")";
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(code);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof TransactionStatus) {
			return this.code == ((TransactionStatus) obj).code;
		}

		return false;
	}

	public boolean isCommitted() {
		return equals(COMMITTED) || isParents(COMMITTED);
	}

	public boolean isRolledBack() {
		return equals(ROLLED_BACK) || isParents(ROLLED_BACK);
	}

	public boolean isCompleted() {
		return equals(COMPLETED) || isParents(COMPLETED);
	}

	public TransactionStatus changeTo(TransactionStatus status) {
		return new TransactionStatus(this, status.code, status.describe);
	}
}
