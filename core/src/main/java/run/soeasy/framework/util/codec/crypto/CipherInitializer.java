package run.soeasy.framework.util.codec.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;

public interface CipherInitializer {
	void init(Cipher cipher, int opmode) throws GeneralSecurityException;
}
