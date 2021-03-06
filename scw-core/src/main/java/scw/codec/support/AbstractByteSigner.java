package scw.codec.support;

import scw.codec.AbstractSigner;
import scw.codec.Signer;

public abstract class AbstractByteSigner extends AbstractSigner<byte[], byte[]> {

	public Signer<byte[], String> toBase64() {
		return to(Base64.DEFAULT);
	}

	public Signer<byte[], String> toHex() {
		return to(ByteHexCodec.DEFAULT);
	}

}
