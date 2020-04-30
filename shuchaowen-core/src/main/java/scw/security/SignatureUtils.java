package scw.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import scw.core.Assert;
import scw.lang.NotSupportedException;
import scw.util.Base64;

public final class SignatureUtils {
	private static final String DEFAULT_CONCAT = "&";

	private SignatureUtils() {
	};

	/**
	 * 把二进制转化为十六进制
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteToHex(final byte[] hash) {
		Formatter formatter = new Formatter();
		for (byte b : hash) {
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

	/**
	 * 把二进制转化为十六进制 推荐
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byte2hex(final byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex);
		}
		return sign.toString();
	}

	public static StringBuilder formatSortParams(Map<?, ?> map) {
		return formatSortParams(map, DEFAULT_CONCAT);
	}

	public static StringBuilder formatSortParams(Map<?, ?> map, String concat) {
		StringBuilder sb = new StringBuilder();
		if (map != null) {
			Object[] keys = map.keySet().toArray();
			Arrays.sort(keys);
			Object k;
			Object v;
			for (int i = 0; i < keys.length; i++) {
				k = keys[i];
				if (k == null) {
					continue;
				}

				v = map.get(k);
				if (v == null) {
					continue;
				}

				if (concat != null) {
					if (sb.length() > 0) {
						sb.append(concat);
					}
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
	public static String hmacMD5(String data, String secret, String charsetName) {
		try {
			return byte2hex(hmacEncrypt(data, secret, charsetName, "HmacMD5"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 签名方法 对encryptText进行签名
	 * 
	 * @param encryptText
	 *            被签名内容
	 * @param encryptKey
	 *            密钥
	 * @param signType
	 *            签名方式 HmacSHA1 HmacMD5
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws Exception
	 */
	public static byte[] hmacEncrypt(byte[] encryptText, byte[] encryptKey, String signType)
			throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKey secretKey = new SecretKeySpec(encryptKey, signType);
		Mac mac = Mac.getInstance(secretKey.getAlgorithm());
		mac.init(secretKey);
		return mac.doFinal(encryptText);
	}

	/**
	 * 签名方法对 对encryptText进行签名
	 * 
	 * @param encryptText
	 * @param encryptKey
	 * @param charsetName
	 * @param signType
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 */
	public static byte[] hmacEncrypt(String encryptText, String encryptKey, String charsetName, String signType)
			throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		return hmacEncrypt(encryptText.getBytes(charsetName), encryptKey.getBytes(charsetName), signType);
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
	public static byte[] hmacSHA1(String encryptText, String encryptKey, String charsetName) {
		try {
			return hmacEncrypt(encryptText, encryptKey, charsetName, "HmacSHA1");
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] messageDigest(byte[] message, String type) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance(type);
		messageDigest.reset();
		messageDigest.update(message);
		return messageDigest.digest();
	}

	public static byte[] messageDigest(String message, String charsetName, String type)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return messageDigest(message.getBytes(charsetName), type);
	}

	public static byte[] md5(byte[] message) {
		try {
			return messageDigest(message, "MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static String md5(String message, String charsetName) {
		try {
			return byte2hex(messageDigest(message, charsetName, "MD5"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] sha1(byte[] byteArray) {
		try {
			return messageDigest(byteArray, "SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String sha1(String message, String charsetName) {
		try {
			return byte2hex(messageDigest(message, charsetName, "SHA-1"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String base64Encode(String text, String charsetName) {
		try {
			return Base64.encode(text.getBytes(charsetName));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String sign(String text, String charsetName, SignType signType) {
		Assert.notNull(text);
		Assert.notNull(charsetName);
		Assert.notNull(signType);
		switch (signType) {
		case MD5:
			return md5(text, charsetName);
		case BASE64:
			return base64Encode(text, charsetName);
		case MD5_UPPERCASE:
			return md5(text, charsetName).toUpperCase();
		case SHA1:
			return sha1(text, charsetName);
		default:
			throw new NotSupportedException("不支持的签名方式:" + signType);
		}
	}

	public static String sign(String text, String charsetName, SignType... signType) {
		Assert.notEmpty(signType);
		String sign = text;
		for (SignType type : signType) {
			sign = sign(sign, charsetName, type);
		}
		return sign;
	}
}
