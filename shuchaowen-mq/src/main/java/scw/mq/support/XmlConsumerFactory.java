package scw.mq.support;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.core.instance.InstanceUtils;
import scw.core.parameter.annotation.DefaultValue;
import scw.core.reflect.PropertyMapper;
import scw.core.utils.XMLUtils;
import scw.io.resource.ResourceUtils;
import scw.lang.AlreadyExistsException;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mq.ConsumerFactory;
import scw.util.queue.Consumer;
import scw.util.value.ValueUtils;
import scw.util.value.property.PropertyFactory;

public class XmlConsumerFactory implements ConsumerFactory {
	private static Logger logger = LoggerUtils.getLogger(XmlConsumerFactory.class);

	private Map<String, AmqpConfig> amqpMap = new HashMap<String, AmqpConfig>();

	public XmlConsumerFactory(final BeanFactory beanFactory, PropertyFactory propertyFactory,
			@DefaultValue("consumer.xml") String xmlPath) {
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
					amqpMap.put(name, XMLUtils.newInstanceLoadAttributeBySetter(InstanceUtils.NO_ARGS_INSTANCE_FACTORY, AmqpConfig.class, propertyFactory, node,
							new PropertyMapper<String>() {

								public Object mapper(String name, String value, Type type) throws Exception {
									if (name.equals("exchange")) {
										return beanFactory.getInstance(value);
									}

									return ValueUtils.parse(value, type);
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
			logger.warn("无法绑定{}消费者", name);
		}

		if (amqpMap.containsKey(name)) {
			AmqpConfig config = amqpMap.get(name);
			config.getExchange().bindConsumer(config, consumer);
		}
	}

}
