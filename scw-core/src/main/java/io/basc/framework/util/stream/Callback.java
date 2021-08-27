package io.basc.framework.util.stream;

/**
 * 一个回调的定义
 * 
 * @author shuchaowen
 *
 * @param <S>
 *            回调的数据类型
 * @param <E>
 *            异常类型
 */
@FunctionalInterface
public interface Callback<S, E extends Throwable> {
	void call(S source) throws E;

	/**
	 * 在执行之前添加一个回调
	 * @param callback
	 * @return
	 */
	default Callback<S, E> beforeCall(Callback<S, E> callback) {
		if (callback == null) {
			return this;
		}

		final Callback<S, E> self = this;
		return new Callback<S, E>() {

			@Override
			public void call(S source) throws E {
				try {
					callback.call(source);
				} finally {
					self.call(source);
				}
			}
		};
	}

	/**
	 * 在回调之后追加一个回调
	 * @param callback
	 * @return
	 */
	default Callback<S, E> afterCall(Callback<S, E> callback) {
		if (callback == null) {
			return this;
		}

		final Callback<S, E> self = this;
		return new Callback<S, E>() {

			@Override
			public void call(S source) throws E {
				try {
					self.call(source);
				} finally {
					callback.call(source);
				}
			}
		};
	}
}
