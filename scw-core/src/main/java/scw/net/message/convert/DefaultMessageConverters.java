package scw.net.message.convert;

import scw.convert.ConversionService;

public class DefaultMessageConverters extends MessageConverters {
//	private static final FileItemParser FILE_ITEM_PARSER = InstanceUtils.loadService(FileItemParser.class,
//			"scw.net.message.multipart.apache.ApacheFileItemParser");

	public DefaultMessageConverters(ConversionService conversionService) {
		addMessageConverter(new JsonMessageConverter());
		addMessageConverter(new StringMessageConverter(conversionService));
		addMessageConverter(new ByteArrayMessageConverter());
		addMessageConverter(new XmlMessageConverter(conversionService));
		addMessageConverter(new HttpFormMessageConveter());
		addMessageConverter(new MultipartMessageWriter());
		addMessageConverter(new ResourceMessageConverter());
//		if(FILE_ITEM_PARSER != null){
//			addMessageConverter(new MultipartMessageConverter(FILE_ITEM_PARSER));
//		}
	}
}
