package scw.rpc.remote;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import scw.codec.Signer;
import scw.codec.encoder.MD5;
import scw.codec.support.CharsetCodec;
import scw.io.IOUtils;
import scw.io.Serializer;
import scw.io.SerializerUtils;
import scw.lang.Nullable;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

/**
 * 验证签名的方式编解码数据
 * 
 * @author shuchaowen
 *
 */
public class SignerRemoteMessageCodec implements RemoteMessageCodec {
	private static final String SIGN_HEADER_NAME = "_content_sign";
	private final Serializer serializer;
	private final Signer<byte[], String> signer;

	public SignerRemoteMessageCodec(String secretKey) {
		this(null, secretKey);
	}

	public SignerRemoteMessageCodec(@Nullable Serializer serializer,
			String secretKey) {
		this(serializer, new MD5().wrapperSecretKey(
				CharsetCodec.UTF_8.encode(secretKey)).toHex().toSigner());
	}

	public SignerRemoteMessageCodec(@Nullable Serializer serializer,
			Signer<byte[], String> signer) {
		this.serializer = serializer == null ? SerializerUtils.getSerializer()
				: serializer;
		this.signer = signer;
	}
	
	public void write(OutputMessage output, Object message) throws IOException,
			RemoteMessageCodecException {
		byte[] data = serializer.serialize(message);
		String sign = signer.encode(data);
		output.getHeaders().set(SIGN_HEADER_NAME, sign);
		OutputStream os = output.getOutputStream();
		try{
			IOUtils.write(data, os);
		}finally{
			os.close();
		}
	}
	
	public Object read(InputMessage input) throws IOException,
			RemoteMessageCodecException {
		InputStream is = input.getInputStream();
		byte[] data;
		try {
			data = IOUtils.toByteArray(is);
		} finally{
			is.close();
		}
		
		String sign = input.getHeaders().getFirst(SIGN_HEADER_NAME);
		if (sign == null) {
			throw new RemoteMessageCodecException("not found sign");
		}
		
		if (!signer.verify(data, sign)) {
			throw new RemoteMessageCodecException("sign verify fail");
		}
		
		try {
			return serializer.deserialize(data);
		} catch (ClassNotFoundException e) {
			throw new RemoteMessageCodecException(e);
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

	public void encode(OutputMessage output, RemoteResponseMessage responseMessage,
			RemoteRequestMessage requestMessage) throws IOException,
			RemoteMessageCodecException {
		write(output, responseMessage);
	}

	public RemoteRequestMessage decode(InputMessage input) throws IOException,
			RemoteMessageCodecException {
		return (RemoteRequestMessage) read(input);
	}
}
