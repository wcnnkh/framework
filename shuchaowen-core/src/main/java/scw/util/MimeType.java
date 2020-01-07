package scw.util;

import java.util.Collection;
import java.util.Map;

public interface MimeType extends Comparable<MimeType> {
	String getType();

	String getSubtype();

	Map<String, String> getParameters();

	/*----------------other------------------*/
	String getParameter(String name);

	String getCharsetName();

	boolean isWildcardType();

	boolean isWildcardSubtype();

	boolean isConcrete();

	/**
	 * Indicate whether this MIME Type includes the given MIME Type.
	 * <p>
	 * For instance, {@code text/*} includes {@code text/plain} and
	 * {@code text/html}, and {@code application/*+xml} includes
	 * {@code application/soap+xml}, etc. This method is <b>not</b> symmetric.
	 * 
	 * @param other
	 *            the reference MIME Type with which to compare
	 * @return {@code true} if this MIME Type includes the given MIME Type;
	 *         {@code false} otherwise
	 */
	boolean includes(MimeType other);

	/**
	 * Indicate whether this MIME Type is compatible with the given MIME Type.
	 * <p>
	 * For instance, {@code text/*} is compatible with {@code text/plain},
	 * {@code text/html}, and vice versa. In effect, this method is similar to
	 * {@link #includes}, except that it <b>is</b> symmetric.
	 * 
	 * @param other
	 *            the reference MIME Type with which to compare
	 * @return {@code true} if this MIME Type is compatible with the given MIME
	 *         Type; {@code false} otherwise
	 */
	boolean isCompatibleWith(MimeType other);

	/**
	 * Similar to {@link #equals(Object)} but based on the type and subtype
	 * only, i.e. ignoring parameters.
	 * 
	 * @param other
	 *            the other mime type to compare to
	 * @return whether the two mime types have the same type and subtype
	 */
	boolean equalsTypeAndSubtype(MimeType other);

	/**
	 * Unlike {@link Collection#contains(Object)} which relies on
	 * {@link MimeType#equals(Object)}, this method only checks the type and the
	 * subtype, but otherwise ignores parameters.
	 * 
	 * @param mimeTypes
	 *            the list of mime types to perform the check against
	 * @return whether the list contains the given mime type
	 */
	boolean isPresentIn(Collection<? extends MimeType> mimeTypes);
}
