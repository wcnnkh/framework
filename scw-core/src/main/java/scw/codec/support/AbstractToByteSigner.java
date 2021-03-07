package scw.codec.support;

import scw.codec.AbstractSigner;
import scw.codec.Signer;

public abstract class AbstractToByteSigner<D> extends AbstractSigner<D, byte[]> {

	public Signer<D, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	public Signer<D, String> toHex() {
		return to(ByteHexCodec.DEFAULT);
	}

}
