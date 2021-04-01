package scw.codec.signer;

import scw.codec.Signer;
import scw.codec.encoder.BytesEncoder;
import scw.codec.support.Base64;
import scw.codec.support.HexCodec;


public interface BytesSigner<D> extends Signer<D, byte[]>, BytesEncoder<D>{
	
	default Signer<D, String> toBase64(){
		return to(Base64.DEFAULT);
	}
	
	default Signer<D, String> toHex(){
		return to(HexCodec.DEFAULT);
	}
}
