package shuchaowen.core.http.rpc;

import java.nio.charset.Charset;
import java.util.List;

import shuchaowen.core.beans.AbstractBeanFactory;
import shuchaowen.core.beans.Bean;
import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.http.rpc.serialization.Serializer;
import shuchaowen.core.util.ClassUtils;

public class HttpRPCBeanFactory extends AbstractBeanFactory {
	private final BeanFactory beanFactory;
	private final String host;
	private final String signStr;
	private final Serializer serializer;
	private final Charset charset;
	private List<String> packageNameList;

	public HttpRPCBeanFactory(BeanFactory beanFactory, String host, String signStr, Serializer serializer,
			Charset charset) {
		this.beanFactory = beanFactory;
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
		return new HttpRPCBean(beanFactory, interfactClass, host, signStr, serializer, charset);
	}

	@Override
	public boolean contains(String name) {
		boolean find = false;
		if (packageNameList != null) {
			for (String packagePrefix : packageNameList) {
				if (name.startsWith(packagePrefix)) {
					find = true;
					break;
				}
			}
		}
		return find || super.contains(name);
	}
}
