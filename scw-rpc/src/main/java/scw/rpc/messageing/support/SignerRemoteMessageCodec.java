package scw.rpc.messageing.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.codec.DecodeException;
import scw.codec.Signer;
import scw.codec.support.CharsetCodec;
import scw.codec.support.MD5;
import scw.convert.TypeDescriptor;
import scw.io.IOUtils;
import scw.io.Serializer;
import scw.io.SerializerUtils;
import scw.lang.Nullable;
import scw.rpc.messageing.MessageHeaders;
import scw.rpc.messageing.RemoteMessageCodec;
import scw.rpc.messageing.RemoteMessageCodecException;
import scw.rpc.messageing.RemoteRequestMessage;
import scw.rpc.messageing.RemoteResponseMessage;

/**
 * 验证签名的方式编解码数据
 * 
 * @author shuchaowen
 *
 */
public class SignerRemoteMessageCodec implements RemoteMessageCodec {
	private static final String SIGN_ATTRIBUTE_NAME = "_content_sign";
	private final Serializer serializer;
	private final Signer<byte[], String> signer;

	public SignerRemoteMessageCodec(String secretKey) {
		this(null, secretKey);
	}

	public SignerRemoteMessageCodec(@Nullable Serializer serializer,
			String secretKey) {
		this(serializer, new MD5().wrapperSecretKey(
				CharsetCodec.UTF_8.encode(secretKey)).toHex());
	}

	public SignerRemoteMessageCodec(@Nullable Serializer serializer,
			Signer<byte[], String> signer) {
		this.serializer = serializer == null ? SerializerUtils.DEFAULT_SERIALIZER
				: serializer;
		this.signer = signer;
	}

	public void encode(OutputStream output, RemoteRequestMessage requestMessage)
			throws IOException {
		byte[] data = serializer.serialize(requestMessage);
		String sign = signer.encode(data);
		requestMessage.setAttribute(SIGN_ATTRIBUTE_NAME, sign);
		data = serializer.serialize(requestMessage);
		output.write(data);
	}

	public RemoteRequestMessage decode(InputStream input, MessageHeaders headers)
			throws IOException, RemoteMessageCodecException {
		byte[] data = IOUtils.toByteArray(input);
		RemoteRequestMessage requestMessage;
		try {
			requestMessage = serializer.deserialize(data);
		} catch (ClassNotFoundException e) {
			throw new RemoteMessageCodecException(e);
		}
		String requestSign = (String) requestMessage
				.getAttribute(SIGN_ATTRIBUTE_NAME);
		if (requestSign == null) {
			throw new DecodeException("not found sign");
		}

		requestMessage.removeAttribute(SIGN_ATTRIBUTE_NAME);
		data = serializer.serialize(requestMessage);
		if (signer.verify(data, requestSign)) {
			throw new DecodeException("sign verify fail");
		}
		return requestMessage;
	}

	public void encode(OutputStream output,
			RemoteResponseMessage responseMessage) throws IOException,
			RemoteMessageCodecException {
		byte[] data = serializer.serialize(responseMessage);
		String sign = signer.encode(data);
		responseMessage.setAttribute(SIGN_ATTRIBUTE_NAME, sign);
		data = serializer.serialize(responseMessage);
		output.write(data);
	}

	public RemoteResponseMessage decode(InputStream input,
			MessageHeaders headers, TypeDescriptor responseType)
			throws IOException, RemoteMessageCodecException {
		byte[] data = IOUtils.toByteArray(input);
		RemoteResponseMessage responseMessage;
		try {
			responseMessage = serializer.deserialize(data);
		} catch (ClassNotFoundException e) {
			throw new RemoteMessageCodecException(e);
		}
		String requestSign = (String) responseMessage
				.getAttribute(SIGN_ATTRIBUTE_NAME);
		if (requestSign == null) {
			throw new DecodeException("not found sign");
		}

		responseMessage.removeAttribute(SIGN_ATTRIBUTE_NAME);
		data = serializer.serialize(responseMessage);
		if (signer.verify(data, requestSign)) {
			throw new DecodeException("sign verify fail");
		}
		return responseMessage;
	}

}
