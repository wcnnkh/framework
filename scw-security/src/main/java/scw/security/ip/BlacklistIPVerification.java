package scw.security.ip;

import scw.core.parameter.annotation.ParameterName;
import scw.env.Environment;

/**
 * 黑名单 检查是否存在于黑名单中
 * 
 * @author shuchaowen
 *
 */
public final class BlacklistIPVerification extends BaseIPVerification {
	private static final long serialVersionUID = 1L;

	public BlacklistIPVerification(Environment environment, @ParameterName("ip-blacklist") String sourceFile) {
		appendIPFile(environment, sourceFile);
	}
}
