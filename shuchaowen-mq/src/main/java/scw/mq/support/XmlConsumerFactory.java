package scw.mq.support;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.core.parameter.annotation.DefaultValue;
import scw.io.ResourceUtils;
import scw.lang.AlreadyExistsException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mq.ConsumerFactory;
import scw.util.queue.Consumer;
import scw.util.value.property.PropertyFactory;
import scw.xml.XMLUtils;

public class XmlConsumerFactory implements ConsumerFactory {
	private static Logger logger = LoggerUtils.getLogger(XmlConsumerFactory.class);

	private Map<String, AmqpConfig> amqpMap = new HashMap<String, AmqpConfig>();

	public XmlConsumerFactory(final BeanFactory beanFactory, PropertyFactory propertyFactory,
			@DefaultValue("consumer.xml") String xmlPath) throws Exception {
		if (ResourceUtils.getResourceOperations().isExist(xmlPath)) {
			NodeList nodeList = XmlBeanUtils.getRootNodeList(xmlPath);
			for (int i = 0, size = nodeList.getLength(); i < size; i++) {
				Node node = nodeList.item(i);
				if (node == null) {
					continue;
				}

				if (!node.getNodeName().startsWith("consumer:")) {
					continue;
				}

				String name = XMLUtils.getNodeAttributeValue(propertyFactory, node, "name");
				if (exists(name)) {
					throw new AlreadyExistsException(name + "消费者已经存在");
				}

				if (node.getNodeName().equals("consumer:amqp")) {
					XmlAmqpConfigMapper mapper = new XmlAmqpConfigMapper(beanFactory, propertyFactory, node);
					AmqpConfig amqpConfig = mapper.mapping(AmqpConfig.class, null);
					amqpMap.put(name, amqpConfig);
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
			logger.warn("无法绑定{}消费者", name);
		}

		if (amqpMap.containsKey(name)) {
			AmqpConfig config = amqpMap.get(name);
			config.getExchange().bindConsumer(config, consumer);
		}
	}

}
