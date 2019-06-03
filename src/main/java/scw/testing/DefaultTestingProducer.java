package scw.testing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import scw.core.utils.XTime;

public class DefaultTestingProducer<T> implements TestingProducer<T>, scw.core.Destroy {
	private LinkedBlockingQueue<T> queue = new LinkedBlockingQueue<T>();
	private ObjectOutputStream oos;
	private Thread thread;

	public DefaultTestingProducer(String rootPath) throws IOException {
		File file = new File(rootPath);
		file.mkdirs();
		file = new File(rootPath + File.separator + XTime.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss:SSS")
				+ ".testing");
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fos = new FileOutputStream(file, true);
		oos = new ObjectOutputStream(fos);
		thread = new ProducerThread();
		thread.start();
	}

	public void push(T message) {
		queue.offer(message);
	}

	private final class ProducerThread extends Thread {
		@Override
		public void run() {
			T message;
			try {
				while (!isInterrupted()) {
					message = queue.take();
					if (message == null) {
						continue;
					}

					try {
						oos.writeObject(message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
			}
		}
	}

	public void destroy() {
		thread.interrupt();
		try {
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
