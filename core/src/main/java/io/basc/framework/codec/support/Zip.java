package io.basc.framework.codec.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import io.basc.framework.codec.DecodeException;
import io.basc.framework.codec.EncodeException;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.IOUtils;
import io.basc.framework.util.Assert;

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

	public static void unZip(File source, File target) throws ZipException, IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(source);
			if (!target.exists()) {
				target.mkdirs();
			}

			Enumeration<? extends ZipEntry> ens = zipFile.entries();
			ZipEntry zipEntry = null;
			while (ens.hasMoreElements()) {
				zipEntry = ens.nextElement();
				File f = new File(target, Assert.securePath(zipEntry.getName()));
				if (zipEntry.isDirectory()) {
					// dirName = dirName.substring(0, dirName.length() - 1);
					f.mkdirs();
				} else {
					f.createNewFile();
					InputStream is = zipFile.getInputStream(zipEntry);
					try {
						FileUtils.copyInputStreamToFile(is, f);
						// 出现异常应该中止吗？
					} finally {
						IOUtils.close(is);
					}
				}
			}
		} finally {
			IOUtils.close(zipFile);
		}
	}
}
