package scw.net.message.converter.support;

import scw.core.instance.annotation.Configuration;
import scw.net.message.converter.ByteArrayMessageConverter;
import scw.net.message.converter.HttpFormMessageConveter;
import scw.net.message.converter.JsonMessageConverter;
import scw.net.message.converter.MultiMessageConverter;
import scw.net.message.converter.StringMessageConverter;
import scw.net.message.converter.XmlMessageConverter;

//此处使用max_value是因为默认的MultiMessageConverter实现是add默认为addFirst行为
@Configuration(order=Integer.MAX_VALUE)
public class AllMessageConverter extends MultiMessageConverter {
	private static final long serialVersionUID = 1L;

	public AllMessageConverter() {
		add(new JsonMessageConverter());
		add(new StringMessageConverter());
		add(new ByteArrayMessageConverter());
		add(new XmlMessageConverter());
		add(new HttpFormMessageConveter());
	}
}
