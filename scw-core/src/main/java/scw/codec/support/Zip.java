package scw.codec.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import scw.codec.DecodeException;
import scw.codec.EncodeException;
import scw.core.Assert;
import scw.io.IOUtils;

public class Zip extends FastStreamCodec {
	public static final Zip DEFAULT = new Zip(DEFAULT_BUFF_SIZE);

	public Zip(int buffSize) {
		super(buffSize);
	}

	@Override
	public void encode(InputStream source, OutputStream target) throws IOException, EncodeException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		if (target instanceof ZipOutputStream) {
			super.encode(source, target);
		} else {
			ZipOutputStream zip = null;
			try {
				zip = new ZipOutputStream(target);
				super.encode(source, zip);
			} finally {
				IOUtils.closeQuietly(zip);
			}
		}
	}

	@Override
	public void decode(InputStream source, OutputStream target) throws IOException, DecodeException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		if (source instanceof ZipInputStream) {
			super.decode(source, target);
		} else {
			ZipInputStream zip = null;
			try {
				zip = new ZipInputStream(source);
				super.decode(zip, target);
			} finally {
				IOUtils.closeQuietly(zip);
			}
		}
	}

}
