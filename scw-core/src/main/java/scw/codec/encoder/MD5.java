package scw.codec.encoder;

import scw.codec.Encoder;
import scw.codec.support.HexCodec;


public class MD5 extends MessageDigestEncoder {
	public static final String ALGORITHM = "MD5";
	
	public static final Encoder<byte[], String> DEFAULT = new MD5().toEncoder(HexCodec.DEFAULT);

	public MD5() {
		super(ALGORITHM);
	}

}
