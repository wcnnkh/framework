package scw.compatible;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public interface StringOperations {
	String createString(byte[] bytes, String charsetName) throws UnsupportedEncodingException;
	
	String createString(byte[] bytes, Charset charset);
	
	byte[] getBytes(String text, String charsetName) throws UnsupportedEncodingException;
	
	byte[] getBytes(String text, Charset charset);
}
