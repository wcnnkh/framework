package io.basc.framework.observe;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

@Data
public class Changed<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 变更前
	 */
	private final T before;
	/**
	 * 变更后
	 */
	private final T after;

	@NonNull
	private volatile ChangeType changeType;

	public Changed(T before, T after) {
		this.before = before;
		this.after = after;
	}

	public ChangeType getChangeType() {
		if (changeType == null) {
			synchronized (this) {
				if (changeType == null) {
					if (before == null && after != null) {
						changeType = ChangeType.CREATE;
					} else if (before != null && after == null) {
						changeType = ChangeType.DELETE;
					} else {
						changeType = ChangeType.UPDATE;
					}
				}
			}
		}
		return changeType;
	}
}
