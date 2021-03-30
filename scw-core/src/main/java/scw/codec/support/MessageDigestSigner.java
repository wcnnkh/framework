package scw.codec.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import scw.codec.CodecException;
import scw.codec.EncodeException;

public class MessageDigestSigner implements BytesSigner<byte[]>{
	protected final String algorithm;
	private byte[] secretKey;
	
	public MessageDigestSigner(String algorithm){
		this.algorithm = algorithm;
	}
	
	public MessageDigest getMessageDigest(){
		return getMessageDigest(algorithm);
	}
	
	public MessageDigestSigner wrapperSecretKey(byte[] secretKey){
		MessageDigestSigner signer = new MessageDigestSigner(algorithm);
		signer.secretKey = secretKey;
		return signer;
	}
	
	public byte[] encode(byte[] source) throws EncodeException {
		MessageDigest messageDigest = getMessageDigest();
		messageDigest.reset();
		
		if(secretKey == null){
			messageDigest.update(source);
		}else{
			byte[] secretSource = Arrays.copyOf(source, source.length + secretKey.length);
			System.arraycopy(secretKey, 0, secretSource, source.length, secretKey.length);
			messageDigest.update(secretSource);
		}
		return messageDigest.digest();
	}

	public boolean verify(byte[] source, byte[] encode) throws CodecException {
		byte[] sourceEncode = encode(source);
		return Arrays.equals(sourceEncode, encode);
	}

	public static MessageDigest getMessageDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new CodecException(algorithm, e);
		}
	}
	
	@Override
	public String toString() {
		return algorithm;
	}
}
