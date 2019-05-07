package scw.beans.rpc.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.Constants;
import scw.core.aop.Invoker;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.SignUtils;
import scw.core.utils.StringUtils;

public class DefaultRpcService implements RpcService {
	private static Logger logger = LoggerFactory.getLogger(DefaultRpcService.class);
	private final Map<String, Invoker> invokerRPCMap = new HashMap<String, Invoker>();
	private final String sign;
	private final BeanFactory beanFactory;

	public DefaultRpcService(BeanFactory beanFactory, String sign) {
		this.beanFactory = beanFactory;
		this.sign = sign;
	}

	public void service(InputStream in, OutputStream os) throws Throwable {
		Message message = Constants.DEFAULT_SERIALIZER.deserialize(in);
		if (!rpcAuthorize(message)) {
			throw new RuntimeException("RPC验证失败");
		}

		Invoker invoker = getRPCInvoker(message);
		if (invoker == null) {
			throw new RuntimeException("not found service:" + message.getMessageKey());
		}

		Object obj = invoker.invoke(message.getArgs());
		Constants.DEFAULT_SERIALIZER.serialize(os, obj);
	}

	protected Invoker getRPCInvoker(final Message message) throws NoSuchMethodException, SecurityException {
		Invoker invoker = invokerRPCMap.get(message.getMessageKey());
		if (invoker == null) {
			synchronized (invokerRPCMap) {
				invoker = invokerRPCMap.get(message.getMessageKey());
				if (invoker == null) {
					invoker = BeanUtils.getInvoker(beanFactory, message.getMethodDefinition().getBelongClass(),
							message.getMethod());
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
		String checkSign = SignUtils.md5Str(t + sign, Constants.DEFAULT_CHARSET.name());
		if (t < System.currentTimeMillis() - 10000) {// 如果超过10秒失效
			return false;
		}

		String sign = (String) message.getAttribute("sign");
		if (!checkSign.equals(sign)) {
			return false;
		}
		return true;
	}
}
