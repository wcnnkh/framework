package shuchaowen.core.http.rpc;

import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.HashSet;

import shuchaowen.core.beans.AbstractBeanFactory;
import shuchaowen.core.beans.Bean;
import shuchaowen.core.http.rpc.serialization.JavaObjectSerializer;
import shuchaowen.core.http.rpc.serialization.Serializer;
import shuchaowen.core.util.ClassUtils;

public class HttpRPCBeanFactory extends AbstractBeanFactory {
	private final String host;
	private final String signStr;
	private final Serializer serializer;
	private final Charset charset;
	private final HashSet<String> serviceSet = new HashSet<String>();
	
	public HttpRPCBeanFactory(String host, String signStr) {
		this(host, signStr, new JavaObjectSerializer(), Charset.forName("utf-8"));
	}

	public HttpRPCBeanFactory(String host, String signStr, Serializer serializer,
			Charset charset) {
		this.host = host;
		this.signStr = signStr;
		this.serializer = serializer;
		this.charset = charset;
	}

	@Override
	protected Bean newBean(String name) throws Exception {
		if (!contains(name)) {
			return null;
		}

		Class<?> interfactClass = ClassUtils.forName(name);
		return new HttpRPCBean(interfactClass, host, signStr, serializer, charset);
	}

	@Override
	public boolean contains(String name) {
		return serviceSet.contains(name) || super.contains(name);
	}
	
	public void registerService(Class<?>... interfaceClass){
		for(Class<?> clz : interfaceClass){
			serviceSet.add(clz.getName());
		}
	}
	
	public void registerService(String packageName, boolean isInterface){
		for(Class<?> clz : ClassUtils.getClasses(packageName)){
			if(isInterface){
				if(Modifier.isInterface(clz.getModifiers())){
					serviceSet.add(clz.getName());
				}
			}else{
				serviceSet.add(clz.getName());
			}
		}
	}
	
	public void removeService(Class<?>... type){
		for(Class<?> clz : type){
			serviceSet.remove(clz.getName());
		}
	}
}
