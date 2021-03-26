package scw.rpc.remote.web;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.xml.XmlBeanFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.context.annotation.Provider;
import scw.core.annotation.AnnotationUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.dom.DomUtils;
import scw.http.client.ClientHttpRequestFactory;
import scw.http.client.SimpleClientHttpRequestFactory;
import scw.io.Serializer;
import scw.lang.NotSupportedException;
import scw.rpc.CallableFactory;
import scw.rpc.remote.RemoteMessageCodec;
import scw.rpc.remote.SignerRemoteMessageCodec;
import scw.rpc.support.RemoteCallableBeanDefinition;
import scw.util.Supplier;

@Provider
public class HttpRemoteBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static final String TAG_NAME = "http:reference";

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		if (beanFactory instanceof XmlBeanFactory) {
			NodeList rootNodeList = ((XmlBeanFactory) beanFactory).getNodeList();
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
				CallableFactory callableFactory = supplier.get();
				if (!StringUtils.isEmpty(packageName)) {
					for (Class<?> clz : beanFactory.getClassesLoader(packageName)) {
						if (!clz.isInterface() || AnnotationUtils.isIgnore(clz)) {
							continue;
						}

						RemoteCallableBeanDefinition definition = new RemoteCallableBeanDefinition(beanFactory, callableFactory, clz);
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
					RemoteCallableBeanDefinition definition = new RemoteCallableBeanDefinition(beanFactory, callableFactory, clz);
					beanFactory.registerDefinition(definition);
				}
			}
		}
	}
	
	private static class CallableFactorySupplier implements Supplier<CallableFactory>{
		private ClientHttpRequestFactory requestFactory;
		private RemoteMessageCodec codec;
		private String url;
		
		public void config(Node node, BeanFactory beanFactory){
			String address = XmlBeanUtils.getAddress(beanFactory.getEnvironment(), node);
			if(StringUtils.isNotEmpty(address)){
				this.url = address;
			}

			String serializer = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "serializer");
			String secretKey = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "sign");
			if(StringUtils.isNotEmpty(serializer) || StringUtils.isNotEmpty(secretKey)){
				Serializer ser = StringUtils.isEmpty(serializer) ? null
						: (Serializer) beanFactory.getInstance(serializer);
				codec = new SignerRemoteMessageCodec(ser, secretKey);
			}
			
			String codecName = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "codec");
			if(StringUtils.isNotEmpty(codecName)){
				codec = beanFactory.getInstance(codecName);
			}
			
			String connectionFactoryName = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "connectionFactory");
			if(StringUtils.isNotEmpty(connectionFactoryName)){
				this.requestFactory = beanFactory.getInstance(connectionFactoryName);
			}
		}
		
		public ClientHttpRequestFactory getRequestFactory() {
			return requestFactory == null? SimpleClientHttpRequestFactory.INSTANCE:requestFactory;
		}

		public String getUrl() {
			return url;
		}

		public CallableFactory get() {
			if(codec == null){
				throw new NotSupportedException("未配置codec, 请检查codec/secretKey是否配置");
			}
			return new HttpCallableFactory(getRequestFactory(), codec, getUrl());
		}
	}
}
