package scw.security.ip;

import scw.env.Environment;
import scw.instance.annotation.PropertyName;

/**
 * 黑名单 检查是否存在于黑名单中
 * 
 * @author shuchaowen
 *
 */
public final class BlacklistIPVerification extends BaseIPVerification {
	private static final long serialVersionUID = 1L;

	public BlacklistIPVerification(Environment environment, @PropertyName("ip-blacklist") String sourceFile) {
		appendIPFile(environment, sourceFile);
	}
}
