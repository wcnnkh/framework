package scw.rpc.object;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import scw.core.annotation.ParameterName;
import scw.core.instance.InstanceFactory;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.io.Bytes;
import scw.io.serializer.Serializer;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.rpc.RpcConstants;
import scw.rpc.RpcService;
import scw.security.signature.SignatureUtils;

public final class DefaultObjectRpcService implements RpcService, RpcConstants {
	private static Logger logger = LoggerUtils.getLogger(DefaultObjectRpcService.class);
	private volatile Map<String, ObjectServiceInvoker> invokerRPCMap = new HashMap<String, ObjectServiceInvoker>();
	private final String sign;
	private final InstanceFactory instanceFactory;
	private final Serializer serializer;

	public DefaultObjectRpcService(InstanceFactory instanceFactory, @ParameterName(RPC_HTTP_SIGN_NAME) String sign,
			Serializer serializer) {
		this.instanceFactory = instanceFactory;
		this.sign = sign;
		this.serializer = serializer;
	}

	public void service(InputStream in, OutputStream os) throws Throwable {
		ObjectRpcRequestMessage objectRpcRequestMessage = serializer.deserialize(in);
		if (!rpcAuthorize(objectRpcRequestMessage)) {
			throw new RuntimeException("RPC验证失败");
		}

		ObjectServiceInvoker invoker = getRPCInvoker(objectRpcRequestMessage);
		if (invoker == null) {
			throw new RuntimeException("not found service:" + objectRpcRequestMessage.getMessageKey());
		}

		boolean responseThrowable = StringUtils
				.parseBoolean(objectRpcRequestMessage.getAttribute(RPC_REQUEST_MESSAGE_RESPONSE_THROWABLE));
		ObjectRpcResponseMessage response = new ObjectRpcResponseMessage();
		try {
			response.setResponse(invoker.invoke(
					instanceFactory.getInstance(objectRpcRequestMessage.getMethodDefinition().getBelongClass()),
					objectRpcRequestMessage.getArgs()));
		} catch (Throwable e) {
			if (e instanceof IllegalArgumentException) {
				logger.warn("参数不一致：{}", objectRpcRequestMessage.getMessageKey());
			}
			response.setThrowable(e);
			if (!responseThrowable) {
				throw e;
			}
		}

		if (responseThrowable) {
			serializer.serialize(os, response);
		} else if (response.getResponse() != null) {
			serializer.serialize(os, response.getResponse());
		}
	}

	protected ObjectServiceInvoker getRPCInvoker(final ObjectRpcRequestMessage objectRpcRequestMessage)
			throws NoSuchMethodException, SecurityException {
		ObjectServiceInvoker invoker = invokerRPCMap.get(objectRpcRequestMessage.getMessageKey());
		if (invoker == null) {
			synchronized (invokerRPCMap) {
				invoker = invokerRPCMap.get(objectRpcRequestMessage.getMessageKey());
				if (invoker == null) {
					invoker = new ObjectServiceInvoker(objectRpcRequestMessage.getMethod());
					if (invoker != null) {
						invokerRPCMap.put(objectRpcRequestMessage.getMessageKey(), invoker);
					}
				}
			}
		}
		return invoker;
	}

	/**
	 * RPC权限验证
	 * 
	 * @param objectRpcRequestMessage
	 */
	private boolean rpcAuthorize(ObjectRpcRequestMessage objectRpcRequestMessage) {
		if (StringUtils.isNull(sign)) {// 不校验签名
			logger.warn("RPC Signature verification not opened(未开启签名验证)");
			return true;
		}

		long t = (Long) objectRpcRequestMessage.getAttribute(RPC_REQUEST_MESSAGE_T);
		if (t < System.currentTimeMillis() - XTime.ONE_MINUTE) {// 如果超过10秒失效
			return false;
		}

		return (SignatureUtils.byte2hex(SignatureUtils.md5(Bytes.string2bytes(t + sign))))
				.equals((String) objectRpcRequestMessage.getAttribute(RPC_REQUEST_MESSAGE_SIGN));
	}
}
