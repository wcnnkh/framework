package scw.net.message.multipart;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import scw.io.FileCopyUtils;
import scw.io.FileUtils;
import scw.io.Resource;
import scw.lang.Nullable;
import scw.net.message.InputMessage;

public interface MultipartMessage extends InputMessage {
	String getName();

	@Nullable
	String getOriginalFilename();

	default boolean isFile() {
		return getOriginalFilename() != null;
	}

	long getSize();

	default Resource getResource() {
		return new MultipartFileResource(this);
	}

	/**
	 * Transfer the received file to the given destination file.
	 * <p>
	 * This may either move the file in the filesystem, copy the file in the
	 * filesystem, or save memory-held contents to the destination file. If the
	 * destination file already exists, it will be deleted first.
	 * <p>
	 * If the target file has been moved in the filesystem, this operation cannot be
	 * invoked again afterwards. Therefore, call this method just once in order to
	 * work with any storage mechanism.
	 * <p>
	 * <b>NOTE:</b> Depending on the underlying provider, temporary storage may be
	 * container-dependent, including the base directory for relative destinations
	 * specified here (e.g. with Servlet 3.0 multipart handling). For absolute
	 * destinations, the target file may get renamed/moved from its temporary
	 * location or newly copied, even if a temporary copy already exists.
	 * 
	 * @param dest the destination file (typically absolute)
	 * @throws IOException           in case of reading or writing errors
	 * @throws IllegalStateException if the file has already been moved in the
	 *                               filesystem and is not available anymore for
	 *                               another transfer
	 * @see org.apache.commons.fileupload.FileItem#write(File)
	 * @see javax.servlet.http.Part#write(String)
	 */
	default void transferTo(File dest) throws IOException, IllegalStateException {
		FileUtils.copyInputStreamToFile(getBody(), dest);
	}

	/**
	 * Transfer the received file to the given destination file.
	 * <p>
	 * The default implementation simply copies the file input stream.
	 * 
	 * @see #getInputStream()
	 * @see #transferTo(File)
	 */
	default void transferTo(Path dest) throws IOException, IllegalStateException {
		FileCopyUtils.copy(getBody(), Files.newOutputStream(dest));
	}
}
