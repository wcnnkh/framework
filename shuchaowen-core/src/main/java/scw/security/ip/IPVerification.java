package scw.security.ip;

import scw.beans.annotation.AutoImpl;
import scw.core.Verification;

@AutoImpl({ DefaultIPVerification.class })
public interface IPVerification extends Verification<String> {
	public static final String IGNORE_INNER_IP_NAME = "ip.verification.ignore.inner";

	boolean verification(String ip);
}
