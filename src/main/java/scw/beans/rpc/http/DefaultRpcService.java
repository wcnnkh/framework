package scw.beans.rpc.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.core.utils.SignUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.io.Bytes;
import scw.io.serializer.Serializer;
import scw.logger.Logger;
import scw.logger.LoggerUtils;

public class DefaultRpcService implements RpcService {
	private static Logger logger = LoggerUtils.getLogger(DefaultRpcService.class);
	private volatile Map<String, ServiceInvoker> invokerRPCMap = new HashMap<String, ServiceInvoker>();
	private final String sign;
	private final BeanFactory beanFactory;
	private final Serializer serializer;

	public DefaultRpcService(BeanFactory beanFactory, String sign, Serializer serializer) {
		this.beanFactory = beanFactory;
		this.sign = sign;
		this.serializer = serializer;
	}

	public void service(InputStream in, OutputStream os) throws Throwable {
		Message message = serializer.deserialize(in);
		if (!rpcAuthorize(message)) {
			throw new RuntimeException("RPC验证失败");
		}

		ServiceInvoker invoker = getRPCInvoker(message);
		if (invoker == null) {
			throw new RuntimeException("not found service:" + message.getMessageKey());
		}

		Object obj;
		try {
			obj = invoker.invoke(beanFactory.getInstance(message.getMethodDefinition().getBelongClass()),
					message.getArgs());
		} catch (IllegalArgumentException e) {
			logger.warn("参数不一致：{}", message.getMessageKey());
			throw e;
		}
		serializer.serialize(os, obj);
	}

	protected ServiceInvoker getRPCInvoker(final Message message) throws NoSuchMethodException, SecurityException {
		ServiceInvoker invoker = invokerRPCMap.get(message.getMessageKey());
		if (invoker == null) {
			synchronized (invokerRPCMap) {
				invoker = invokerRPCMap.get(message.getMessageKey());
				if (invoker == null) {
					invoker = new ServiceInvoker(message.getMethod());
					if (invoker != null) {
						invokerRPCMap.put(message.getMessageKey(), invoker);
					}
				}
			}
		}
		return invoker;
	}

	/**
	 * RPC权限验证
	 * 
	 * @param message
	 */
	private boolean rpcAuthorize(Message message) {
		if (StringUtils.isNull(sign)) {// 不校验签名
			logger.warn("RPC Signature verification not opened(未开启签名验证)");
			return true;
		}

		long t = (Long) message.getAttribute("t");
		if (t < System.currentTimeMillis() - XTime.ONE_MINUTE) {// 如果超过10秒失效
			return false;
		}
		
		return SignUtils.md5Str(Bytes.string2bytes(t + sign)).equals((String) message.getAttribute("sign"));
	}
}
