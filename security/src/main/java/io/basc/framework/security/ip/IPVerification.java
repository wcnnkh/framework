package io.basc.framework.security.ip;

import io.basc.framework.beans.annotation.AutoImpl;

@AutoImpl({ DefaultIPVerification.class })
public interface IPVerification {
	boolean verification(String ip);
}
