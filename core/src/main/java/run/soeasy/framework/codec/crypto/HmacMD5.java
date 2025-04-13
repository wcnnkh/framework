package run.soeasy.framework.codec.crypto;

public class HmacMD5 extends MAC {
	public static final String ALGORITHM = "HmacMD5";

	public HmacMD5(byte[] secretKey) {
		super(ALGORITHM, secretKey);
	}

	protected HmacMD5(HmacMD5 hmacMD5) {
		super(hmacMD5);
	}

	@Override
	public HmacMD5 clone() {
		return new HmacMD5(this);
	}
}
