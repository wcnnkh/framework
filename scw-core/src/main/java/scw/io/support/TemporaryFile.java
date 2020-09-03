package scw.io.support;

import java.io.File;
import java.io.IOException;

import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.event.BasicEventDispatcher;
import scw.event.Event;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.support.BasicEvent;
import scw.event.support.DefaultBasicEventDispatcher;
import scw.io.FileUtils;

/**
 * 这是一个临时文件，在系统回收时会自动删除，但推荐使用结束主动后删除
 * 
 * @author shuchaowen
 *
 */
public class TemporaryFile extends File {
	private static final long serialVersionUID = 1L;
	private static final BasicEventDispatcher<Event> DELETE_EVENT_DISPATCHER = new DefaultBasicEventDispatcher<Event>(
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

	public static TemporaryFile createTempFile(String prefix, String suffix) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		return wrapper(file);
	}

	public static TemporaryFile createTempFile(String prefix, String suffix, File directory) throws IOException {
		File file = File.createTempFile(prefix, suffix, directory);
		return wrapper(file);
	}

	public static TemporaryFile createInTempDirectory(String filename) {
		Assert.requiredArgument(StringUtils.isNotEmpty(filename), "filename");
		String path = FileUtils.getTempDirectoryPath() + File.separator + filename;
		return new TemporaryFile(path);
	}
}
