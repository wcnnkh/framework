package scw.application.consumer;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.core.Consumer;
import scw.core.PropertyFactory;
import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.PropertyMapper;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringParse;
import scw.core.utils.XMLUtils;
import scw.logger.LoggerUtils;

public class XmlConsumerFactory implements ConsumerFactory {
	private Map<String, AmqpConfig> amqpMap = new HashMap<String, AmqpConfig>();

	public XmlConsumerFactory(final BeanFactory beanFactory,
			PropertyFactory propertyFactory, String xmlPath) {
		if (ResourceUtils.isExist(xmlPath)) {
			NodeList nodeList = XmlBeanUtils.getRootNodeList(xmlPath);
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node node = nodeList.item(i);
				if (node == null) {
					continue;
				}

				if (!node.getNodeName().startsWith("consumer:")) {
					continue;
				}

				String name = XMLUtils.getNodeAttributeValue(propertyFactory,
						node, "name");
				if (exists(name)) {
					throw new AlreadyExistsException(name + "消费者已经存在");
				}

				if (node.getNodeName().equals("consumer:amqp")) {
					amqpMap.put(name, XMLUtils
							.newInstanceLoadAttributeBySetter(AmqpConfig.class,
									propertyFactory, node,
									new PropertyMapper<String>() {

										public Object mapper(String name,
												String value, Type type)
												throws Exception {
											if (name.equals("exchange")) {
												return beanFactory
														.getInstance(value);
											}

											return StringParse.defaultParse(
													value, type);
										}
									}));
				}
			}
		}
	}

	private boolean exists(String name) {
		return amqpMap.containsKey(name);
	}

	@SuppressWarnings("unchecked")
	public void bindConsumer(String name, Consumer<?> consumer) {
		if (!exists(name)) {
			LoggerUtils.warn(getClass(), "无法绑定{}消费者", name);
		}

		if (amqpMap.containsKey(name)) {
			AmqpConfig config = amqpMap.get(name);
			config.getExchange().bindConsumer(config, consumer);
		}
	}

}
