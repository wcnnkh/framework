package run.soeasy.framework.codec.support;

import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.BytesCodec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Gzip implements BytesCodec {
	public static final Gzip DEFAULT = new Gzip();

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
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
	public void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
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
