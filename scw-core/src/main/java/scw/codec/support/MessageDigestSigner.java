package scw.codec.support;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import scw.codec.AbstractSigner;
import scw.codec.CodecException;
import scw.codec.EncodeException;

public class MessageDigestSigner extends AbstractSigner<byte[], byte[]>{
	protected final String algorithm;
	
	public MessageDigestSigner(String algorithm){
		this.algorithm = algorithm;
	}
	
	public MessageDigest getMessageDigest(){
		return getMessageDigest(algorithm);
	}
	
	public byte[] encode(byte[] source) throws EncodeException {
		MessageDigest messageDigest = getMessageDigest();
		messageDigest.reset();
		messageDigest.update(source);
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
}
