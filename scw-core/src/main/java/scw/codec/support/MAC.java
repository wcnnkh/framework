package scw.codec.support;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import scw.codec.CodecException;
import scw.codec.EncodeException;

/**
 * 
 * HmacMD5<br/>
 * HmacSHA1<br/>
 * HmacSHA256<br/>
 * 
 * @see Mac
 * 
 * @author shuchaowen
 *
 */
public class MAC extends AbstractByteSigner{
	private final SecretKey secretKey;
	
	public MAC(SecretKey secretKey){
		this.secretKey = secretKey;
	}
	
	public MAC(String algorithm, byte[] secretKey){
		this(getSecretKey(algorithm, secretKey));
	}
	
	public Mac getMac(){
		return getMac(secretKey.getAlgorithm());
	}
	
	public byte[] encode(byte[] source) throws EncodeException {
		Mac mac = getMac();
		try {
			mac.init(secretKey);
		} catch (InvalidKeyException e) {
			throw new CodecException(e);
		}
		return mac.doFinal(source);
	}

	public boolean verify(byte[] source, byte[] encode) throws CodecException {
		byte[] target = encode(source);
		return Arrays.equals(target, encode);
	}

	public static Mac getMac(String algorithm){
		try {
			return Mac.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CodecException(algorithm, e);
		}
	}
	
	public static SecretKey getSecretKey(String algorithm, byte[] secretKey){
		return new SecretKeySpec(secretKey, algorithm);
	}

}
