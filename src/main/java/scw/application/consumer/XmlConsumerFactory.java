package scw.application.consumer;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.core.Consumer;
import scw.core.PropertiesFactory;
import scw.core.exception.AlreadyExistsException;
import scw.core.logger.LoggerUtils;
import scw.core.reflect.PropertyMapper;
import scw.core.utils.StringUtils;
import scw.core.utils.XMLUtils;

public class XmlConsumerFactory implements ConsumerFactory {
	private Map<String, AmqpConfig> amqpMap = new HashMap<String, AmqpConfig>();

	public XmlConsumerFactory(final BeanFactory beanFactory, PropertiesFactory propertiesFactory, String xmlPath) {
		if (!StringUtils.isEmpty(xmlPath)) {
			NodeList nodeList = XmlBeanUtils.getRootNodeList(xmlPath);
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node node = nodeList.item(i);
				if (node == null) {
					continue;
				}

				if (!node.getNodeName().startsWith("consumer:")) {
					continue;
				}

				String name = XMLUtils.getNodeAttributeValue(propertiesFactory, node, "name");
				if (exists(name)) {
					throw new AlreadyExistsException(name + "消费者已经存在");
				}

				if (node.getNodeName().equals("consumer:amqp")) {
					amqpMap.put(name, XMLUtils.newInstanceLoadAttributeBySetter(AmqpConfig.class, propertiesFactory,
							node, new PropertyMapper<String>() {

								public Object mapper(String name, String value, Class<?> type) throws Exception {
									if (name.equals("name")) {
										return null;
									}

									if (name.equals("exchange")) {
										return beanFactory.get(value);
									}

									return StringUtils.conversion(value, type);
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
