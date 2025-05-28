package run.soeasy.framework.core.exchange;

/**
 * 最终状态的回执
 * 
 * @author shuchaowen
 *
 */
public class Receipted extends Registed implements Receipt {
	private static final long serialVersionUID = 1L;
	private final boolean done;
	private final boolean success;
	private final Throwable cause;

	/**
	 * 一个未完成的回执
	 */
	public Receipted() {
		this(false, false, null);
	}

	/**
	 * 一个已完成的回执
	 * 
	 * @param success
	 * @param cause
	 */
	public Receipted(boolean success, Throwable cause) {
		this(true, success, cause);
	}

	protected Receipted(boolean done, boolean success, Throwable cause) {
		super(false);
		this.done = done;
		this.success = success;
		this.cause = cause;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public Throwable cause() {
		return cause;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}
}