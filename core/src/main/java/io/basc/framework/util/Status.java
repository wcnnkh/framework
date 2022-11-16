package io.basc.framework.util;

import java.io.Serializable;

import io.basc.framework.lang.Nullable;

public interface Status extends Serializable {

	static Status success() {
		return success(null);
	}

	static Status success(String msg) {
		return success(0, msg);
	}

	static Status success(long code, String msg) {
		return new StandardStatus(true, code, msg);
	}

	static Status error(@Nullable String msg) {
		return error(0, msg);
	}

	static Status error(long code, @Nullable String msg) {
		return new StandardStatus(false, code, msg);
	}

	boolean isSuccess();

	default boolean isError() {
		return !isSuccess();
	}

	long getCode();

	/**
	 * message的简写
	 * 
	 * @return
	 */
	@Nullable
	String getMsg();

	default void assertSuccess() {
		String msg = getMsg();
		if (StringUtils.isEmpty(msg)) {
			Assert.isTrue(isSuccess());
		} else {
			Assert.isTrue(isSuccess(), msg);
		}
	}

	default <T> Return<T> toReturn() {
		return toReturn(null);
	}

	default <T> Return<T> toReturn(@Nullable T value) {
		return new StandardReturn<T>(isSuccess(), getCode(), getMsg(), value);
	}
}
