package scw.integration.verification;

import java.io.Serializable;

public interface VerificationCode extends Serializable{
	String getCode();

	int getType();

	long getLastSendTime();
	
	int getSendCount();
	
	int getCheckCount();
}
