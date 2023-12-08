package io.basc.framework.observe.mode;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class ViewerRegistry<V extends Viewer> extends ElementViewer<V> {

	@Override
	public void run() {
		getServices().forEach(Viewer::run);
	}

	@Override
	public boolean await(long timeout, TimeUnit unit) throws InterruptedException {
		List<V> list = getServices().toList();
		long time = unit.toMillis(timeout);
		time = Math.max(1, time / list.size());
		for (Viewer viewer : list) {
			if (viewer.await(time, TimeUnit.MILLISECONDS)) {
				return true;
			}
		}
		return false;
	}
}
