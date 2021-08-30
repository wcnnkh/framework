package io.basc.framework.io.support;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 此类优化了{@link File#deleteOnExit()}<br/>
 * jdk自身实现当文件提前删除时注册的删除路径依然存在，在极端情况也可能会出现内存泄露
 * 
 * @see File
 * @see File#deleteOnExit()
 * @see File#delete()
 * @author shuchaowen
 *
 */
public class TemporaryFile extends File {
	private static final long serialVersionUID = 1L;
	private static final Set<String> DELETES = Collections.synchronizedSet(new HashSet<String>());

	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				for (String path : DELETES) {
					new File(path).delete();
				}
			}
		});
	}

	public TemporaryFile(String path) {
		super(path);
	}

	@Override
	public boolean delete() {
		DELETES.remove(getPath());
		return super.delete();
	}

	@Override
	public void deleteOnExit() {
		DELETES.add(getPath());
	}
}
