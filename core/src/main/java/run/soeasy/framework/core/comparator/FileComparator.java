package run.soeasy.framework.core.comparator;

import java.io.File;
import java.util.Comparator;

public enum FileComparator implements Comparator<File> {
	SYSTEM, SIZE, TYPE, NAME, LAST_MODIFIED;

	public int compare(File o1, File o2) {
		switch (this) {
		case SYSTEM:
			return o1.compareTo(o2);
		case NAME:
			return o1.getName().compareTo(o2.getName());
		case TYPE:
			if (o1.isDirectory() && o2.isDirectory()) {
				return 0;
			}

			if (o1.isDirectory()) {
				return 1;
			} else if (o2.isDirectory()) {
				return -1;
			}

			return 0;
		case SIZE:
			return CompareUtils.compare(o1.length(), o2.length(), true);
		case LAST_MODIFIED:
			return CompareUtils.compare(o1.lastModified(), o2.lastModified(), true);
		default:
			throw new UnsupportedOperationException(this.name());
		}
	}

}
