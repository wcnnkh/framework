package io.basc.framework.codec.support;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.core.Assert;
import io.basc.framework.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Gzip extends FastStreamCodec {
	public static final Gzip DEFAULT = new Gzip(DEFAULT_BUFF_SIZE);

	public Gzip(int buffSize) {
		super(buffSize);
	}

	@Override
	public void encode(InputStream source, OutputStream target) throws IOException, EncodeException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		if (target instanceof GZIPOutputStream) {
			super.encode(source, target);
		} else {
			GZIPOutputStream gzip = null;
			try {
				gzip = new GZIPOutputStream(target);
				super.encode(source, gzip);
			} finally {
				IOUtils.closeQuietly(gzip);
			}
		}
	}

	@Override
	public void decode(InputStream source, OutputStream target) throws IOException, DecodeException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		if (source instanceof GZIPInputStream) {
			super.decode(source, target);
		} else {
			GZIPInputStream gzip = null;
			try {
				gzip = new GZIPInputStream(source);
				super.decode(gzip, target);
			} finally {
				IOUtils.closeQuietly(gzip);
			}
		}
	}
}
