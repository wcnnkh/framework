package scw.security.ip;

import scw.core.annotation.NotRequire;
import scw.core.instance.annotation.PropertyParameter;
import scw.core.ip.IPUtils;

public class DefaultIPVerification implements IPVerification {
	private BlacklistIPVerification blacklistIPVerification;
	private WhitelistIPVerification whitelistIPVerification;

	public DefaultIPVerification(@NotRequire @PropertyParameter(false) BlacklistIPVerification blacklistIPVerification,
			@PropertyParameter(false) WhitelistIPVerification whitelistIPVerification) {
		this.blacklistIPVerification = blacklistIPVerification;
		this.whitelistIPVerification = whitelistIPVerification;
	}

	public boolean verification(String data) {
		if (IPUtils.isLocalIP(data)) {
			return true;
		}

		if (blacklistIPVerification != null && blacklistIPVerification.verification(data)) {// 如果存在黑名单中
			return false;
		}

		if (whitelistIPVerification == null) {
			return false;
		}

		return whitelistIPVerification.verification(data);
	}

}
