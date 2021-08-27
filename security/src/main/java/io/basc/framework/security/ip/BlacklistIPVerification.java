package io.basc.framework.security.ip;

import io.basc.framework.env.Environment;
import io.basc.framework.instance.annotation.PropertyName;

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
