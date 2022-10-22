package io.basc.framework.rpc.remote.web;

import java.util.function.Supplier;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.context.Context;
import io.basc.framework.context.ContextPostProcessor;
import io.basc.framework.context.annotation.Provider;
import io.basc.framework.context.xml.XmlBeanUtils;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.http.HttpUtils;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.io.Resource;
import io.basc.framework.io.Serializer;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.rpc.CallableFactory;
import io.basc.framework.rpc.remote.RemoteMessageCodec;
import io.basc.framework.rpc.remote.SignerRemoteMessageCodec;
import io.basc.framework.rpc.support.RemoteCallableBeanDefinition;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.StringUtils;

@Provider
public class HttpRemoteContextPostProcessor implements ContextPostProcessor {
	private static final String TAG_NAME = "http:reference";

	private void resolve(ConfigurableContext context, NodeList rootNodeList) {
		for (int i = 0; i < rootNodeList.getLength(); i++) {
			Node node = rootNodeList.item(i);
			if (node == null) {
				continue;
			}

			if (!TAG_NAME.equals(node.getNodeName())) {
				continue;
			}

			String packageName = XmlBeanUtils.getPackageName(context, node);
			CallableFactorySupplier supplier = new CallableFactorySupplier();
			supplier.config(node, context);
			if (!StringUtils.isEmpty(packageName)) {
				for (Class<?> clz : context.getClassesLoaderFactory().getClassesLoader(packageName,
						(e, m) -> e.getClassMetadata().isInterface())) {
					RemoteCallableBeanDefinition definition = new RemoteCallableBeanDefinition(context, supplier, clz);
					context.registerDefinition(definition);
				}
			}

			NodeList nodeList = node.getChildNodes();
			for (int a = 0; a < nodeList.getLength(); a++) {
				Node n = nodeList.item(a);
				if (n == null) {
					continue;
				}

				String className = DomUtils.getNodeAttributeValue(context, n, "interface").getAsString();
				if (StringUtils.isEmpty(className)) {
					continue;
				}

				Class<?> clz = ClassUtils.getClass(className, context.getClassLoader());
				supplier.config(n, context);
				RemoteCallableBeanDefinition definition = new RemoteCallableBeanDefinition(context, supplier, clz);
				context.registerDefinition(definition);
			}
		}
	}

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		for (Resource resource : context.getConfigurationResources()) {
			if (resource.exists() && resource.getName().endsWith(".xml")) {
				XmlBeanUtils.read(context.getResourceLoader(), resource, (nodeList) -> resolve(context, nodeList));
			}
		}
	}

	private static class CallableFactorySupplier implements Supplier<CallableFactory> {
		private HttpClient httpClient;
		private RemoteMessageCodec codec;
		private String url;

		public void config(Node node, Context context) {
			String address = XmlBeanUtils.getAddress(context, node);
			if (StringUtils.isNotEmpty(address)) {
				this.url = address;
			}

			String serializer = DomUtils.getNodeAttributeValue(context, node, "serializer").getAsString();
			String secretKey = DomUtils.getNodeAttributeValue(context, node, "sign").getAsString();
			if (StringUtils.isNotEmpty(serializer) || StringUtils.isNotEmpty(secretKey)) {
				Serializer ser = StringUtils.isEmpty(serializer) ? null : (Serializer) context.getInstance(serializer);
				codec = new SignerRemoteMessageCodec(ser, secretKey);
			}

			String codecName = DomUtils.getNodeAttributeValue(context, node, "codec").getAsString();
			if (StringUtils.isNotEmpty(codecName)) {
				codec = (RemoteMessageCodec) context.getInstance(codecName);
			}

			String httpClientName = DomUtils.getNodeAttributeValue(context, node, "httpClient").getAsString();
			if (StringUtils.isNotEmpty(httpClientName)) {
				this.httpClient = (HttpClient) context.getInstance(httpClientName);
			}
		}

		public HttpClient getHttpClient() {
			return httpClient == null ? HttpUtils.getHttpClient() : httpClient;
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
						callableFactory = new HttpCallableFactory(getHttpClient(), codec, getUrl());
					}
				}
			}
			return callableFactory;
		}
	}
}
