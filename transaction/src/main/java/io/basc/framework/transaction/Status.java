package io.basc.framework.transaction;

import java.io.Serializable;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.function.ParentDiscover;

public final class Status implements Serializable, ParentDiscover<Status> {
	private static final long serialVersionUID = 1L;

	public static final Status UNKNOWN = new Status(null, 0, "未知");

	public static final Status ACTIVE = new Status(UNKNOWN, 1, "活跃的");
	public static final Status MARKED_ROLLBACK = new Status(UNKNOWN, 2, "标记为已回滚");

	public static final Status COMMITTING = new Status(UNKNOWN, 3, "提交中");
	public static final Status COMMITTED = new Status(UNKNOWN, 4, "已提交");

	public static final Status ROLLING_BACK = new Status(UNKNOWN, 5, "回滚中");
	public static final Status ROLLED_BACK = new Status(UNKNOWN, 6, "已回滚");

	public static final Status COMPLETED = new Status(UNKNOWN, 7, "已完成");

	private final Status parent;
	private final int code;
	private final String describe;

	public Status(@Nullable Status parent, int code, String describe) {
		Assert.requiredArgument(StringUtils.isNotEmpty(describe), "describe");
		this.parent = parent;
		this.code = code;
		this.describe = describe;
	}

	@Override
	public Status getParent() {
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

		if (obj instanceof Status) {
			return this.code == ((Status) obj).code;
		}

		return false;
	}

	public boolean isCommitting() {
		return equals(COMMITTING) || isParents(COMMITTING);
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

	public Status changeTo(Status status) {
		return new Status(this, status.code, status.describe);
	}
}
