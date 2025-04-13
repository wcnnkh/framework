package run.soeasy.framework.codec.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.NoSuchProviderException;
import java.security.Provider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.CodecException;
import run.soeasy.framework.core.io.BufferProcessor;
import run.soeasy.framework.core.io.IOUtils;

@Getter
@RequiredArgsConstructor
public class CipherFactory {
	@NonNull
	private final String transformation;
	private final Object provider;
	private final int opmode;
	@NonNull
	private final CipherInitializer cipherInitializer;

	public Cipher newCipher() throws GeneralSecurityException, NoSuchProviderException {
		Cipher cipher;
		if (provider == null) {
			cipher = Cipher.getInstance(transformation);
		} else if (provider instanceof String) {
			cipher = Cipher.getInstance(transformation, (String) provider);
		} else if (provider instanceof Provider) {
			cipher = Cipher.getInstance(transformation, (Provider) provider);
		} else {
			throw new NoSuchProviderException(provider.toString());
		}
		return cipher;
	}

	public Cipher getCipher() throws GeneralSecurityException, NoSuchProviderException {
		Cipher cipher = newCipher();
		cipherInitializer.init(cipher, opmode);
		return cipher;
	}

	public <E extends Throwable> long doFinal(InputStream source, int bufferSize,
			BufferProcessor<byte[], E> targetProcessor)
			throws IOException, GeneralSecurityException, NoSuchProviderException, E {
		Cipher cipher = getCipher();
		return IOUtils.read(source, bufferSize, (buff, offset, len) -> {
			byte[] target;
			try {
				target = cipher.doFinal(buff, offset, len);
			} catch (IllegalBlockSizeException | BadPaddingException e) {
				throw new CodecException(e);
			}
			targetProcessor.process(target, 0, target.length);
		});
	}

	public byte[] doFinal(byte[] source) throws GeneralSecurityException, NoSuchProviderException {
		Cipher cipher = getCipher();
		return cipher.doFinal(source);
	}

	public byte[] doFinal(byte[] source, int offset, int len) throws GeneralSecurityException, NoSuchProviderException {
		Cipher cipher = getCipher();
		return cipher.doFinal(source, offset, len);
	}
}
