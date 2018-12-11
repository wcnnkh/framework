package shuchaowen.common.io.decoder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import shuchaowen.common.io.Decoder;
import shuchaowen.common.utils.XUtils;

public class StringLineDecoder implements Decoder<List<String>>{
	private final Charset charset;
	
	public StringLineDecoder(Charset charset){
		this.charset = charset;
	}
	
	public List<String> decode(InputStream in) throws IOException {
		List<String> list = new ArrayList<String>();
		InputStreamReader isr = null;
		BufferedReader br = null;
		String line;
		try {
			isr = new InputStreamReader(in, charset);
			br = new BufferedReader(isr);
			while((line = br.readLine()) != null){
				list.add(line);
			}
		} finally {
			XUtils.close(true, br, isr);
		}
		return list;
	}

}
