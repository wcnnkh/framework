package scw.codec.support;


public class AES extends SymmetricCodec{
	public static final String ALGORITHM = "AES";
	
	public AES(byte[] secreKey, byte[] ivKey){
		this("PKCS7Padding", secreKey, ivKey);
	}
	
	public AES(String padding, byte[] secreKey, byte[] ivKey) {
		super(ALGORITHM, padding, secreKey, secreKey);
	}
	
	public static AES createNoPaddingCodec(byte[] secreKey, byte[] ivKey){
		return new AES("NoPadding", secreKey, ivKey);
	}
}
