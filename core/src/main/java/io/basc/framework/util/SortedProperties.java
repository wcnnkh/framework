package io.basc.framework.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import io.basc.framework.lang.Nullable;

public class SortedProperties extends Properties {
	private static final long serialVersionUID = 1L;

	static final String EOL = System.lineSeparator();

	private static final Comparator<Object> keyComparator = new Comparator<Object>() {

		public int compare(Object o1, Object o2) {
			return String.valueOf(o1).compareTo(String.valueOf(o2));
		}
	};

	private static final Comparator<Entry<Object, Object>> entryComparator = new Comparator<Entry<Object, Object>>() {

		public int compare(java.util.Map.Entry<Object, Object> o1, java.util.Map.Entry<Object, Object> o2) {
			return keyComparator.compare(o1.getKey(), o2.getKey());
		}
	};

	private final boolean omitComments;

	/**
	 * Construct a new {@code SortedProperties} instance that honors the supplied
	 * {@code omitComments} flag.
	 * 
	 * @param omitComments {@code true} if comments should be omitted when storing
	 *                     properties in a file
	 */
	public SortedProperties(boolean omitComments) {
		this.omitComments = omitComments;
	}

	/**
	 * Construct a new {@code SortedProperties} instance with properties populated
	 * from the supplied {@link Properties} object and honoring the supplied
	 * {@code omitComments} flag.
	 * <p>
	 * Default properties from the supplied {@code Properties} object will not be
	 * copied.
	 * 
	 * @param properties   the {@code Properties} object from which to copy the
	 *                     initial properties
	 * @param omitComments {@code true} if comments should be omitted when storing
	 *                     properties in a file
	 */
	public SortedProperties(Properties properties, boolean omitComments) {
		this(omitComments);
		putAll(properties);
	}

	@Override
	public void store(OutputStream out, @Nullable String comments) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		super.store(baos, (this.omitComments ? null : comments));
		String contents = baos.toString(StandardCharsets.ISO_8859_1.name());
		for (String line : contents.split(EOL)) {
			if (!(this.omitComments && line.startsWith("#"))) {
				out.write((line + EOL).getBytes(StandardCharsets.ISO_8859_1));
			}
		}
	}

	@Override
	public void store(Writer writer, @Nullable String comments) throws IOException {
		StringWriter stringWriter = new StringWriter();
		super.store(stringWriter, (this.omitComments ? null : comments));
		String contents = stringWriter.toString();
		for (String line : contents.split(EOL)) {
			if (!(this.omitComments && line.startsWith("#"))) {
				writer.write(line + EOL);
			}
		}
	}

	@Override
	public void storeToXML(OutputStream out, @Nullable String comments) throws IOException {
		super.storeToXML(out, (this.omitComments ? null : comments));
	}

	@Override
	public void storeToXML(OutputStream out, @Nullable String comments, String encoding) throws IOException {
		super.storeToXML(out, (this.omitComments ? null : comments), encoding);
	}

	/**
	 * Return a sorted enumeration of the keys in this {@link Properties} object.
	 * 
	 * @see #keySet()
	 */
	@Override
	public synchronized Enumeration<Object> keys() {
		return Collections.enumeration(keySet());
	}

	/**
	 * Return a sorted set of the keys in this {@link Properties} object.
	 * <p>
	 * The keys will be converted to strings if necessary using
	 * {@link String#valueOf(Object)} and sorted alphanumerically according to the
	 * natural order of strings.
	 */
	@Override
	public Set<Object> keySet() {
		Set<Object> sortedKeys = new TreeSet<Object>(keyComparator);
		sortedKeys.addAll(super.keySet());
		return Collections.synchronizedSet(sortedKeys);
	}

	/**
	 * Return a sorted set of the entries in this {@link Properties} object.
	 * <p>
	 * The entries will be sorted based on their keys, and the keys will be
	 * converted to strings if necessary using {@link String#valueOf(Object)} and
	 * compared alphanumerically according to the natural order of strings.
	 */
	@Override
	public Set<Entry<Object, Object>> entrySet() {
		Set<Entry<Object, Object>> sortedEntries = new TreeSet<Entry<Object, Object>>(entryComparator);
		sortedEntries.addAll(super.entrySet());
		return Collections.synchronizedSet(sortedEntries);
	}

}
