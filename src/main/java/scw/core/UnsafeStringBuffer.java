package scw.core;

import java.io.IOException;
import java.io.Serializable;

public final class UnsafeStringBuffer implements CharSequence, Appendable, Serializable {
	private static final long serialVersionUID = 1L;
	private char[] chars;
	private int count;

	public UnsafeStringBuffer() {
		this(16);
	}

	public UnsafeStringBuffer(int initCapacity) {
		this.chars = new char[initCapacity];
	}

	public UnsafeStringBuffer(String text) {
		this.chars = text.toCharArray();
		this.count = chars.length;
	}

	public UnsafeStringBuffer(char[] chars) {
		this.count = chars.length;
		this.chars = new char[chars.length];
		System.arraycopy(chars, 0, this.chars, 0, count);
	}

	public UnsafeStringBuffer(char[] chars, int start, int end) {
		this.count = end - start;
		if (count < 0) {
			throw new IndexOutOfBoundsException(start + "");
		}

		this.chars = new char[count];
		System.arraycopy(chars, start, chars, 0, count);
	}

	public UnsafeStringBuffer appendNull() {
		expandCapacity(count + 4);
		chars[count++] = 'n';
		chars[count++] = 'u';
		chars[count++] = 'l';
		chars[count++] = 'l';
		return this;
	}

	public UnsafeStringBuffer append(String text) {
		if (text == null) {
			return appendNull();
		}

		int len = text.length();
		if (len == 0) {
			return this;
		}

		expandCapacity(count + len);
		text.getChars(0, len, chars, count);
		count += len;
		return this;
	}

	public UnsafeStringBuffer append(Object value) {
		if (value == null) {
			return appendNull();
		}

		if (value instanceof String) {
			return append((String) value);
		} else if (value instanceof CharSequence) {
			return append((CharSequence) value);
		} else if (value instanceof StringBuilder) {
			StringBuilder sb = (StringBuilder) value;
			int len = sb.length();
			if (len == 0) {
				return this;
			}

			sb.getChars(0, len, chars, count);
			count += len;
			return this;
		} else if (value instanceof StringBuffer) {
			StringBuffer sb = (StringBuffer) value;
			int len = sb.length();
			if (len == 0) {
				return this;
			}

			sb.getChars(0, len, chars, count);
			count += len;
			return this;
		} else {
			return append(value.toString());
		}
	}

	public UnsafeStringBuffer append(String text, int start, int end) {
		if (text == null) {
			return appendNull();
		}

		if ((start < 0) || (start > end) || (end > text.length()))
			throw new IndexOutOfBoundsException("start " + start + ", end " + end + ", s.length() " + text.length());

		int len = end - start;
		expandCapacity(count + len);
		text.getChars(start, end, chars, count);
		count += len;
		return this;
	}

	private void expandCapacity(int minimumCapacity) {
		if (minimumCapacity > chars.length) {
			changeCapacity(minimumCapacity);
		}
	}

	public char[] toCharArray() {
		return chars.clone();
	}

	private void changeCapacity(int capacity) {
		if (capacity == chars.length) {
			return;
		}

		char[] newChars = new char[capacity];
		System.arraycopy(chars, 0, newChars, 0, chars.length);
		this.chars = newChars;
	}

	public void trimToSize() {
		changeCapacity(count);
	}

	public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
		if (srcBegin < 0)
			throw new StringIndexOutOfBoundsException(srcBegin);
		if ((srcEnd < 0) || (srcEnd > count))
			throw new StringIndexOutOfBoundsException(srcEnd);
		if (srcBegin > srcEnd)
			throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
		System.arraycopy(chars, srcBegin, dst, dstBegin, srcEnd - srcBegin);
	}

	/**
	 * 重置
	 * 
	 */
	public void reset() {
		count = 0;
	}

	public UnsafeStringBuffer append(CharSequence sequence, int start, int end) {
		if (sequence == null) {
			return appendNull();
		}

		if ((start < 0) || (start > end) || (end > sequence.length()))
			throw new IndexOutOfBoundsException(
					"start " + start + ", end " + end + ", s.length() " + sequence.length());
		int len = end - start;
		expandCapacity(count + len);
		for (int i = start, j = count; i < end; i++, j++)
			chars[j] = sequence.charAt(i);
		count += len;
		return this;
	}

	public int capacity() {
		return chars.length;
	}

	public int length() {
		return count;
	}

	@Override
	public String toString() {
		return new String(chars, 0, count);
	}

	public UnsafeStringBuffer subSequence(int start, int end) {
		if (start < 0)
			throw new StringIndexOutOfBoundsException(start);
		if (end > count)
			throw new StringIndexOutOfBoundsException(end);
		if (start > end)
			throw new StringIndexOutOfBoundsException(end - start);
		return new UnsafeStringBuffer(chars, start, end);
	}

	public char charAt(int index) {
		if (index > count) {
			throw new IndexOutOfBoundsException(index + "");
		}

		return chars[index];
	}

	public UnsafeStringBuffer append(CharSequence csq) {
		if (csq == null) {
			return appendNull();
		}

		int len = csq.length();
		if (len == 0) {
			return this;
		}

		expandCapacity(count + len);
		for (int i = 0; i < csq.length(); i++, count++) {
			chars[count] = csq.charAt(i);
		}
		return this;
	}

	public Appendable append(char c) throws IOException {
		chars[count++] = c;
		return this;
	}
}
