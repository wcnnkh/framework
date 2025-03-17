package run.soeasy.framework.lang;

/**
 * 不可能执行到这里
 * 
 * @author shuchaowen
 *
 */
public class ImpossibleException extends IllegalStateException {
	private static final long serialVersionUID = 1L;

	/**
	 * 构造一个不可能出现的异常
	 */
	public ImpossibleException() {
		super("Should never get here");
	}
}
