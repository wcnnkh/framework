package scw.rpc.web;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scw.beans.BeanFactory;
import scw.beans.BeanFactoryPostProcessor;
import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.beans.xml.XmlBeanFactory;
import scw.beans.xml.XmlBeanUtils;
import scw.context.annotation.Provider;
import scw.core.Constants;
import scw.core.annotation.AnnotationUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.dom.DomUtils;
import scw.http.HttpUtils;
import scw.http.client.HttpConnectionFactory;
import scw.instance.InstanceException;
import scw.io.Serializer;
import scw.rpc.CallableFactory;
import scw.rpc.messageing.RemoteMessageCodec;
import scw.rpc.messageing.support.DefaultRemoteMessageCodec;
import scw.rpc.support.RemoteCallableBeanDefinition;
import scw.util.Supplier;

@Provider
public class HttpRemoteBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	private static final String RPC_HTTP_SIGN_NAME = "rpc.http.sign";
	private static final String RPC_HTTP_PATH = "mvc.http.rpc-path";

	private static final String WEB_RPC_SECRET_KEY = "web.rpc.secret.key";
	private static final String WEB_RPC_PATH = "web.rpc.path";
	private static final String DEFAULT_RPC_PATH = "/rpc";
	
	private static final String TAG_NAME = "http:reference";

	public void postProcessBeanFactory(ConfigurableBeanFactory beanFactory)
			throws BeansException {
		//service
		RemoteHttpServiceHandlerDefinition httpServiceDefinition = new RemoteHttpServiceHandlerDefinition(
				beanFactory);
		if (!beanFactory.containsDefinition(httpServiceDefinition.getId())) {
			beanFactory.registerDefinition(httpServiceDefinition);
		}
		
		//reference
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
					
					String className = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "interface");
					if (StringUtils.isEmpty(className)) {
						continue;
					}
					
					Class<?> clz = ClassUtils.getClass(className, beanFactory.getClassLoader());
					supplier.config(node, beanFactory);
					RemoteCallableBeanDefinition definition = new RemoteCallableBeanDefinition(beanFactory, callableFactory, clz);
					beanFactory.registerDefinition(definition);
				}
			}
		}
	}
	
	private static class CallableFactorySupplier implements Supplier<CallableFactory>{
		private HttpConnectionFactory connectionFactory;
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
				codec = new DefaultRemoteMessageCodec(ser, secretKey);
			}
			
			String codecName = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "codec");
			if(StringUtils.isNotEmpty(codecName)){
				codec = beanFactory.getInstance(codecName);
			}
			
			String connectionFactoryName = DomUtils.getNodeAttributeValue(beanFactory.getEnvironment(), node, "connectionFactory");
			if(StringUtils.isNotEmpty(connectionFactoryName)){
				this.connectionFactory = beanFactory.getInstance(connectionFactoryName);
			}
		}
		
		public HttpConnectionFactory getConnectionFactory() {
			return connectionFactory == null? HttpUtils.getHttpClient():connectionFactory;
		}

		public RemoteMessageCodec getCodec() {
			if(codec == null){
				return new DefaultRemoteMessageCodec();
			}
			return codec;
		}

		public String getUrl() {
			return url;
		}

		public CallableFactory get() {
			return new HttpCallableFactory(getConnectionFactory(), getCodec(), getUrl());
		}
	}

	private static class RemoteHttpServiceHandlerDefinition extends
			DefaultBeanDefinition {

		public RemoteHttpServiceHandlerDefinition(BeanFactory beanFactory) {
			super(beanFactory, RemoteHttpServiceHandler.class);
		}

		@Override
		public boolean isInstance() {
			return beanFactory.getEnvironment().containsKey(RPC_HTTP_SIGN_NAME)
					|| beanFactory.getEnvironment().containsKey(
							WEB_RPC_SECRET_KEY);
		}

		@Override
		public Object create() throws InstanceException {
			RemoteMessageCodec messageCodec;
			if (beanFactory.isInstance(RemoteMessageCodec.class)) {
				messageCodec = beanFactory
						.getInstance(RemoteMessageCodec.class);
			} else {
				String secretKey = beanFactory.getEnvironment().getValue(
						RPC_HTTP_SIGN_NAME,
						String.class,
						beanFactory.getEnvironment().getString(
								WEB_RPC_SECRET_KEY));
				byte[] secretKeyBytes = StringUtils.getStringOperations()
						.getBytes(secretKey, Constants.UTF_8);
				messageCodec = new DefaultRemoteMessageCodec(secretKeyBytes);
			}

			String path = beanFactory.getEnvironment().getValue(
					RPC_HTTP_PATH,
					String.class,
					beanFactory.getEnvironment().getValue(WEB_RPC_PATH,
							String.class, DEFAULT_RPC_PATH));
			return new RemoteHttpServiceHandler(beanFactory, messageCodec, path);
		}
	}

}
