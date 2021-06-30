package scw.security.ip;

import scw.env.Environment;
import scw.instance.annotation.ResourceParameter;
import scw.lang.DefaultValue;

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

	public WhitelistIPVerification(Environment environment, @ResourceParameter@DefaultValue("ip-whitelist") String sourceFile) {
		appendIPFile(environment, sourceFile);
	}

}
