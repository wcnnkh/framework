package scw.redis.connection;

import java.util.List;

public interface RedisSetsCommands {
	/**
	 * Add the specified members to the set stored at key. Specified members
	 * that are already a member of this set are ignored. If key does not exist,
	 * a new set is created before adding the specified members.
	 * 
	 * An error is returned when the value stored at key is not a set.
	 * 
	 * @param key
	 * @param members
	 * @return Integer reply: the number of elements that were added to the set,
	 *         not including all the elements already present in the set.
	 */
	Integer sadd(byte[] key, byte[]... members);

	/**
	 * https://redis.io/commands/scard<br/>
	 * <br/>
	 * Returns the set cardinality (number of elements) of the set stored at
	 * key.
	 * 
	 * @param key
	 * @return Integer reply: the cardinality (number of elements) of the set,
	 *         or 0 if key does not exist.
	 */
	Integer scard(byte[] key);

	/**
	 * https://redis.io/commands/sdiff<br/>
	 * <br/>
	 * Returns the members of the set resulting from the difference between the
	 * first set and all the successive sets.
	 * 
	 * @param keys
	 * @return Array reply: list with members of the resulting set.
	 */
	List<byte[]> sdiff(byte[]... keys);

	/**
	 * https://redis.io/commands/sdiffstore<br/>
	 * <br/>
	 * This command is equal to SDIFF, but instead of returning the resulting
	 * set, it is stored in destination.
	 * 
	 * If destination already exists, it is overwritten.
	 * 
	 * @param destination
	 * @param keys
	 * @return Integer reply: the number of elements in the resulting set.
	 */
	Integer sdiffstore(byte[] destination, byte[]... keys);

	/**
	 * https://redis.io/commands/sinter<br/>
	 * <br/>
	 * Returns the members of the set resulting from the intersection of all the
	 * given sets.
	 * 
	 * @param keys
	 * @return Array reply: list with members of the resulting set.
	 */
	List<byte[]> sinter(byte[]... keys);

	/**
	 * https://redis.io/commands/sinterstore<br/>
	 * <br/>
	 * 
	 * @param destination
	 * @param keys
	 * @return Integer reply: the number of elements in the resulting set.
	 */
	Integer sinterstore(byte[] destination, byte[]... keys);

	/**
	 * https://redis.io/commands/sismember<br/>
	 * <br/>
	 * Returns if member is a member of the set stored at key.
	 * 
	 * @param key
	 * @param member
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the element is a member of the set. 0 if the element is not
	 *         a member of the set, or if key does not exist.
	 */
	Integer sismember(byte[] key, byte[] member);

	/**
	 * https://redis.io/commands/smembers<br/>
	 * <br/>
	 * Returns all the members of the set value stored at key.
	 * 
	 * This has the same effect as running SINTER with one argument key.
	 * 
	 * @param key
	 * @return Array reply: all elements of the set.
	 */
	List<byte[]> smembers(byte[] key);

	/**
	 * https://redis.io/commands/smismember<br/>
	 * <br/>
	 * Returns whether each member is a member of the set stored at key.
	 * 
	 * For every member, 1 is returned if the value is a member of the set, or 0
	 * if the element is not a member of the set or if key does not exist.
	 * 
	 * @param key
	 * @param members
	 * @return Array reply: list representing the membership of the given
	 *         elements, in the same order as they are requested.
	 */
	List<byte[]> smismember(byte[] key, byte[]... members);

	/**
	 * https://redis.io/commands/smove<br/>
	 * <br/>
	 * Move member from the set at source to the set at destination. This
	 * operation is atomic. In every given moment the element will appear to be
	 * a member of source or destination for other clients.
	 * 
	 * If the source set does not exist or does not contain the specified
	 * element, no operation is performed and 0 is returned. Otherwise, the
	 * element is removed from the source set and added to the destination set.
	 * When the specified element already exists in the destination set, it is
	 * only removed from the source set.
	 * 
	 * An error is returned if source or destination does not hold a set value.
	 * 
	 * @param source
	 * @param destination
	 * @param member
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the element is moved. 0 if the element is not a member of
	 *         source and no operation was performed.
	 */
	Integer smove(byte[] source, byte[] destination, byte[] member);

	/**
	 * https://redis.io/commands/spop<br/>
	 * <br/>
	 * Removes and returns one or more random members from the set value store
	 * at key.
	 * 
	 * This operation is similar to SRANDMEMBER, that returns one or more random
	 * elements from a set but does not remove it.
	 * 
	 * By default, the command pops a single member from the set. When provided
	 * with the optional count argument, the reply will consist of up to count
	 * members, depending on the set's cardinality.
	 * 
	 * @param key
	 * @param count
	 *            History >= 3.2: Added the count argument.
	 * @return When called without the count argument:
	 * 
	 *         Bulk string reply: the removed member, or nil when key does not
	 *         exist.
	 * 
	 *         When called with the count argument:
	 * 
	 *         Array reply: the removed members, or an empty array when key does
	 *         not exist.
	 */
	List<byte[]> spop(byte[] key, Integer count);

	/**
	 * https://redis.io/commands/srandmember<br/>
	 * <br/>
	 * When called with just the key argument, return a random element from the
	 * set value stored at key.
	 * 
	 * If the provided count argument is positive, return an array of distinct
	 * elements. The array's length is either count or the set's cardinality
	 * (SCARD), whichever is lower.
	 * 
	 * If called with a negative count, the behavior changes and the command is
	 * allowed to return the same element multiple times. In this case, the
	 * number of returned elements is the absolute value of the specified count.
	 * 
	 * @param key
	 * @param count
	 *            >= 2.6.0: Added the optional count argument.
	 * @return Bulk string reply: without the additional count argument, the
	 *         command returns a Bulk Reply with the randomly selected element,
	 *         or nil when key does not exist.
	 * 
	 *         Array reply: when the additional count argument is passed, the
	 *         command returns an array of elements, or an empty array when key
	 *         does not exist.
	 */
	List<byte[]> srandmember(byte[] key, Integer count);

	/**
	 * https://redis.io/commands/srem<br/>
	 * <br/>
	 * Remove the specified members from the set stored at key. Specified
	 * members that are not a member of this set are ignored. If key does not
	 * exist, it is treated as an empty set and this command returns 0.
	 * 
	 * An error is returned when the value stored at key is not a set.
	 * 
	 * @param key
	 * @param members
	 *            >= 2.4: Accepts multiple member arguments. Redis versions
	 *            older than 2.4 can only remove a set member per call.
	 * @return Integer reply: the number of members that were removed from the
	 *         set, not including non existing members.
	 */
	Integer srem(byte[] key, byte[]... members);

	/**
	 * https://redis.io/commands/sunion<br/>
	 * <br/>
	 * Returns the members of the set resulting from the union of all the given
	 * sets.
	 * 
	 * @param keys
	 * @return Array reply: list with members of the resulting set.
	 */
	List<byte[]> sunion(byte[]... keys);

	/**
	 * https://redis.io/commands/sunionstore<br/>
	 * <br/>
	 * This command is equal to SUNION, but instead of returning the resulting
	 * set, it is stored in destination.
	 * 
	 * If destination already exists, it is overwritten.
	 * 
	 * @param destination
	 * @param keys
	 * @return Integer reply: the number of elements in the resulting set.
	 */
	Integer sunionstore(byte[] destination, byte[]... keys);
}
