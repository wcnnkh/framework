package scw.codec.support;

import scw.codec.Signer;

public interface BytesSigner<D> extends Signer<D, byte[]>{
	default Signer<D, String> toBase64() {
		return to(Base64.DEFAULT);
	}

	default Signer<D, String> toHex() {
		return to(ByteHexCodec.DEFAULT);
	}
}
