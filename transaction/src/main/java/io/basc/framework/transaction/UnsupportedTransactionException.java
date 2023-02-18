package io.basc.framework.transaction;

import io.basc.framework.lang.UnsupportedException;

/**
 * 不支持事务
 * 
 * @author shuchaowen
 *
 */
public class UnsupportedTransactionException extends UnsupportedException {
	private static final long serialVersionUID = 1L;

	public UnsupportedTransactionException(String msg) {
		super(msg);
	}
}
