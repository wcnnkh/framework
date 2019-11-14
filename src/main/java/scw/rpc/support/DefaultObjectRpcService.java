package scw.rpc.support;

import java.io.IOException;
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

	public Object request(ObjectRpcRequestMessage objectRpcRequestMessage) throws Throwable {
		if (!rpcAuthorize(objectRpcRequestMessage)) {
			throw new RuntimeException("RPC验证失败");
		}

		ObjectServiceInvoker invoker = getRPCInvoker(objectRpcRequestMessage);
		if (invoker == null) {
			throw new RuntimeException("not found service:" + objectRpcRequestMessage.getMessageKey());
		}

		try {
			return invoker.invoke(
					instanceFactory.getInstance(objectRpcRequestMessage.getMethodDefinition().getBelongClass()),
					objectRpcRequestMessage.getArgs());
		} catch (IllegalArgumentException e) {

			throw e;
		}
	}

	private void response(OutputStream os, ObjectRpcResponseMessage responseMessage) {
		try {
			serializer.serialize(os, responseMessage);
		} catch (IOException e) {
			logger.error(e, "rpc返回失败");
		}
	}

	public void service(InputStream in, OutputStream os) {
		ObjectRpcResponseMessage rpcResponseMessage = new ObjectRpcResponseMessage();
		ObjectRpcRequestMessage objectRpcRequestMessage;
		try {
			objectRpcRequestMessage = serializer.deserialize(in);
		} catch (IOException e1) {
			logger.error(e1, "序列化失败");
			rpcResponseMessage.setThrowable(e1);
			response(os, rpcResponseMessage);
			return;
		}

		try {
			rpcResponseMessage.setResponse(request(objectRpcRequestMessage));
		} catch (Throwable e) {
			logger.error(e, objectRpcRequestMessage.getMessageKey());
			rpcResponseMessage.setThrowable(e);
		}
		response(os, rpcResponseMessage);
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
