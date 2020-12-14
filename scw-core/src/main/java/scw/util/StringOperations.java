package scw.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import scw.core.utils.ClassUtils;

public interface StringOperations {
	static final StringOperations INSTANCE = (StringOperations) (JavaVersion.INSTANCE
			.getMasterVersion() >= 6 ? ClassUtils.createInstance("scw.util.Jdk6StringOperations")
			: new Jdk5StringOperations());
	
	String createString(byte[] bytes, String charsetName) throws UnsupportedEncodingException;
	
	String createString(byte[] bytes, Charset charset);
	
	byte[] getBytes(String text, String charsetName) throws UnsupportedEncodingException;
	
	byte[] getBytes(String text, Charset charset);
}
