package io.basc.framework.codec.support;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.codec.MultipleCodec;
import io.basc.framework.core.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class URLCodec implements MultipleCodec<String>{
	public static final URLCodec UTF_8 = new URLCodec(Constants.UTF_8_NAME);
	private final String charsetName;
	
	public URLCodec(String charsetName){
		this.charsetName = charsetName;
	}
	
	public URLCodec(Charset charset){
		this(charset.name());
	}
	
	public String encode(String source) throws EncodeException {
		try {
			return URLEncoder.encode(source, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new EncodeException(source, e);
		}
	}

	public String decode(String source) throws DecodeException {
		try {
			return URLDecoder.decode(source, charsetName);
		} catch (UnsupportedEncodingException e) {
			throw new DecodeException(source, e);
		}
	}

}
