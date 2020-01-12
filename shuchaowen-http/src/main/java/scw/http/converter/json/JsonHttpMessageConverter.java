package scw.http.converter.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import scw.http.HttpHeaders;
import scw.http.HttpInputMessage;
import scw.http.HttpOutputMessage;
import scw.http.MediaType;
import scw.http.converter.AbstractGenericHttpMessageConverter;
import scw.http.converter.HttpMessageNotReadableException;
import scw.http.converter.HttpMessageNotWritableException;
import scw.io.IOUtils;
import scw.json.JSONUtils;

public class JsonHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object>{
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	public JsonHttpMessageConverter(){
		super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
		setDefaultCharset(DEFAULT_CHARSET);
	}
	
	private Charset getCharset(HttpHeaders headers) {
		if (headers == null || headers.getContentType() == null || headers.getContentType().getCharset() == null) {
			return DEFAULT_CHARSET;
		}
		return headers.getContentType().getCharset();
	}	
	
	public Object read(Type type, Class<?> contextClass,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		return JSONUtils.parseObject(IOUtils.readContent(inputMessage.getBody(), getCharset(inputMessage.getHeaders()).name()), type);
	}

	@Override
	protected void writeInternal(Object t, Type type,
			HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		IOUtils.write(JSONUtils.toJSONString(t), outputMessage.getBody(), getCharset(outputMessage.getHeaders()).name());
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		return JSONUtils.parseObject(IOUtils.readContent(inputMessage.getBody(), getCharset(inputMessage.getHeaders()).name()), clazz);
	}

}
