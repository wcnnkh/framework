package scw.common.io.encoder;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import scw.common.io.Encoder;

public class StringEncoder implements Encoder<String>{
	private final Charset charset;
	
	public StringEncoder(Charset charset){
		this.charset = charset;
	}
	
	public StringEncoder(String charsetName){
		this(Charset.forName(charsetName));
	}
	
	public void encode(OutputStream out, String data) throws IOException {
		if(data != null){
			out.write(data.getBytes(charset));
		}
	}

}
