package scw.codec.support;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import scw.codec.AbstractCodec;
import scw.codec.DecodeException;
import scw.codec.EncodeException;

public class URLCodec extends AbstractCodec<String, String>{
	private final String charsetName;
	
	public URLCodec(String charsetName){
		this.charsetName = charsetName;
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
