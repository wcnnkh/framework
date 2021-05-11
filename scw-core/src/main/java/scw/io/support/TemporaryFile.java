package scw.io.support;

import java.io.File;
import java.io.IOException;

import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.event.BasicEvent;
import scw.event.EventDispatcher;
import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.DefaultEventDispatcher;
import scw.io.FileUtils;

/**
 * 这是一个临时文件，在系统回收时会自动删除，但推荐使用结束主动后删除
 * 
 * @author shuchaowen
 *
 */
public class TemporaryFile extends File {
	private static final long serialVersionUID = 1L;
	private static final EventDispatcher<Event> DELETE_EVENT_DISPATCHER = new DefaultEventDispatcher<Event>(
			true);

	static {
		Thread thread = new Thread(new Runnable() {

			public void run() {
				DELETE_EVENT_DISPATCHER.publishEvent(new BasicEvent());
			}
		}, TemporaryFile.class.getName());
		Runtime.getRuntime().addShutdownHook(thread);
	}

	private boolean deleteOnFinalize = false;

	private TemporaryFile(String path) {
		super(path);
		deleteOnExit();
	}

	public boolean isDeleteOnFinalize() {
		return deleteOnFinalize;
	}

	public void setDeleteOnFinalize(boolean deleteOnFinalize) {
		this.deleteOnFinalize = deleteOnFinalize;
	}

	@Override
	protected void finalize() throws Throwable {
		if (isDeleteOnFinalize()) {
			delete();
		}
		super.finalize();
	}

	private EventRegistration registration;

	@Override
	public synchronized void deleteOnExit() {
		if (registration != null) {
			return;
		}

		registration = DELETE_EVENT_DISPATCHER.registerListener(new EventListener<Event>() {

			public void onEvent(Event event) {
				delete();
			}
		});
	}

	@Override
	public boolean delete() {
		boolean b = super.delete();
		if (b) {
			if (registration != null) {
				registration.unregister();
				registration = null;
			}
		}
		return b;
	}

	public static TemporaryFile wrapper(File file) {
		if (file instanceof TemporaryFile) {
			return (TemporaryFile) file;
		}
		return new TemporaryFile(file.getPath());
	}

	/**
     * Creates an empty file in the default temporary-file directory, using
     * the given prefix and suffix to generate its name. Invoking this method
     * is equivalent to invoking <code>{@link #createTempFile(java.lang.String,
     * java.lang.String, java.io.File)
     * createTempFile(prefix,&nbsp;suffix,&nbsp;null)}</code>.
     *
     * <p> The {@link
     * java.nio.file.Files#createTempFile(String,String,java.nio.file.attribute.FileAttribute[])
     * Files.createTempFile} method provides an alternative method to create an
     * empty file in the temporary-file directory. Files created by that method
     * may have more restrictive access permissions to files created by this
     * method and so may be more suited to security-sensitive applications.
     *
     * @param  prefix     The prefix string to be used in generating the file's
     *                    name; must be at least three characters long
     *
     * @param  suffix     The suffix string to be used in generating the file's
     *                    name; may be <code>null</code>, in which case the
     *                    suffix <code>".tmp"</code> will be used
     *
     * @return  An abstract pathname denoting a newly-created empty file
     *
     * @throws  IllegalArgumentException
     *          If the <code>prefix</code> argument contains fewer than three
     *          characters
     *
     * @throws  IOException  If a file could not be created
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method does not allow a file to be created
     *
     * @see java.nio.file.Files#createTempDirectory(String,FileAttribute[])
     */
	public static TemporaryFile createTempFile(String prefix, String suffix) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		return wrapper(file);
	}

	/**
     * <p> Creates a new empty file in the specified directory, using the
     * given prefix and suffix strings to generate its name.  If this method
     * returns successfully then it is guaranteed that:
     *
     * <ol>
     * <li> The file denoted by the returned abstract pathname did not exist
     *      before this method was invoked, and
     * <li> Neither this method nor any of its variants will return the same
     *      abstract pathname again in the current invocation of the virtual
     *      machine.
     * </ol>
     *
     * This method provides only part of a temporary-file facility.  To arrange
     * for a file created by this method to be deleted automatically, use the
     * <code>{@link #deleteOnExit}</code> method.
     *
     * <p> The <code>prefix</code> argument must be at least three characters
     * long.  It is recommended that the prefix be a short, meaningful string
     * such as <code>"hjb"</code> or <code>"mail"</code>.  The
     * <code>suffix</code> argument may be <code>null</code>, in which case the
     * suffix <code>".tmp"</code> will be used.
     *
     * <p> To create the new file, the prefix and the suffix may first be
     * adjusted to fit the limitations of the underlying platform.  If the
     * prefix is too long then it will be truncated, but its first three
     * characters will always be preserved.  If the suffix is too long then it
     * too will be truncated, but if it begins with a period character
     * (<code>'.'</code>) then the period and the first three characters
     * following it will always be preserved.  Once these adjustments have been
     * made the name of the new file will be generated by concatenating the
     * prefix, five or more internally-generated characters, and the suffix.
     *
     * <p> If the <code>directory</code> argument is <code>null</code> then the
     * system-dependent default temporary-file directory will be used.  The
     * default temporary-file directory is specified by the system property
     * <code>java.io.tmpdir</code>.  On UNIX systems the default value of this
     * property is typically <code>"/tmp"</code> or <code>"/var/tmp"</code>; on
     * Microsoft Windows systems it is typically <code>"C:\\WINNT\\TEMP"</code>.  A different
     * value may be given to this system property when the Java virtual machine
     * is invoked, but programmatic changes to this property are not guaranteed
     * to have any effect upon the temporary directory used by this method.
     *
     * @param  prefix     The prefix string to be used in generating the file's
     *                    name; must be at least three characters long
     *
     * @param  suffix     The suffix string to be used in generating the file's
     *                    name; may be <code>null</code>, in which case the
     *                    suffix <code>".tmp"</code> will be used
     *
     * @param  directory  The directory in which the file is to be created, or
     *                    <code>null</code> if the default temporary-file
     *                    directory is to be used
     *
     * @return  An abstract pathname denoting a newly-created empty file
     *
     * @throws  IllegalArgumentException
     *          If the <code>prefix</code> argument contains fewer than three
     *          characters
     *
     * @throws  IOException  If a file could not be created
     *
     * @throws  SecurityException
     *          If a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *          method does not allow a file to be created
     */
	public static TemporaryFile createTempFile(String prefix, String suffix, File directory) throws IOException {
		File file = File.createTempFile(prefix, suffix, directory);
		return wrapper(file);
	}

	/**
	 * 在临时目录创建一个文件
	 * @param filename
	 * @return
	 */
	public static TemporaryFile createInTempDirectory(String filename) {
		Assert.requiredArgument(StringUtils.isNotEmpty(filename), "filename");
		String path = FileUtils.getTempDirectoryPath() + File.separator + filename;
		return new TemporaryFile(path);
	}
}
