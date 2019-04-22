package scw.beans.rpc.http;

import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import scw.aop.Invoker;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.common.Constants;
import scw.common.utils.IOUtils;
import scw.common.utils.SignUtils;
import scw.common.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class DefaultService implements Service {
	private static Logger logger = LoggerFactory.getLogger(DefaultService.class);
	private final Map<String, Invoker> invokerRPCMap = new HashMap<String, Invoker>();
	private final String sign;
	private final BeanFactory beanFactory;

	public DefaultService(BeanFactory beanFactory, String sign) {
		this.beanFactory = beanFactory;
		this.sign = sign;
	}

	public void service(InputStream in, OutputStream os) throws Throwable {
		Message message = IOUtils.readJavaObject(in);
		if (!rpcAuthorize(message)) {
			throw new RuntimeException("RPC验证失败");
		}

		Invoker invoker = getRPCInvoker(message);
		if (invoker == null) {
			throw new RuntimeException("not found service:" + message.getMessageKey());
		}

		Object obj = invoker.invoke(message.getArgs());
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(obj);
		oos.flush();
	}

	protected Invoker getRPCInvoker(final Message message) throws NoSuchMethodException, SecurityException {
		Invoker invoker = invokerRPCMap.get(message.getMessageKey());
		if (invoker == null) {
			synchronized (invokerRPCMap) {
				invoker = invokerRPCMap.get(message.getMessageKey());
				if (invoker == null) {
					invoker = BeanUtils.getInvoker(beanFactory, message.getClz(), message.getMethod());
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
