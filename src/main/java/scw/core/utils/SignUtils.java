package scw.core.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import scw.core.Base64;
import scw.core.exception.NotFoundException;
import scw.security.signature.SignatureUtils;

public final class SignUtils {
	private SignUtils() {
	};

	public static StringBuilder getShotParamsStr(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		if (map != null) {
			String[] keys = map.keySet().toArray(new String[0]);
			Arrays.sort(keys);
			String k;
			String v;
			for (int i = 0; i < keys.length; i++) {
				k = keys[i];
				if (k == null) {
					continue;
				}

				v = map.get(k);
				if (v == null) {
					continue;
				}

				if (sb.length() > 0) {
					sb.append("&");
				}
				sb.append(k).append("=").append(v);
			}
		}
		return sb;
	}

	/**
	 * HmacMD5加密
	 * 
	 * @param data
	 * @param secret
	 * @return
	 * @throws IOException
	 */
	public static byte[] HmacMD5(String data, String secret, String charsetName) {
		try {
			return HmacEncrypt(data.getBytes(charsetName),
					secret.getBytes(charsetName), "HmacMD5");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 签名方法对对encryptText进行签名
	 * 
	 * @param encryptText
	 *            被签名内容
	 * @param encryptKey
	 *            密钥
	 * @param signType
	 *            签名方式 HmacSHA1 HmacMD5
	 * @return
	 * @throws Exception
	 */
	public static byte[] HmacEncrypt(byte[] encryptText, byte[] encryptKey,
			String signType) {
		try {
			SecretKey secretKey = new SecretKeySpec(encryptKey, signType);
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			return mac.doFinal(encryptText);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 使用 HMAC-SHA1 签名方法对对encryptText进行签名
	 * 
	 * @param encryptText
	 *            被签名的字符串
	 * @param encryptKey
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static byte[] HmacSHA1(String encryptText, String encryptKey,
			String charsetName) {
		try {
			return HmacEncrypt(encryptText.getBytes(charsetName),
					encryptKey.getBytes(charsetName), "HmacSHA1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] md5(byte[] byteArray) {
		try {
			return MessageDigest.getInstance("MD5").digest(byteArray);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String md5Str(byte[] byteArray) {
		return SignatureUtils.byte2hex(md5(byteArray));
	}

	public static String md5UpperStr(String str, String charsetName) {
		try {
			return md5Str(str.getBytes(charsetName)).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] md5Tobyte(String str, String charsetName) {
		try {
			return md5(str.getBytes(charsetName));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String md5Str(String str, String charsetName) {
		try {
			return md5Str(str.getBytes(charsetName));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String sha1(byte[] byteArray) {
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(byteArray);
			return SignatureUtils.byteToHex(crypt.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String sha1(String str, String charsetName) {
		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(str.getBytes(charsetName));
			return SignatureUtils.byteToHex(crypt.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String base64Encode(String text, String charsetName) {
		try {
			return Base64.encode(text.getBytes(charsetName));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String characterStringSign(String text, String charsetName,
			SignType signType) {
		Assert.notNull(text);
		Assert.notNull(charsetName);
		Assert.notNull(signType);
		switch (signType) {
		case MD5:
			return md5Str(text, charsetName);
		case BASE64:
			return base64Encode(text, charsetName);
		case MD5_UPPERCASE:
			return md5UpperStr(text, charsetName);
		case SHA1:
			return sha1(text, charsetName);
		default:
			throw new NotFoundException("不支持的签名方式");
		}
	}

	public static String characterStringSign(String text, String charsetName,
			SignType... signType) {
		Assert.notEmpty(signType);
		String sign = text;
		for (SignType type : signType) {
			sign = characterStringSign(sign, charsetName, type);
		}
		return sign;
	}
}
