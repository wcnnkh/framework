package io.basc.framework.util.io;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.CharBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;

import io.basc.framework.util.StringUtils;
import lombok.Data;

@Data
class SplitReadableIterator implements Iterator<CharSequence> {
	private final Readable readable;
	private final CharBuffer buffer;
	private final CharSequence separator;
	/**
	 * TODO 思考是否可以不使用cache，复用buffer来节约内存
	 */
	private final StringBuilder cache = new StringBuilder();
	private CharSequence next;

	private boolean hasNextCache() {
		int index = StringUtils.indexOf(cache, separator);
		if (index == -1) {
			return false;
		}

		next = cache.subSequence(0, index);
		cache.delete(0, index + separator.length());
		return true;
	}

	@Override
	public boolean hasNext() {
		if (next != null) {
			return true;
		}

		if (hasNextCache()) {
			return true;
		}

		try {
			while (readable.read(buffer) != -1) {
				cache.append(buffer.flip().toString());
				if (hasNextCache()) {
					return true;
				}
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		if (cache.length() != 0) {
			next = cache.toString();
			cache.delete(0, cache.length());
			return true;
		}

		return false;
	}

	@Override
	public CharSequence next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		try {
			return next;
		} finally {
			next = null;
		}
	}
}
