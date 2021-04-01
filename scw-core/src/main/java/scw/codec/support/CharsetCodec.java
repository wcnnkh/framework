package scw.codec.support;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import scw.codec.DecodeException;
import scw.codec.EncodeException;
import scw.codec.Encoder;
import scw.codec.encoder.HmacMD5;
import scw.codec.encoder.HmacSHA1;
import scw.core.Constants;

/**
 * 使用指定字符集进行编解码<br/>
 * 
 * @author shuchaowen
 *
 */
public class CharsetCodec implements BytesCodec<String> {
	public static final CharsetCodec UTF_8 = new CharsetCodec(Constants.UTF_8);
	
	public static final CharsetCodec ISO_8859_1 = new CharsetCodec(Constants.ISO_8859_1);
	
	public static final CharsetCodec US_ASCII = new CharsetCodec(Constants.US_ASCII);

	private final Object charset;

	public CharsetCodec(String charsetName) {
		this.charset = charsetName;
	}

	public CharsetCodec(Charset charset) {
		this.charset = charset;
	}

	public String getCharsetName() {
		if (charset instanceof Charset) {
			return ((Charset) charset).name();
		} else {
			return (String) charset;
		}
	}

	public Charset getCharset() {
		if (charset instanceof Charset) {
			return (Charset) charset;
		} else {
			return Charset.forName((String) charset);
		}
	}

	public byte[] encode(String source) throws EncodeException {
		if(source == null){
			return null;
		}
		
		try {
			if(charset instanceof Charset){
				return source.getBytes((Charset)charset);
			}else{
				return source.getBytes((String)charset);
			}
		} catch (UnsupportedEncodingException e) {
			throw new EncodeException("charset=" + charset + ", source="
					+ source, e);
		}
	}

	public String decode(byte[] source) throws DecodeException {
		if(source == null){
			return null;
		}
		
		try {
			if(charset instanceof Charset){
				return new String(source, (Charset)charset);
			}else{
				return new String(source, (String)charset);
			}
		} catch (UnsupportedEncodingException e) {
			throw new EncodeException("charset=" + charset + ", source="
					+ source, e);
		}
	}
	
	public Encoder<String, String> toHmacMD5(String secretKey){
		return toEncoder(new HmacMD5(encode(secretKey)).toEncoder(HexCodec.DEFAULT));
	}
	
	public Encoder<String, String> toHmacSHA1(String secretKey){
		return toEncoder(new HmacSHA1(encode(secretKey)).toEncoder(HexCodec.DEFAULT));
	}
}
