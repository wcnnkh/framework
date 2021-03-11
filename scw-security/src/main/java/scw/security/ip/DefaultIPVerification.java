package scw.security.ip;

import scw.core.parameter.annotation.DefaultValue;
import scw.core.utils.ArrayUtils;
import scw.core.utils.StringUtils;
import scw.instance.annotation.PropertyName;
import scw.instance.annotation.PropertyParameter;
import scw.lang.Nullable;
import scw.net.InetUtils;

public class DefaultIPVerification implements IPVerification {
	private final BlacklistIPVerification blacklistIPVerification;
	private final WhitelistIPVerification whitelistIPVerification;
	private final boolean ignoreInnerIp;

	/**
	 * @param blacklistIPVerification
	 *            黑名单检查
	 * @param whitelistIPVerification
	 *            白名单检查
	 * @param ignoreInnerIp
	 *            是否不检查内网ip
	 */
	public DefaultIPVerification(@Nullable @PropertyParameter(false) BlacklistIPVerification blacklistIPVerification,
			@PropertyParameter(false) WhitelistIPVerification whitelistIPVerification,
			@PropertyName(IGNORE_INNER_IP_NAME) @DefaultValue("true") boolean ignoreInnerIp) {
		this.blacklistIPVerification = blacklistIPVerification;
		this.whitelistIPVerification = whitelistIPVerification;
		this.ignoreInnerIp = ignoreInnerIp;
	}

	protected boolean check(String ip) {
		if (InetUtils.isLocalIP(ip)) {
			return true;
		}

		if (ignoreInnerIp && InetUtils.isInnerIP(ip)) {
			return true;
		}

		if (blacklistIPVerification != null && blacklistIPVerification.verification(ip)) {
			return false;
		}

		return whitelistIPVerification != null && whitelistIPVerification.verification(ip);
	}

	public boolean verification(String ip) {
		if (StringUtils.isEmpty(ip)) {
			return false;
		}

		String[] arr = StringUtils.commonSplit(ip);
		if (ArrayUtils.isEmpty(arr)) {
			return false;
		}

		for (String v : arr) {
			if (check(v)) {
				return true;
			}
		}
		return false;
	}

}
