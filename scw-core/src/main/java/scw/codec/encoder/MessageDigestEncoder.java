package scw.codec.encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import scw.codec.CodecException;
import scw.codec.EncodeException;
import scw.codec.MultipleEncoder;

public class MessageDigestEncoder implements BytesEncoder<byte[]>, MultipleEncoder<byte[]>{
	protected final String algorithm;
	private byte[] secretKey;
	
	public MessageDigestEncoder(String algorithm){
		this.algorithm = algorithm;
	}
	
	public MessageDigest getMessageDigest(){
		return getMessageDigest(algorithm);
	}
	
	public MessageDigestEncoder wrapperSecretKey(byte[] secretKey){
		MessageDigestEncoder signer = new MessageDigestEncoder(algorithm);
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
