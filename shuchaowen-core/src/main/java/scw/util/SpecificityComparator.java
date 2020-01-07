package scw.util;

import java.util.Comparator;

/**
 * Comparator to sort {@link MimeType MimeTypes} in order of specificity.
 *
 * @param <T>
 *            the type of mime types that may be compared by this comparator
 */
public class SpecificityComparator<T extends MimeType> implements Comparator<T> {

	public int compare(T mimeType1, T mimeType2) {
		if (mimeType1.isWildcardType() && !mimeType2.isWildcardType()) { // */*
																			// <
																			// audio/*
			return 1;
		} else if (mimeType2.isWildcardType() && !mimeType1.isWildcardType()) { // audio/*
																				// >
																				// */*
			return -1;
		} else if (!mimeType1.getType().equals(mimeType2.getType())) { // audio/basic
																		// ==
																		// text/html
			return 0;
		} else { // mediaType1.getType().equals(mediaType2.getType())
			if (mimeType1.isWildcardSubtype() && !mimeType2.isWildcardSubtype()) { // audio/*
																					// <
																					// audio/basic
				return 1;
			} else if (mimeType2.isWildcardSubtype() && !mimeType1.isWildcardSubtype()) { // audio/basic
																							// >
																							// audio/*
				return -1;
			} else if (!mimeType1.getSubtype().equals(mimeType2.getSubtype())) { // audio/basic
																					// ==
																					// audio/wave
				return 0;
			} else { // mediaType2.getSubtype().equals(mediaType2.getSubtype())
				return compareParameters(mimeType1, mimeType2);
			}
		}
	}

	protected int compareParameters(T mimeType1, T mimeType2) {
		int paramsSize1 = mimeType1.getParameters().size();
		int paramsSize2 = mimeType2.getParameters().size();
		return Integer.compare(paramsSize2, paramsSize1); // audio/basic;level=1
															// < audio/basic
	}
}
