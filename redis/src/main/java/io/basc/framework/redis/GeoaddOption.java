package io.basc.framework.redis;

/**
 * Note: The XX and NX options are mutually exclusive.
 * 
 * @author wcnnkh
 *
 */
public enum GeoaddOption {
	/**
	 * Only update elements that already exist. Never add elements.
	 */
	XX,
	/**
	 * Don't update already existing elements. Always add new elements.
	 */
	NX,
	/**
	 * Modify the return value from the number of new elements added, to the total
	 * number of elements changed (CH is an abbreviation of changed). Changed
	 * elements are new elements added and elements already existing for which the
	 * coordinates was updated. So elements specified in the command line having the
	 * same score as they had in the past are not counted. Note: normally, the
	 * return value of GEOADD only counts the number of new elements added.
	 */
	CH
}