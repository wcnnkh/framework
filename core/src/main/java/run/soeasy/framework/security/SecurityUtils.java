package run.soeasy.framework.security;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SecurityUtils {

	public static KeyFactory getKeyFactory(String algorithm) {
		try {
			return KeyFactory.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new SecurityException("不支持的加密算法: " + algorithm, e);
		}
	}

	public static PrivateKey generatePrivateKey(String algorithm, KeySpec keySpec) {
		KeyFactory keyFactory = getKeyFactory(algorithm);
		try {
			return keyFactory.generatePrivate(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new SecurityException("无效的私钥格式（算法：" + algorithm + "）", e);
		}
	}

	public static PublicKey generatePublicKey(String algorithm, KeySpec keySpec) {
		KeyFactory keyFactory = getKeyFactory(algorithm);
		try {
			return keyFactory.generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			throw new SecurityException("无效的公钥格式（算法：" + algorithm + "）", e);
		}
	}
}