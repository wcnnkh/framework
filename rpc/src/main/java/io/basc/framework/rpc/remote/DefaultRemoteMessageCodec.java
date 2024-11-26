package io.basc.framework.rpc.remote;

import io.basc.framework.io.IOUtils;
import io.basc.framework.io.Serializer;
import io.basc.framework.io.SerializerUtils;
import io.basc.framework.lang.Constants;
import io.basc.framework.lang.Nullable;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.codec.Codec;
import io.basc.framework.util.codec.DecodeException;
import io.basc.framework.util.codec.support.CharsetCodec;
import io.basc.framework.util.codec.support.DES;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 加解密码的方式编解码数据
 * @author wcnnkh
 *
 */
public class DefaultRemoteMessageCodec implements RemoteMessageCodec {
	private final Serializer serializer;
	private final Codec<byte[], String> codec;

	public DefaultRemoteMessageCodec() {
		this((String) null);
	}

	public DefaultRemoteMessageCodec(@Nullable String secretKey) {
		this(secretKey == null ? null : CharsetCodec.UTF_8.encode(secretKey));
	}

	public DefaultRemoteMessageCodec(@Nullable byte[] secretKey) {
		this(SerializerUtils.getSerializer(), secretKey);
	}

	public DefaultRemoteMessageCodec(@Nullable Serializer serializer,
			@Nullable String secretKey) {
		this(serializer, secretKey == null ? null : CharsetCodec.UTF_8.encode(secretKey));
	}

	public DefaultRemoteMessageCodec(@Nullable Serializer serializer,
			@Nullable byte[] secretKey) {
		this(serializer, new DES(secretKey, secretKey).toBase64());
	}

	public DefaultRemoteMessageCodec(@Nullable Serializer serializer,
			@Nullable Codec<byte[], String> codec) {
		this.serializer = serializer == null? SerializerUtils.getSerializer():serializer;
		this.codec = codec;
	}

	public void write(OutputMessage output, Object message)
			throws IOException, RemoteMessageCodecException {
		byte[] data = serializer.serialize(message);
		if (codec != null) {
			String messageToUse = codec.encode(data);
			data = messageToUse.getBytes(Constants.UTF_8_NAME);
		}
		OutputStream os = output.getOutputStream();
		try{
			IOUtils.write(data, os);
		}finally{
			os.close();
		}
	}

	public Object read(InputMessage input)
			throws IOException, RemoteMessageCodecException {
		InputStream is = input.getInputStream();
		byte[] data;
		try {
			data = IOUtils.toByteArray(is);
		} finally{
			is.close();
		}
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

	public void encode(OutputMessage output, RemoteRequestMessage requestMessage)
			throws IOException, RemoteMessageCodecException {
		write(output, requestMessage);
	}

	public RemoteResponseMessage decode(InputMessage input,
			RemoteRequestMessage requestMessage) throws IOException,
			RemoteMessageCodecException {
		return (RemoteResponseMessage) read(input);
	}

	public RemoteRequestMessage decode(InputMessage input) throws IOException,
			RemoteMessageCodecException {
		return (RemoteRequestMessage) read(input);
	}

	public void encode(OutputMessage output,
			RemoteResponseMessage responseMessage,
			RemoteRequestMessage requestMessage) throws IOException,
			RemoteMessageCodecException {
		encode(output, responseMessage, requestMessage);
	}

}
