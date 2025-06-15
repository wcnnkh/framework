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

import lombok.NonNull;
import run.soeasy.framework.codec.DecodeException;
import run.soeasy.framework.codec.EncodeException;
import run.soeasy.framework.codec.binary.BytesCodec;
import run.soeasy.framework.io.FileUtils;
import run.soeasy.framework.io.IOUtils;

public class Zip implements BytesCodec {
	public static final Zip DEFAULT = new Zip();

	public static void unZip(@NonNull File source, @NonNull File target) throws ZipException, IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(source);
			if (!target.exists()) {
				target.mkdirs();
			}

			File canonicalTarget = target.getCanonicalFile();
			Enumeration<? extends ZipEntry> ens = zipFile.entries();
			ZipEntry zipEntry = null;
			while (ens.hasMoreElements()) {
				zipEntry = ens.nextElement();
				File entityFile = new File(target, zipEntry.getName());
				entityFile = entityFile.getCanonicalFile();
				if (!entityFile.toPath().startsWith(canonicalTarget.toPath())) {
					throw new IOException("Zip entry is outside of the target directory: " + zipEntry.getName());
				}

				if (zipEntry.isDirectory()) {
					entityFile.mkdirs();
				} else {
					entityFile.createNewFile();
					InputStream is = zipFile.getInputStream(zipEntry);
					try {
						FileUtils.transferTo(is, entityFile);
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
	public void encode(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target)
			throws IOException, EncodeException {
		if (target instanceof ZipOutputStream) {
			IOUtils.transferTo(source, target, bufferSize);
		} else {
			ZipOutputStream zip = null;
			try {
				zip = new ZipOutputStream(target);
				IOUtils.transferTo(source, zip, bufferSize);
			} finally {
				IOUtils.closeQuietly(zip);
			}
		}
	}

	@Override
	public void decode(@NonNull InputStream source, int bufferSize, @NonNull OutputStream target)
			throws DecodeException, IOException {
		if (source instanceof ZipInputStream) {
			IOUtils.transferTo(source, target, bufferSize);
		} else {
			ZipInputStream zip = null;
			try {
				zip = new ZipInputStream(source);
				IOUtils.transferTo(zip, target, bufferSize);
			} finally {
				IOUtils.closeQuietly(zip);
			}
		}
	}
}
