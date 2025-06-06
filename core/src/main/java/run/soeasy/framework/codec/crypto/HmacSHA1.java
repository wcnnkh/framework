package run.soeasy.framework.codec.crypto;

public class HmacSHA1 extends MAC {
	public static final String ALGORITHM = "HmacSHA1";

	public HmacSHA1(byte[] secretKey) {
		super(ALGORITHM, secretKey);
	}

	protected HmacSHA1(HmacSHA1 hmacSHA1) {
		super(hmacSHA1);
	}

	@Override
	public HmacSHA1 clone() {
		return new HmacSHA1(this);
	}
}
