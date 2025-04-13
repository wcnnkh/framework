package run.soeasy.framework.core.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.List;

import run.soeasy.framework.core.strings.StringUtils;

public class SuffixFileFilter implements FileFilter, FilenameFilter, Serializable {

	private static final long serialVersionUID = -3389157631240246157L;

	/** The file name suffixes to search for */
	private final String[] suffixes;

	/** Whether the comparison is case sensitive. */
	private final IOCase caseSensitivity;

	/**
	 * Constructs a new Suffix file filter for a single extension.
	 *
	 * @param suffix the suffix to allow, must not be null
	 * @throws IllegalArgumentException if the suffix is null
	 */
	public SuffixFileFilter(final String suffix) {
		this(suffix, IOCase.SENSITIVE);
	}

	/**
	 * Constructs a new Suffix file filter for a single extension specifying
	 * case-sensitivity.
	 *
	 * @param suffix          the suffix to allow, must not be null
	 * @param caseSensitivity how to handle case sensitivity, null means
	 *                        case-sensitive
	 * @throws IllegalArgumentException if the suffix is null
	 */
	public SuffixFileFilter(final String suffix, final IOCase caseSensitivity) {
		if (suffix == null) {
			throw new IllegalArgumentException("The suffix must not be null");
		}
		this.suffixes = new String[] { suffix };
		this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
	}

	/**
	 * Constructs a new Suffix file filter for an array of suffixes.
	 * <p>
	 * The array is not cloned, so could be changed after constructing the instance.
	 * This would be inadvisable however.
	 *
	 * @param suffixes the suffixes to allow, must not be null
	 * @throws IllegalArgumentException if the suffix array is null
	 */
	public SuffixFileFilter(final String... suffixes) {
		this(suffixes, IOCase.SENSITIVE);
	}

	/**
	 * Constructs a new Suffix file filter for an array of suffixes specifying
	 * case-sensitivity.
	 *
	 * @param suffixes        the suffixes to allow, must not be null
	 * @param caseSensitivity how to handle case sensitivity, null means
	 *                        case-sensitive
	 * @throws IllegalArgumentException if the suffix array is null
	 */
	public SuffixFileFilter(final String[] suffixes, final IOCase caseSensitivity) {
		if (suffixes == null) {
			throw new IllegalArgumentException("The array of suffixes must not be null");
		}
		this.suffixes = new String[suffixes.length];
		System.arraycopy(suffixes, 0, this.suffixes, 0, suffixes.length);
		this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
	}

	/**
	 * Constructs a new Suffix file filter for a list of suffixes.
	 *
	 * @param suffixes the suffixes to allow, must not be null
	 * @throws IllegalArgumentException if the suffix list is null
	 * @throws ClassCastException       if the list does not contain Strings
	 */
	public SuffixFileFilter(final List<String> suffixes) {
		this(suffixes, IOCase.SENSITIVE);
	}

	/**
	 * Constructs a new Suffix file filter for a list of suffixes specifying
	 * case-sensitivity.
	 *
	 * @param suffixes        the suffixes to allow, must not be null
	 * @param caseSensitivity how to handle case sensitivity, null means
	 *                        case-sensitive
	 * @throws IllegalArgumentException if the suffix list is null
	 * @throws ClassCastException       if the list does not contain Strings
	 */
	public SuffixFileFilter(final List<String> suffixes, final IOCase caseSensitivity) {
		if (suffixes == null) {
			throw new IllegalArgumentException("The list of suffixes must not be null");
		}
		this.suffixes = suffixes.toArray(StringUtils.EMPTY_ARRAY);
		this.caseSensitivity = caseSensitivity == null ? IOCase.SENSITIVE : caseSensitivity;
	}

	/**
	 * Checks to see if the file name ends with the suffix.
	 *
	 * @param file the File to check
	 * @return true if the file name ends with one of our suffixes
	 */
	@Override
	public boolean accept(final File file) {
		final String name = file.getName();
		for (final String suffix : this.suffixes) {
			if (caseSensitivity.checkEndsWith(name, suffix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks to see if the file name ends with the suffix.
	 *
	 * @param file the File directory
	 * @param name the file name
	 * @return true if the file name ends with one of our suffixes
	 */
	@Override
	public boolean accept(final File file, final String name) {
		for (final String suffix : this.suffixes) {
			if (caseSensitivity.checkEndsWith(name, suffix)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Provide a String representation of this file filter.
	 *
	 * @return a String representation
	 */
	@Override
	public String toString() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append(super.toString());
		buffer.append("(");
		if (suffixes != null) {
			for (int i = 0; i < suffixes.length; i++) {
				if (i > 0) {
					buffer.append(",");
				}
				buffer.append(suffixes[i]);
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

}
