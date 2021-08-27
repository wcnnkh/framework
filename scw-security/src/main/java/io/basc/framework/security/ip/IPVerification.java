package io.basc.framework.security.ip;

import io.basc.framework.beans.annotation.AutoImpl;

@AutoImpl({ DefaultIPVerification.class })
public interface IPVerification {
	public static final String IGNORE_INNER_IP_NAME = "ip.verification.ignore.inner";

	boolean verification(String ip);
}
