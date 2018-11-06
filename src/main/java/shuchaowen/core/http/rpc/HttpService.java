package shuchaowen.core.http.rpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.exception.ShuChaoWenRuntimeException;
import shuchaowen.core.http.rpc.serialization.Serializer;
import shuchaowen.core.invoke.Invoker;
import shuchaowen.core.invoke.ReflectInvoker;
import shuchaowen.core.util.Logger;
import shuchaowen.core.util.SignHelp;
import shuchaowen.core.util.StringUtils;

public class HttpService implements Service {
	private volatile Map<String, Invoker> invokerMap = new HashMap<String, Invoker>();
	private BeanFactory beanFactory;
	private Charset charset;
	private String signStr;
	private Serializer serializer;

	public HttpService(BeanFactory beanFactory, String signStr, Serializer serializer, Charset charset) {
		this.beanFactory = beanFactory;
		this.charset = charset;
		this.signStr = signStr;
		this.serializer = serializer;
	}

	private Invoker getInvoker(final Message message) throws NoSuchMethodException, SecurityException {
		Invoker invoker = invokerMap.get(message.getMessageKey());
		if (invoker == null) {
			synchronized (invokerMap) {
				invoker = invokerMap.get(message.getMessageKey());
				if (invoker == null) {
					invoker = new ReflectInvoker(beanFactory, message.getClz(), message.getMethod());
					invokerMap.put(message.getMessageKey(), invoker);
				}
			}
		}
		return invoker;
	}

	private Object call(Message message) throws Throwable {
		if (StringUtils.isNull(signStr)) {// 不校验签名
			Logger.warn("RPC", "Signature verification not opened(未开启签名验证)");
			return true;
		} else {
			long t = (Long) message.getAttribute("t");
			String checkSign = SignHelp.md5Str(t + signStr, charset.name());
			if (t < System.currentTimeMillis() - 10000) {// 如果超过10秒失效
				throw new ShuChaoWenRuntimeException("超时：t=" + t + ",name=" + message.getMessageKey());
			}

			String sign = (String) message.getAttribute("sign");
			if (!checkSign.equals(sign)) {
				throw new ShuChaoWenRuntimeException("签名验证失败：t=" + t + ",sign=" + sign);
			}
		}

		Invoker invoker = getInvoker(message);
		if (invoker == null) {
			throw new ShuChaoWenRuntimeException("not found service:" + message.getMessageKey());
		}

		return invoker.invoke(message.getArgs());
	}

	public void service(InputStream in, OutputStream os) {
		Message message;
		Object obj;
		try {
			message = serializer.decode(in, Message.class);
			obj = call(message);
		} catch (Throwable e) {
			throw new ShuChaoWenRuntimeException(e);
		}

		try {
			serializer.encode(os, obj);
		} catch (IOException e) {
			throw new ShuChaoWenRuntimeException(e);
		}
	}
}
