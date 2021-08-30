package io.basc.framework.security.ip;

import io.basc.framework.env.Environment;
import io.basc.framework.io.Resource;

/**
 * 白名单
 * 
 * 检查是否存在于白名单中
 * 
 * @author shuchaowen
 *
 */
public final class WhitelistIPVerification extends BaseIPVerification {
	private static final long serialVersionUID = 1L;

	public WhitelistIPVerification(Environment environment, Resource sourceFile) {
		appendIPFile(environment, sourceFile);
	}

}
