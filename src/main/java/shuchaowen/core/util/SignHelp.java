package shuchaowen.core.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SignHelp {
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
	 * 把二进制转化为十六进制
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
	 * 把二进制转化为十六进制  推荐
	 * @param bytes
	 * @return
	 */
	public static String byte2hex(byte[] bytes) {
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
	
	/**
	 * HmacMD5加密
	 * @param data
	 * @param secret
	 * @return
	 * @throws IOException
	 */
	public static byte[] HmacMD5(String data, String secret, String charsetName) {
		try {
			return HmacEncrypt(data.getBytes(charsetName), secret.getBytes(charsetName), "HmacMD5");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return null;
	}
	
	 /**  
     * 签名方法对对encryptText进行签名  
     * @param encryptText 被签名内容
     * @param encryptKey  密钥  
     * @param signType 签名方式    HmacSHA1   HmacMD5
     * @return  
     * @throws Exception  
     */ 
	 public static byte[] HmacEncrypt(byte[] encryptText, byte[] encryptKey, String signType)     
	    {       
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
     * @param encryptText 被签名的字符串  
     * @param encryptKey  密钥  
     * @return  
     * @throws Exception  
     */ 
    public static byte[] HmacSHA1(String encryptText, String encryptKey, String charsetName)     
    {       
    	try {
			return HmacEncrypt(encryptText.getBytes(charsetName), encryptKey.getBytes(charsetName), "HmacSHA1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return null;
    }
	
	public static byte[] md5(byte[] byteArray) throws NoSuchAlgorithmException{
		return MessageDigest.getInstance("MD5").digest(byteArray);
	}
	
	public static String md5Str(byte[] byteArray) throws NoSuchAlgorithmException{
		return byte2hex(md5(byteArray));
	}
	
	public static String md5UpperStr(String str, String charsetName){
		try {
			return md5Str(str.getBytes(charsetName)).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] md5Tobyte(String str, String charsetName){
		try {
			return md5(str.getBytes(charsetName));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String md5Str(String str, String charsetName){
		try {
			return md5Str(str.getBytes(charsetName));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
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
			return byteToHex(crypt.digest());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
