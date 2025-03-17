package run.soeasy.framework.util.exchange;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 回执
 * 
 * @author shuchaowen
 *
 */
public interface Receipt extends Registration {

	@RequiredArgsConstructor
	public static class SuccessfullyRegistered implements Receipt, RegistrationWrapper<Registration> {
		@NonNull
		private final Registration source;

		@Override
		public Registration getSource() {
			return source;
		}

		@Override
		public Throwable cause() {
			return null;
		}

		@Override
		public boolean isDone() {
			return true;
		}

		@Override
		public boolean isSuccess() {
			return true;
		}
	}

	public static interface ReceiptWrapper<W extends Receipt> extends Receipt, RegistrationWrapper<W> {
		@Override
		default Throwable cause() {
			return getSource().cause();
		}

		@Override
		default boolean isDone() {
			return getSource().isDone();
		}

		@Override
		default boolean isSuccess() {
			return getSource().isSuccess();
		}
	}

	/**
	 * 最终状态的回执
	 * 
	 * @author shuchaowen
	 *
	 */
	public static class Receipted extends Registed implements Receipt {
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

	public static final Receipt FAILURE = new Receipted(false, null);
	public static final Receipt SUCCESS = new Receipted(true, null);

	public static Receipt failure(Throwable cause) {
		return new Receipted(false, cause);
	}

	public static Receipt success(Throwable cause) {
		return new Receipted(true, cause);
	}

	public static Receipt success(@NonNull Registration registration) {
		return new SuccessfullyRegistered(registration);
	}

	/**
	 * 异常信息
	 * 
	 * @return
	 */
	Throwable cause();

	/**
	 * 是否已完成
	 * 
	 * @return
	 */
	boolean isDone();

	/**
	 * 是否成功
	 * 
	 * @return
	 */
	boolean isSuccess();

	/**
	 * 
	 * 同步
	 * 
	 * @return 返回一个同步后的回执
	 */
	default Receipt sync() {
		return this;
	}
}
