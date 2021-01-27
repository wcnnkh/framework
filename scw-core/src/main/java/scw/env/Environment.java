package scw.env;

import java.nio.charset.Charset;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.convert.resolve.ResourceResolver;
import scw.event.Observable;

public interface Environment extends BasicEnvironment,
		EnvironmentResourceLoader, ConversionService, ResourceResolver {
	public static final String CHARSET_PROPERTY = "charset.name";
	
	String getCharsetName();
	
	Observable<String> getObservableCharsetName();

	Charset getCharset();
	
	Observable<Charset> getObservableCharset();
	
	Object resolveResource(String location, TypeDescriptor targetType);
}