package shuchaowen.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public final class IOUtils {
	public static StringBuilder readerContent(Reader in) throws IOException {
		if(in.markSupported()){
			in.mark(0);
		}
		char[] b = new char[1024];
		int len;
		StringBuilder sb = new StringBuilder();
		while ((len = in.read(b)) != -1) {
			sb.append(b, 0, len);
		}
		
		if(in.markSupported()){
			in.reset();
		}
		return sb;
	}
	
	public static List<String> readerContent(BufferedReader in) throws IOException {
		List<String> list = new ArrayList<String>();
		String line;
		while((line = in.readLine()) != null){
			list.add(line);
		}
		return list;
	}

	public static StringBuilder readerContent(InputStream in, String charsetName) throws IOException {
		InputStreamReader inputStreamReader = new InputStreamReader(in, charsetName); 
		StringBuilder sb = readerContent(inputStreamReader);
		inputStreamReader.close();
		return sb;
	}
	
	public static void write(OutputStream os, InputStream is, int buffSize) throws IOException{
		byte[] b = new byte[buffSize];
		int len = 0;
		while((len = is.read(b)) != -1){
			os.write(b, 0, len);
		}
	}
}
