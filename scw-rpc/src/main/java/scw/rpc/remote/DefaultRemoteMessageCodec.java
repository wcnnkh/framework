package scw.rpc.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.codec.Codec;
import scw.codec.DecodeException;
import scw.codec.support.Base64;
import scw.codec.support.CharsetCodec;
import scw.codec.support.DES;
import scw.core.Constants;
import scw.io.IOUtils;
import scw.io.NoTypeSpecifiedSerializer;
import scw.io.SerializerUtils;
import scw.lang.Nullable;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

/**
 * 加解密码的方式编解码数据
 * @author shuchaowen
 *
 */
public class DefaultRemoteMessageCodec implements RemoteMessageCodec {
	private final NoTypeSpecifiedSerializer serializer;
	private final Codec<byte[], String> codec;

	public DefaultRemoteMessageCodec() {
		this((String) null);
	}

	public DefaultRemoteMessageCodec(@Nullable String secretKey) {
		this(secretKey == null ? null : CharsetCodec.UTF_8.encode(secretKey));
	}

	public DefaultRemoteMessageCodec(@Nullable byte[] secretKey) {
		this(SerializerUtils.DEFAULT_SERIALIZER, secretKey);
	}

	public DefaultRemoteMessageCodec(@Nullable NoTypeSpecifiedSerializer serializer,
			@Nullable String secretKey) {
		this(serializer, secretKey == null ? null : CharsetCodec.UTF_8.encode(secretKey));
	}

	public DefaultRemoteMessageCodec(@Nullable NoTypeSpecifiedSerializer serializer,
			@Nullable byte[] secretKey) {
		this(serializer, new DES(secretKey, secretKey).to(new Base64()));
	}

	public DefaultRemoteMessageCodec(@Nullable NoTypeSpecifiedSerializer serializer,
			@Nullable Codec<byte[], String> codec) {
		this.serializer = serializer == null? SerializerUtils.DEFAULT_SERIALIZER:serializer;
		this.codec = codec;
	}

	public void write(OutputMessage output, Object message)
			throws IOException, RemoteMessageCodecException {
		byte[] data = serializer.serialize(message);
		if (codec != null) {
			String messageToUse = codec.encode(data);
			data = messageToUse.getBytes(Constants.UTF_8_NAME);
		}
		OutputStream os = output.getBody();
		try{
			IOUtils.write(data, os);
		}finally{
			os.close();
		}
	}

	public Object read(InputMessage input)
			throws IOException, RemoteMessageCodecException {
		InputStream is = input.getBody();
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
