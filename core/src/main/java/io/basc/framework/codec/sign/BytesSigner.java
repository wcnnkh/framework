package io.basc.framework.codec.sign;

import io.basc.framework.codec.Signer;
import io.basc.framework.codec.encode.BytesEncoder;
import io.basc.framework.codec.support.Base64;
import io.basc.framework.codec.support.HexCodec;


public interface BytesSigner<D> extends Signer<D, byte[]>, BytesEncoder<D>{
	
	default Signer<D, String> toBase64(){
		return to(Base64.DEFAULT);
	}
	
	default Signer<D, String> toHex(){
		return to(HexCodec.DEFAULT);
	}
}
