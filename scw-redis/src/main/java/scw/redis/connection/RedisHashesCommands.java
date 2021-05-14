package scw.redis.connection;

import java.util.List;

import scw.util.Pair;

/**
 * https://redis.io/commands#hash
 * 
 * @author shuchaowen
 *
 */
public interface RedisHashesCommands {
	/**
	 * https://redis.io/commands/hdel<br/>
	 * <br/>
	 * Removes the specified fields from the hash stored at key. Specified fields
	 * that do not exist within this hash are ignored. If key does not exist, it is
	 * treated as an empty hash and this command returns 0.
	 * 
	 * @param key
	 * @param fields
	 * @return Integer reply: the number of fields that were removed from the hash,
	 *         not including specified but non existing fields.
	 */
	Integer hdel(byte[] key, byte[]... fields);

	/**
	 * https://redis.io/commands/hexists<br/>
	 * <br/>
	 * Returns if field is an existing field in the hash stored at key.
	 * 
	 * @param key
	 * @param field
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the hash contains field. 0 if the hash does not contain field,
	 *         or key does not exist.
	 */
	Integer hexists(byte[] key, byte[] field);

	/**
	 * https://redis.io/commands/hget<br/>
	 * <br/>
	 * Returns the value associated with field in the hash stored at key.
	 * 
	 * @param key
	 * @param field
	 * @return Bulk string reply: the value associated with field, or nil when field
	 *         is not present in the hash or key does not exist.
	 */
	byte[] hget(byte[] key, byte[] field);

	/**
	 * https://redis.io/commands/hgetall<br/>
	 * <br/>
	 * Returns all fields and values of the hash stored at key. In the returned
	 * value, every field name is followed by its value, so the length of the reply
	 * is twice the size of the hash.
	 * 
	 * @param key
	 * @return Array reply: list of fields and their values stored in the hash, or
	 *         an empty list when key does not exist.
	 */
	List<byte[]> hgetall(byte[] key);

	/**
	 * Increments the number stored at field in the hash stored at key by increment.
	 * If key does not exist, a new key holding a hash is created. If field does not
	 * exist the value is set to 0 before the operation is performed.
	 * 
	 * The range of values supported by HINCRBY is limited to 64 bit signed
	 * integers.
	 * 
	 * @param key
	 * @param field
	 * @param increment
	 * @return Integer reply: the value at field after the increment operation.
	 */
	Long hincrby(byte[] key, byte[] field, long increment);

	/**
	 * 
	 * https://redis.io/commands/hincrbyfloat<br/>
	 * <br/>
	 * Increment the specified field of a hash stored at key, and representing a
	 * floating point number, by the specified increment. If the increment value is
	 * negative, the result is to have the hash field value decremented instead of
	 * incremented. If the field does not exist, it is set to 0 before performing
	 * the operation. An error is returned if one of the following conditions occur:
	 * 
	 * The field contains a value of the wrong type (not a string). The current
	 * field content or the specified increment are not parsable as a double
	 * precision floating point number. The exact behavior of this command is
	 * identical to the one of the INCRBYFLOAT command, please refer to the
	 * documentation of INCRBYFLOAT for further information.
	 * 
	 * @param key
	 * @param field
	 * @param increment
	 * @return Bulk string reply: the value of field after the increment.
	 */
	Float hincrbyfloat(byte[] key, byte[] field, float increment);

	/**
	 * https://redis.io/commands/hkeys<br/>
	 * <br/>
	 * Returns all field names in the hash stored at key.
	 * 
	 * @param key
	 * @return Array reply: list of fields in the hash, or an empty list when key
	 *         does not exist.
	 */
	List<byte[]> hkeys(byte[] key);

	/**
	 * https://redis.io/commands/hlen<br/>
	 * <br/>
	 * Returns the number of fields contained in the hash stored at key.
	 * 
	 * @param key
	 * @return Integer reply: number of fields in the hash, or 0 when key does not
	 *         exist.
	 */
	Integer hlen(byte[] key);

