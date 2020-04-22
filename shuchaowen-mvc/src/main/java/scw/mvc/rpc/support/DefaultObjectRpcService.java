package scw.mvc.rpc.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.core.instance.InstanceFactory;
import scw.core.parameter.annotation.ParameterName;
import scw.core.utils.StringUtils;
import scw.core.utils.XTime;
import scw.io.Bytes;
import scw.io.Serializer;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.mvc.rpc.RpcConstants;
import scw.mvc.rpc.RpcService;
import scw.rcp.object.ObjectRequestMessage;
import scw.rcp.object.ObjectResponseMessage;
import scw.security.signature.SignatureUtils;

public final class DefaultObjectRpcService implements RpcService, RpcConstants {
	private static Logger logger = LoggerUtils.getLogger(DefaultObjectRpcService.class);
	private final String sign;
	private final InstanceFactory instanceFactory;
	private final Serializer serializer;

	public DefaultObjectRpcService(InstanceFactory instanceFactory, @ParameterName(RPC_HTTP_SIGN_NAME) String sign,
			Serializer serializer) {
		this.instanceFactory = instanceFactory;
		this.sign = sign;
		this.serializer = serializer;
	}

	public Object request(ObjectRequestMessage requestMessage) throws Throwable {
		if (!rpcAuthorize(requestMessage)) {
			throw new RuntimeException("RPC验证失败");
		}

		Object instance = instanceFactory.getInstance(requestMessage.getMethodHolder().getBelongClass());
		return requestMessage.invoke(instance);
	}

	private void response(OutputStream os, ObjectResponseMessage responseMessage) {
		try {
			serializer.serialize(os, responseMessage);
		} catch (IOException e) {
			logger.error(e, "rpc返回失败");
		}
	}

	public void service(InputStream in, OutputStream os) {
		ObjectResponseMessage resonseMessage = new ObjectResponseMessage();
		ObjectRequestMessage requestMessage;
		try {
			requestMessage = serializer.deserialize(in);
			resonseMessage.setRequestMessage(requestMessage);
		} catch (IOException e1) {
			logger.error(e1, "序列化失败");
			resonseMessage.setError(e1);
			response(os, resonseMessage);
			return;
		}

		if (logger.isDebugEnabled()) {
			logger.debug(requestMessage);
		}

		try {
			resonseMessage.setResponse(request(requestMessage));
		} catch (Throwable e) {
			logger.error(e, requestMessage);
			resonseMessage.setError(e);
		}
		response(os, resonseMessage);
	}

	/**
	 * RPC权限验证
	 * 
	 * @param objectRpcRequestMessage
	 */
	private boolean rpcAuthorize(ObjectRequestMessage objectRpcRequestMessage) {
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
