package run.soeasy.framework.messaging.convert.support;

import java.util.Arrays;

import run.soeasy.framework.messaging.MediaType;

public class DocumentMessageConverter extends TextMessageConverter {

	public DocumentMessageConverter() {
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML,
				MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_RSS_XML));
	}
}