	/**
	 * https://redis.io/commands/hmget<br/>
	 * <br/>
	 * Returns the values associated with the specified fields in the hash stored at
	 * key.
	 * 
	 * For every field that does not exist in the hash, a nil value is returned.
	 * Because non-existing keys are treated as empty hashes, running HMGET against
	 * a non-existing key will return a list of nil values.
	 * 
	 * @param key
	 * @param fields
	 * @return Array reply: list of values associated with the given fields, in the
	 *         same order as they are requested.
	 */
	List<byte[]> hmget(byte[] key, byte[]... fields);

	/**
	 * https://redis.io/commands/hmset<br/>
	 * <br/>
	 * Sets the specified fields to their respective values in the hash stored at
	 * key. This command overwrites any specified fields already existing in the
	 * hash. If key does not exist, a new key holding a hash is created.
	 * 
	 * As per Redis 4.0.0, HMSET is considered deprecated. Please prefer HSET in new
	 * code.
	 * 
	 * @param key
	 * @param pairs
	 * @return Simple string reply
	 */
	byte[] hmset(byte[] key, @SuppressWarnings("unchecked") Pair<byte[], byte[]>... pairs);

	/**
	 * https://redis.io/commands/hrandfield<br/>
	 * <br/>
	 * 
	 * When called with just the key argument, return a random field from the hash
	 * value stored at key.
	 * 
	 * If the provided count argument is positive, return an array of distinct
	 * fields. The array's length is either count or the hash's number of fields
	 * (HLEN), whichever is lower.
	 * 
	 * If called with a negative count, the behavior changes and the command is
	 * allowed to return the same field multiple times. In this case, the number of
	 * returned fields is the absolute value of the specified count.
	 * 
	 * The optional WITHVALUES modifier changes the reply so it includes the
	 * respective values of the randomly selected hash fields.
	 * 
	 * @param key
	 * @param count
	 * @param withValues
	 * @return Bulk string reply: without the additional count argument, the command
	 *         returns a Bulk Reply with the randomly selected field, or nil when
	 *         key does not exist.
	 * 
	 *         Array reply: when the additional count argument is passed, the
	 *         command returns an array of fields, or an empty array when key does
	 *         not exist. If the WITHVALUES modifier is used, the reply is a list
	 *         fields and their values from the hash.
	 */
	List<byte[]> hrandfield(byte[] key, Integer count, Boolean withValues);

	/**
	 * https://redis.io/commands/hset<br/>
	 * <br/>
	 * Sets field in the hash stored at key to value. If key does not exist, a new
	 * key holding a hash is created. If field already exists in the hash, it is
	 * overwritten.
	 * 
	 * As of Redis 4.0.0, HSET is variadic and allows for multiple field/value
	 * pairs.
	 * 
	 * @param key
	 * @param pairs
	 * @return Integer reply: The number of fields that were added.
	 */
	Integer hset(byte[] key, @SuppressWarnings("unchecked") Pair<byte[], byte[]>... pairs);

	/**
	 * https://redis.io/commands/hsetnx<br/>
	 * <br/>
	 * Sets field in the hash stored at key to value, only if field does not yet
	 * exist. If key does not exist, a new key holding a hash is created. If field
	 * already exists, this operation has no effect.
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return Integer reply, specifically:
	 * 
	 *         1 if field is a new field in the hash and value was set. 0 if field
	 *         already exists in the hash and no operation was performed.
	 */
	Integer hsetnx(byte[] key, byte[] field, byte[] value);

	/**
	 * https://redis.io/commands/hstrlen<br/>
	 * <br/>
	 * Returns the string length of the value associated with field in the hash
	 * stored at key. If the key or the field do not exist, 0 is returned.
	 * 
	 * @param key
	 * @param field
	 * @return Integer reply: the string length of the value associated with field,
	 *         or zero when field is not present in the hash or key does not exist
	 *         at all.
	 */
	Integer hstrlen(byte[] key, byte[] field);

	/**
	 * Returns all values in the hash stored at key.
	 * 
	 * @param key
	 * @return Array reply: list of values in the hash, or an empty list when key
	 *         does not exist.
	 */
	List<byte[]> hvals(byte[] key);
}
