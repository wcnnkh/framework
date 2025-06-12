package run.soeasy.framework.codec.lang;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import lombok.NonNull;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.BytesCodec;
import run.soeasy.framework.io.IOUtils;

public class Gzip implements BytesCodec {
	public static final Gzip DEFAULT = new Gzip();

	@Override
	public void encode(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target) throws IOException, EncodeException {
		if (target instanceof GZIPOutputStream) {
			IOUtils.write(source, target, bufferSize);
		} else {
			GZIPOutputStream gzip = null;
			try {
				gzip = new GZIPOutputStream(target);
				IOUtils.write(source, gzip, bufferSize);
			} finally {
				IOUtils.closeQuietly(gzip);
			}
		}
	}

	@Override
	public void decode(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target) throws DecodeException, IOException {
		if (source instanceof GZIPInputStream) {
			IOUtils.write(source, target, bufferSize);
		} else {
			GZIPInputStream gzip = null;
			try {
				gzip = new GZIPInputStream(source);
				IOUtils.write(gzip, target, bufferSize);
			} finally {
				IOUtils.closeQuietly(gzip);
			}
		}
	}
}
