package run.soeasy.framework.core.comparator;

import java.io.File;

import lombok.NonNull;

public enum FileComparator implements Sorter<File> {
	SYSTEM, SIZE, TYPE, NAME, LAST_MODIFIED;

	@Override
	public @NonNull Ordering evaluate(File left, File right) {
		switch (this) {
		case SYSTEM:
			return Ordering.comparable(left, right);
		case NAME:
			return Ordering.comparable(left.getName(), right.getName());
		case TYPE:
			if (left.isDirectory() && right.isDirectory()) {
				return Ordering.of(0);
			}

			if (left.isDirectory()) {
				return Ordering.of(1);
			} else if (right.isDirectory()) {
				return Ordering.of(-1);
			}

			return Ordering.equal();
		case SIZE:
			return Ordering.of(Long.compare(right.length(), left.length()));
		case LAST_MODIFIED:
			return Ordering.of(Long.compare(right.lastModified(), left.lastModified()));
		default:
			throw new UnsupportedOperationException(this.name());
		}
	}

}
