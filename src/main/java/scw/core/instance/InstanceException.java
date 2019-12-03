package scw.core.instance;

import scw.lang.NestedRuntimeException;

/**
 * 只有此异常会被抛出
 * @author shuchaowen
 *
 */
public class InstanceException extends NestedRuntimeException{
	private static final long serialVersionUID = 1L;
	
	public InstanceException(String msg) {
		super(msg);
	}
	
	public InstanceException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public InstanceException(Throwable cause) {
		super(cause);
	}
}
