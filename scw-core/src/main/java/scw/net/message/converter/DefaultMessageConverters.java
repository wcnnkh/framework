package scw.net.message.converter;

import scw.convert.ConversionService;
import scw.lang.Nullable;
import scw.net.message.multipart.FileItemParser;
import scw.net.message.multipart.MultipartMessageWriter;
import scw.net.message.multipart.apache.MultipartMessageConverter;

public class DefaultMessageConverters extends MessageConverters {
	
	public DefaultMessageConverters(ConversionService conversionService, @Nullable FileItemParser fileItemParser) {
		getMessageConverters().add(new JsonMessageConverter());
		getMessageConverters().add(
				new StringMessageConverter(conversionService));
		getMessageConverters().add(new ByteArrayMessageConverter());
		getMessageConverters().add(new XmlMessageConverter(conversionService));
		getMessageConverters().add(new HttpFormMessageConveter());
		getMessageConverters().add(new MultipartMessageWriter());
		getMessageConverters().add(new ResourceMessageConverter());
		if(fileItemParser != null){
			getMessageConverters().add(new MultipartMessageConverter(fileItemParser));
		}
	}
}
