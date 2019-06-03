package scw.testing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import scw.core.utils.IOUtils;

public abstract class DefaultTestingConsumer<T> implements TestingConsumer<T> {
	private String file;

	public DefaultTestingConsumer(String file) throws IOException {
		this.file = file;
	}

	@SuppressWarnings("unchecked")
	public void init() {
		File file = new File(this.file);
		if (!file.exists()) {
			return;
		}

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			T obj;
			while ((obj = (T) ois.readObject()) != null) {
				consume(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeInputStream(ois, fis);
		}
	}
}
