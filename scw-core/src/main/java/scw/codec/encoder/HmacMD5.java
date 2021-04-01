package scw.codec.encoder;



public class HmacMD5 extends MAC {
	public static final String ALGORITHM = "HmacMD5";
	
	public HmacMD5(byte[] secretKey) {
		super(ALGORITHM, secretKey);
	}
}
