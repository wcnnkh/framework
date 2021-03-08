package scw.rpc.messageing.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.codec.Codec;
import scw.codec.DecodeException;
import scw.codec.support.Base64;
import scw.codec.support.DES;
import scw.convert.TypeDescriptor;
import scw.core.Constants;
import scw.core.utils.StringUtils;
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
 * 加解密码的方式编解码数据
 * @author shuchaowen
 *
 */
public class DefaultRemoteMessageCodec implements RemoteMessageCodec {
	private final Serializer serializer;
	private final Codec<byte[], String> codec;

	public DefaultRemoteMessageCodec() {
		this((String) null);
	}

	public DefaultRemoteMessageCodec(@Nullable String secretKey) {
		this(secretKey == null ? null : StringUtils.getStringOperations()
				.getBytes(secretKey, Constants.UTF_8));
	}

	public DefaultRemoteMessageCodec(@Nullable byte[] secretKey) {
		this(SerializerUtils.DEFAULT_SERIALIZER, secretKey);
	}

	public DefaultRemoteMessageCodec(@Nullable Serializer serializer,
			@Nullable String secretKey) {
		this(serializer, secretKey == null ? null : StringUtils
				.getStringOperations().getBytes(secretKey, Constants.UTF_8));
	}

	public DefaultRemoteMessageCodec(@Nullable Serializer serializer,
			@Nullable byte[] secretKey) {
		this(serializer, new DES(secretKey, secretKey).to(new Base64()));
	}

	public DefaultRemoteMessageCodec(@Nullable Serializer serializer,
			@Nullable Codec<byte[], String> codec) {
		this.serializer = serializer == null? SerializerUtils.DEFAULT_SERIALIZER:serializer;
		this.codec = codec;
	}

	public void encode(OutputStream output, RemoteRequestMessage requestMessage)
			throws IOException {
		byte[] data = serializer.serialize(requestMessage);
		if (codec != null) {
			String messageToUse = codec.encode(data);
			data = messageToUse.getBytes(Constants.UTF_8_NAME);
		}
		output.write(data);
	}

	public RemoteRequestMessage decode(InputStream input, MessageHeaders headers)
			throws IOException, RemoteMessageCodecException {
		byte[] data = IOUtils.toByteArray(input);
		if (codec != null) {
			String message = new String(data, Constants.UTF_8);
			data = codec.decode(message);
		}
		try {
			return serializer.deserialize(data);
		} catch (ClassNotFoundException e) {
			throw new DecodeException(e);
		}
	}

	public void encode(OutputStream output,
			RemoteResponseMessage responseMessage) throws IOException,
			RemoteMessageCodecException {
		byte[] data = serializer.serialize(responseMessage);
		if (codec != null) {
			String messageToUse = codec.encode(data);
			data = messageToUse.getBytes(Constants.UTF_8_NAME);
		}
		output.write(data);
	}

	public RemoteResponseMessage decode(InputStream input,
			MessageHeaders headers, TypeDescriptor responseType)
			throws IOException, RemoteMessageCodecException {
		byte[] data = IOUtils.toByteArray(input);
		if (codec != null) {
			String message = new String(data, Constants.UTF_8);
			data = codec.decode(message);
		}
		try {
			return serializer.deserialize(data);
		} catch (ClassNotFoundException e) {
			throw new DecodeException(e);
		}
	}

}
