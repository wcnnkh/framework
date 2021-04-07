package scw.codec.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import scw.codec.DecodeException;
import scw.codec.EncodeException;
import scw.codec.multiple.MultipleCodec;
import scw.core.Constants;

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
