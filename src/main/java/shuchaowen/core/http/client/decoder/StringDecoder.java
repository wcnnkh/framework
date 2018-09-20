package shuchaowen.core.http.client.decoder;

import java.io.IOException;
import java.io.InputStream;

import shuchaowen.core.util.IOUtils;

public class StringDecoder implements Decoder<String>{
	private String charsetName;
	
	public StringDecoder(){
		this("UTF-8");
	}
	
	public StringDecoder(String charsetName){
		this.charsetName = charsetName;
	}

	public String decode(InputStream in) throws IOException {
		return IOUtils.readerContent(in, charsetName).toString();
	}

}
