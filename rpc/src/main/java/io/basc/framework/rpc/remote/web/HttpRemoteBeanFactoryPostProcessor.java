package io.basc.framework.rpc.remote.web;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeanFactoryPostProcessor;
import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.xml.XmlBeanFactory;
import io.basc.framework.beans.xml.XmlBeanUtils;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.http.client.DefaultHttpClient;
import io.basc.framework.io.Serializer;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.rpc.remote.RemoteMessageCodec;
import io.basc.framework.rpc.remote.SignerRemoteMessageCodec;
import io.basc.framework.rpc.support.RemoteCallableBeanDefinition;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.Supplier;

@Provider
public class HttpRemoteBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static final String TAG_NAME = "http:reference";

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory) throws BeansException {
		if (beanFactory instanceof XmlBeanFactory) {
			((XmlBeanFactory) beanFactory).readConfigurationFile((rootNodeList) -> {
				for (int i = 0; i < rootNodeList.getLength(); i++) {
					Node node = rootNodeList.item(i);
					if (node == null) {
						continue;
					}

					if (!TAG_NAME.equals(node.getNodeName())) {
						continue;
					}

					String packageName = XmlBeanUtils.getPackageName(beanFactory.getEnvironment(), node);
					CallableFactorySupplier supplier = new CallableFactorySupplier();
					supplier.config(node, beanFactory);
					if (!StringUtils.isEmpty(packageName)) {
						for (Class<?> clz : beanFactory.getClassesLoaderFactory().getClassesLoader(packageName)) {
							if (!clz.isInterface() || AnnotatedElementUtils.isIgnore(clz)) {
								continue;
							}

							RemoteCallableBeanDefinition definition = new RemoteCallableBeanDefinition(beanFactory,
									supplier, clz);
							beanFactory.registerDefinition(definition);
						}
					}

					NodeList nodeList = node.getChildNodes();
					for (int a = 0; a < nodeList.getLength(); a++) {
						Node n = nodeList.item(a);
						if (n == null) {
							continue;
						}

						String className = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), n, "interface");
						if (StringUtils.isEmpty(className)) {
							continue;
						}

						Class<?> clz = ClassUtils.getClass(className, beanFactory.getClassLoader());
						supplier.config(n, beanFactory);
						RemoteCallableBeanDefinition definition = new RemoteCallableBeanDefinition(beanFactory,
								supplier, clz);
						beanFactory.registerDefinition(definition);
					}
				}
			});
		}
	}

	private static class CallableFactorySupplier implements Supplier<CallableFactory> {
		private ClientHttpRequestFactory requestFactory;
		private RemoteMessageCodec codec;
		private String url;

		public void config(Node node, BeanFactory beanFactory) {
			String address = XmlBeanUtils.getAddress(beanFactory.getEnvironment(), node);
			if (StringUtils.isNotEmpty(address)) {
				this.url = address;
			}

			String serializer = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "serializer");
			String secretKey = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "sign");
			if (StringUtils.isNotEmpty(serializer) || StringUtils.isNotEmpty(secretKey)) {
				Serializer ser = StringUtils.isEmpty(serializer) ? null
						: (Serializer) beanFactory.getInstance(serializer);
				codec = new SignerRemoteMessageCodec(ser, secretKey);
			}

			String codecName = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "codec");
			if (StringUtils.isNotEmpty(codecName)) {
				codec = beanFactory.getInstance(codecName);
			}

			String connectionFactoryName = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node,
					"connectionFactory");
			if (StringUtils.isNotEmpty(connectionFactoryName)) {
				this.requestFactory = beanFactory.getInstance(connectionFactoryName);
			}
		}

		public ClientHttpRequestFactory getRequestFactory() {
			return requestFactory == null ? DefaultHttpClient.CLIENT_HTTP_REQUEST_FACTORY : requestFactory;
		}

		public String getUrl() {
			return url;
		}

		private volatile CallableFactory callableFactory;

		public CallableFactory get() {
			if (callableFactory == null) {
				synchronized (this) {
					if (callableFactory == null) {
						if (codec == null) {
							throw new NotSupportedException("未配置codec, 请检查codec/secretKey是否配置");
						}
						callableFactory = new HttpCallableFactory(getRequestFactory(), codec, getUrl());
					}
				}
			}
			return callableFactory;
		}
	}
}
