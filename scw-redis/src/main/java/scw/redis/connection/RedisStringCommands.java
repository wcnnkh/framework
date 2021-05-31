package scw.redis.connection;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public interface RedisStringCommands<K, V> {
	/**
	 * https://redis.io/commands/append<br/>
	 * <br/>
	 * If key already exists and is a string, this command appends the value at the
	 * end of the string. If key does not exist it is created and set as an empty
	 * string, so APPEND will be similar to SET in this special case.
	 * 
	 * @param key
	 * @param value
	 * @return Integer reply: the length of the string after the append operation.
	 */
	Long append(K key, V value);

	/**
	 * https://redis.io/commands/bitcount<br/>
	 * <br/>
	 * Count the number of set bits (population counting) in a string.
	 * 
	 * By default all the bytes contained in the string are examined. It is possible
	 * to specify the counting operation only in an interval passing the additional
	 * arguments start and end.
	 * 
	 * Like for the GETRANGE command start and end can contain negative values in
	 * order to index bytes starting from the end of the string, where -1 is the
	 * last byte, -2 is the penultimate, and so forth.
	 * 
	 * Non-existent keys are treated as empty strings, so the command will return
	 * zero.
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return Integer reply
	 * 
	 *         The number of bits set to 1.
	 */
	Long bitcount(K key, long start, long end);

	static enum BitOP {
		AND, OR, XOR, NOT;
	}

	/**
	 * https://redis.io/commands/bitop<br/>
	 * <br/>
	 * 
	 * @param operation
	 * @param destkey
	 * @param srcKeys
	 * @return Integer reply
	 * 
	 *         The size of the string stored in the destination key, that is equal
	 *         to the size of the longest input string.
	 */
	Long bitop(BitOP op, K destkey, K... srcKeys);

	/**
	 * https://redis.io/commands/bitpos
	 * 
	 * @param key
	 * @param bit
	 * @param start
	 * @param end
	 * @return Integer reply
	 * 
	 *         The command returns the position of the first bit set to 1 or 0
	 *         according to the request.
	 * 
	 *         If we look for set bits (the bit argument is 1) and the string is
	 *         empty or composed of just zero bytes, -1 is returned.
	 * 
	 *         If we look for clear bits (the bit argument is 0) and the string only
	 *         contains bit set to 1, the function returns the first bit not part of
	 *         the string on the right. So if the string is three bytes set to the
	 *         value 0xff the command BITPOS key 0 will return 24, since up to bit
	 *         23 all the bits are 1.
	 * 
	 *         Basically, the function considers the right of the string as padded
	 *         with zeros if you look for clear bits and specify no range or the
	 *         start argument only.
	 * 
	 *         However, this behavior changes if you are looking for clear bits and
	 *         specify a range with both start and end. If no clear bit is found in
	 *         the specified range, the function returns -1 as the user specified a
	 *         clear range and there are no 0 bits in that range.
	 */
	Long bitpos(K key, boolean bit, Long start, Long end);

	/**
	 * https://redis.io/commands/decr
	 * 
	 * @param key
	 * @return Integer reply: the value of key after the decrement
	 */
	Long decr(K key);

	/**
	 * https://redis.io/commands/decrby
	 * 
	 * @param key
	 * @param decrement
	 * @return Integer reply: the value of key after the decrement
	 */
	Long decrBy(K key, long decrement);

	/**
	 * https://redis.io/commands/get<br/>
	 * <br/>
	 * Get the value of key. If the key does not exist the special value nil is
	 * returned. An error is returned if the value stored at key is not a string,
	 * because GET only handles string values.
	 * 
	 * @param key
	 * @return Bulk string reply: the value of key, or nil when key does not exist.
	 */
	V get(K key);

	/**
	 * https://redis.io/commands/getbit
	 * 
	 * @param key
	 * @param offset
	 * @return Integer reply: the bit value stored at offset.
	 */
	Boolean getbit(K key, Long offset);

	/**
	 * https://redis.io/commands/getdel<br/>
	 * <br/>
	 * 
	 * Get the value of key and delete the key. This command is similar to GET,
	 * except for the fact that it also deletes the key on success (if and only if
	 * the key's value type is a string).
	 * 
	 * @param key
	 * @return Bulk string reply: the value of key, nil when key does not exist, or
	 *         an error if the key's value type isn't a string.
	 */
	V getdel(K key);

	static enum ExpireOption {
		/**
		 * seconds -- Set the specified expire time, in seconds.
		 */
		EX,
		/**
		 * milliseconds -- Set the specified expire time, in milliseconds.
		 */
		PX,
		/**
		 * timestamp-seconds -- Set the specified Unix time at which the key will
		 * expire, in seconds
		 */
		EXAT,
		/**
		 * timestamp-milliseconds -- Set the specified Unix time at which the key will
		 * expire, in milliseconds.
		 */
		PXAT,

		/**
		 * Remove the time to live associated with the key.
		 */
		PERSIST
	}
	
	/**
	 * https://redis.io/commands/getex<br/>
	 * <br/>
	 * Get the value of key and optionally set its expiration. GETEX is similar to
	 * GET, but is a write command with additional options.
	 * 
	 * @param key
	 * @param option
	 * @param time
	 * @return Bulk string reply: the value of key, or nil when key does not exist.
	 */
	V getEx(K key, ExpireOption option, Long time);

	/**
	 * https://redis.io/commands/getrange<br/>
	 * <br/>
	 * 
	 * @param key
	 * @return Bulk string reply
	 */
	V getrange(K key, long startOffset, long endOffset);

	/**
	 * https://redis.io/commands/getset<br/>
	 * <br/>
	 * Atomically sets key to value and returns the old value stored at key. Returns
	 * an error when key exists but does not hold a string value.
	 * 
	 * @param key
	 * @param value
	 * @return Bulk string reply: the old value stored at key, or nil when key did
	 *         not exist.
	 */
	V getset(K key, V value);

	/**
	 * https://redis.io/commands/incr
	 * 
	 * @param key
	 * @return Integer reply: the value of key after the increment
	 */
	Long incr(K key);

	/**
	 * https://redis.io/commands/incrby
	 * 
	 * @param key
	 * @param increment
	 * @return Integer reply: the value of key after the increment
	 */
	Long incrBy(K key, long increment);

	/**
	 * https://redis.io/commands/incrbyfloat
	 * 
	 * @param key
	 * @param increment
	 * @return Bulk string reply: the value of key after the increment.
	 */
	Double incrByFloat(K key, double increment);

	/**
	 * https://redis.io/commands/mget
	 * 
	 * @param keys
	 * @return Array reply: list of values at the specified keys.
	 */
	List<V> mget(K... keys);

	/**
	 * https://redis.io/commands/mset
	 * 
	 * @param pairs
	 * @return Simple string reply: always OK since MSET can't fail.
	 */
	Boolean mset(Map<K, V> pairs);

	/**
	 * https://redis.io/commands/msetnx
	 * 
	 * @param pairs
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the all the keys were set. 0 if no key was set (at least one key
	 *         already existed).
	 */
	Long msetnx(Map<K, V> pairs);

	/**
	 * https://redis.io/commands/psetex<br/>
	 * <br/>
	 * PSETEX works exactly like SETEX with the sole difference that the expire time
	 * is specified in milliseconds instead of seconds.
	 * 
	 * @param key
	 * @param milliseconds
	 * @param value
	 * @return
	 */
	Boolean psetex(K key, long milliseconds, V value);

	/**
	 * https://redis.io/commands/set<br/>
	 * <br/>
	 * Set key to hold the string value. If key already holds a value, it is
	 * overwritten, regardless of its type. Any previous time to live associated
	 * with the key is discarded on successful SET operation.
	 * 
	 * Options The SET command supports a set of options that modify its behavior:
	 * 
	 * EX seconds -- Set the specified expire time, in seconds. PX milliseconds --
	 * Set the specified expire time, in milliseconds. EXAT timestamp-seconds -- Set
	 * the specified Unix time at which the key will expire, in seconds. PXAT
	 * timestamp-milliseconds -- Set the specified Unix time at which the key will
	 * expire, in milliseconds. NX -- Only set the key if it does not already exist.
	 * XX -- Only set the key if it already exist. KEEPTTL -- Retain the time to
	 * live associated with the key. GET -- Return the old value stored at key, or
	 * nil when key did not exist.
	 * 
	 * @return Simple string reply: OK if SET was executed correctly. Bulk string
	 *         reply: when GET option is set, the old value stored at key, or nil
	 *         when key did not exist. Null reply: a Null Bulk Reply is returned if
	 *         the SET operation was not performed because the user specified the NX
	 *         or XX option but the condition was not met, or if the user specified
	 *         the GET option and there was no previous value for the key.
	 */
	Boolean set(K key, V value, ExpireOption option, long time, SetOption setOption, boolean get);

	/**
	 * https://redis.io/commands/setbit<br/>
	 * <br/>
	 * Sets or clears the bit at offset in the string value stored at key.
	 * 
	 * The bit is either set or cleared depending on value, which can be either 0 or
	 * 1.
	 * 
	 * When key does not exist, a new string value is created. The string is grown
	 * to make sure it can hold a bit at offset. The offset argument is required to
	 * be greater than or equal to 0, and smaller than 232 (this limits bitmaps to
	 * 512MB). When the string at key is grown, added bits are set to 0.
	 * 
	 * @param key
	 * @param offset
	 * @param value
	 * @return Integer reply: the original bit value stored at offset.
	 */
	Boolean setbit(K key, long offset, boolean value);

	/**
	 * https://redis.io/commands/setex<br/>
	 * <br/>
	 * Set key to hold the string value and set key to timeout after a given number
	 * of seconds. This command is equivalent to executing the following commands:
	 * 
	 * @param key
	 * @param seconds
	 * @param value
	 * @return Simple string reply
	 * 
	 *         Examples
	 */
	Boolean setex(K key, long seconds, V value);

	/**
	 * https://redis.io/commands/setnx<br/>
	 * <br/>
	 * Set key to hold string value if key does not exist. In that case, it is equal
	 * to SET. When key already holds a value, no operation is performed. SETNX is
	 * short for "SET if Not eXists".
	 * 
	 * @param key
	 * @param value
	 * @return Integer reply, specifically:
	 * 
	 *         1 if the key was set 0 if the key was not set
	 */
	Boolean setNX(K key, V value);

	/**
	 * https://redis.io/commands/setrange<br/>
	 * <br/>
	 * Overwrites part of the string stored at key, starting at the specified
	 * offset, for the entire length of value. If the offset is larger than the
	 * current length of the string at key, the string is padded with zero-bytes to
	 * make offset fit. Non-existing keys are considered as empty strings, so this
	 * command will make sure it holds a string large enough to be able to set value
	 * at offset.
	 * 
	 * @param key
	 * @param offset
	 * @param value
	 * @return Integer reply: the length of the string after it was modified by the
	 *         command.
	 */
	Long setrange(K key, Long offset, V value);

	/**
	 * https://redis.io/commands/strlen<br/>
	 * <br/>
	 * Returns the length of the string value stored at key. An error is returned
	 * when key holds a non-string value.
	 * 
	 * @param key
	 * @return Integer reply: the length of the string at key, or 0 when key does
	 *         not exist.
	 */
	Long strlen(K key);
}
