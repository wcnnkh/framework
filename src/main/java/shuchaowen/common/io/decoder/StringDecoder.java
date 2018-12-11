package shuchaowen.common.io.decoder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import shuchaowen.common.io.Decoder;

public class StringDecoder implements Decoder<String>{
	private final Charset charset;
	
	public StringDecoder(Charset charset){
		this.charset = charset;
	}
	
	public StringDecoder(String charsetName){
		this(Charset.forName(charsetName));
	}
	
	public String decode(InputStream in) throws IOException {
		char[] b = new char[1024];
		int len;
		StringBuilder sb = new StringBuilder();
		InputStreamReader isr = new InputStreamReader(in, charset);
		try {
			if(isr.markSupported()){
				isr.mark(0);
			}
			
			while ((len = isr.read(b)) != -1) {
				sb.append(b, 0, len);
			}
			
			if(isr.markSupported()){
				isr.reset();
			}
		} finally {
			isr.close();
		}
		return sb.toString();
	}

}
