package scw.security.ip;

import scw.beans.annotation.AutoImpl;
import scw.core.Verification;

@AutoImpl({ DefaultIPVerification.class })
public interface IPVerification extends Verification<String> {
	boolean verification(String ip);
}
