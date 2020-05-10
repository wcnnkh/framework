package scw.integration.verification;

import scw.result.Result;

public interface VerificationCodeService<U> {
	Result send(int type, U user);

	VerificationCode getVerificationCode(int type, U user);

	Result verification(int type, U user, String code);

	/**
	 * @param type
	 * @param member
	 * @param code
	 * @param maxCheckCount
	 *            最多可以校验的次数
	 * @return
	 */
	Result verification(int type, U user, String code, int maxCheckCount);
}
