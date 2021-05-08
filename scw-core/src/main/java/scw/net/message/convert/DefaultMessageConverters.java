package scw.net.message.convert;

import scw.convert.ConversionService;
import scw.instance.InstanceUtils;
import scw.instance.ServiceLoaderFactory;
import scw.net.message.multipart.FileItemParser;

public class DefaultMessageConverters extends MessageConverters {
	private static final FileItemParser FILE_ITEM_PARSER = InstanceUtils.loadService(FileItemParser.class,
			"scw.net.message.multipart.apache.ApacheFileItemParser");
	
	public DefaultMessageConverters(ConversionService conversionService) {
		getMessageConverters().add(new JsonMessageConverter());
		getMessageConverters().add(
				new StringMessageConverter(conversionService));
		getMessageConverters().add(new ByteArrayMessageConverter());
		getMessageConverters().add(new XmlMessageConverter(conversionService));
		getMessageConverters().add(new HttpFormMessageConveter());
		getMessageConverters().add(new MultipartMessageWriter());
		getMessageConverters().add(new ResourceMessageConverter());
		if(FILE_ITEM_PARSER != null){
			getMessageConverters().add(new MultipartMessageConverter(FILE_ITEM_PARSER));
		}
	}
	
	public DefaultMessageConverters(ConversionService conversionService, ServiceLoaderFactory serviceLoaderFactory){
		this(conversionService);
		getMessageConverters().addAll(serviceLoaderFactory.getServiceLoader(MessageConverter.class).toList());
	}
}
