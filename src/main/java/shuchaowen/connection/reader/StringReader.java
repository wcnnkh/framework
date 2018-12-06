package shuchaowen.connection.reader;

import java.io.IOException;
import java.io.InputStream;

import shuchaowen.common.utils.IOUtils;
import shuchaowen.connection.Reader;

public class StringReader implements Reader<String>{
	private final String charsetName;
	
	public StringReader(String charsetName){
		this.charsetName = charsetName;
	}
	
	public String reader(InputStream inputStream) throws IOException{
		StringBuilder sb = IOUtils.readerContent(inputStream, charsetName);
		return sb == null? null:sb.toString();
	}

}
