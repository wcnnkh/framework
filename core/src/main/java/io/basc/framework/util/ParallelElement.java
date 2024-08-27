package io.basc.framework.util;

import java.io.Serializable;

import lombok.Data;

@Data
public final class ParallelElement<L, R> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final IterativeElement<L> left;
	private final IterativeElement<R> right;

	/**
	 * 并行分支是否都存在
	 * 
	 * @return
	 */
	public boolean isPresent() {
		return left != null && right != null;
	}

	public L getLeftValue() {
		return left == null ? null : left.getValue();
	}

	public R getRightValue() {
		return right == null ? null : right.getValue();
	}
}
