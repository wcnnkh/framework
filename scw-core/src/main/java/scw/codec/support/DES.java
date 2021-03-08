package scw.codec.support;


/**
 * DES
 * 
 * @author shuchaowen
 *
 */
public class DES extends SymmetricCodec {
	public static final String ALGORITHM = "DES";

	public DES(byte[] secretKey, byte[] ivKey) {
		this("PKCS5Padding", secretKey, ivKey);
	}
	
	public DES(String padding, byte[] secretKey, byte[] ivKey){
		super(ALGORITHM, padding, secretKey, ivKey);
	}
}
