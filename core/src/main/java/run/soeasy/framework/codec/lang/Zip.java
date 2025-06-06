package run.soeasy.framework.codec.lang;

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

import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.BytesCodec;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.io.FileUtils;
import run.soeasy.framework.core.io.IOUtils;

public class Zip implements BytesCodec {
	public static final Zip DEFAULT = new Zip();

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
				File entityFile = new File(target, zipEntry.getName());
				entityFile = entityFile.getCanonicalFile();
				if (zipEntry.isDirectory()) {
					// dirName = dirName.substring(0, dirName.length() - 1);
					entityFile.mkdirs();
				} else {
					entityFile.createNewFile();
					InputStream is = zipFile.getInputStream(zipEntry);
					try {
						FileUtils.copyInputStreamToFile(is, entityFile);
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

	@Override
	public void encode(InputStream source, int bufferSize, OutputStream target) throws IOException, EncodeException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		if (target instanceof ZipOutputStream) {
			IOUtils.write(source, target, bufferSize);
		} else {
			ZipOutputStream zip = null;
			try {
				zip = new ZipOutputStream(target);
				IOUtils.write(source, zip, bufferSize);
			} finally {
				IOUtils.closeQuietly(zip);
			}
		}
	}

	@Override
	public void decode(InputStream source, int bufferSize, OutputStream target) throws DecodeException, IOException {
		Assert.requiredArgument(source != null, "source");
		Assert.requiredArgument(target != null, "target");
		if (source instanceof ZipInputStream) {
			IOUtils.write(source, target, bufferSize);
		} else {
			ZipInputStream zip = null;
			try {
				zip = new ZipInputStream(source);
				IOUtils.write(zip, target, bufferSize);
			} finally {
				IOUtils.closeQuietly(zip);
			}
		}
	}
}
