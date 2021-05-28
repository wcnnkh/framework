package scw.redis.connection;

import java.util.List;

public interface RedisListsCommands {
	static enum MovePosition {
		LEFT, RIGHT
	}

	/**
	 * https://redis.io/commands/blmove<br/>
	 * <br/>
	 * BLMOVE is the blocking variant of LMOVE. When source contains elements, this
	 * command behaves exactly like LMOVE. When used inside a MULTI/EXEC block, this
	 * command behaves exactly like LMOVE. When source is empty, Redis will block
	 * the connection until another client pushes to it or until timeout is reached.
	 * A timeout of zero can be used to block indefinitely.
	 * 
	 * This command comes in place of the now deprecated BRPOPLPUSH. Doing BLMOVE
	 * RIGHT LEFT is equivalent.
	 * 
	 * See LMOVE for more information.
	 * 
	 * @param source
	 * @param destination
	 * @param option1
	 * @param option2
	 * @param timout
	 * @return Bulk string reply: the element being popped from source and pushed to
	 *         destination. If timeout is reached, a Null reply is returned.
	 */
	byte[] blmove(byte[] source, byte[] destination, MovePosition option1, MovePosition option2, int timout);

	/**
	 * https://redis.io/commands/blpop<br/>
	 * <br/>
	 * 
	 * <br/>
	 * History >= 6.0: timeout is interpreted as a double instead of an integer.
	 * <br/>
	 * <br/>
	 * 
	 * @param keys
	 * @param timeout
	 * @return Array reply: specifically:
	 * 
	 *         A nil multi-bulk when no element could be popped and the timeout
	 *         expired. A two-element multi-bulk with the first element being the
	 *         name of the key where an element was popped and the second element
	 *         being the value of the popped element.
	 */
	List<byte[]> blpop(List<byte[]> keys, int timeout);

	/**
	 * https://redis.io/commands/brpop <br/>
	 * <br/>
	 * BRPOP is a blocking list pop primitive. It is the blocking version of RPOP
	 * because it blocks the connection when there are no elements to pop from any
	 * of the given lists. An element is popped from the tail of the first list that
	 * is non-empty, with the given keys being checked in the order that they are
	 * given.
	 * 
	 * See the BLPOP documentation for the exact semantics, since BRPOP is identical
	 * to BLPOP with the only difference being that it pops elements from the tail
	 * of a list instead of popping from the head.
	 * 
	 * @param keys
	 * @param timeout
	 * @return Array reply: specifically:
	 * 
	 *         A nil multi-bulk when no element could be popped and the timeout
	 *         expired. A two-element multi-bulk with the first element being the
	 *         name of the key where an element was popped and the second element
	 *         being the value of the popped element.
	 */
	List<byte[]> brpop(List<byte[]> keys, int timeout);

	/**
	 * https://redis.io/commands/brpoplpush <br/>
	 * 
	 * @param source
	 * @param destination
	 * @param timout      History >= 6.0: timeout is interpreted as a double instead
	 *                    of an integer.
	 * @return Bulk string reply: the element being popped from source and pushed to
	 *         destination. If timeout is reached, a Null reply is returned.
	 */
	byte[] brpoplpush(byte[] source, byte[] destination, int timout);

	/**
	 * Returns the element at index index in the list stored at key. The index is
	 * zero-based, so 0 means the first element, 1 the second element and so on.
	 * Negative indices can be used to designate elements starting at the tail of
	 * the list. Here, -1 means the last element, -2 means the penultimate and so
	 * forth.
	 * 
	 * When the value at key is not a list, an error is returned.
	 * 
	 * @param key
	 * @param index
	 * @return Bulk string reply: the requested element, or nil when index is out of
	 *         range.
	 */
	byte[] lindex(byte[] key, int index);

	static enum InsertPosition {
		BEFORE, AFTER
	}

	/**
	 * https://redis.io/commands/linsert<br/>
	 * <br/>
	 * Inserts element in the list stored at key either before or after the
	 * reference value pivot.
	 * 
	 * When key does not exist, it is considered an empty list and no operation is
	 * performed.
	 * 
	 * An error is returned when key exists but does not hold a list value.
	 * 
	 * @param key
	 * @param position
	 * @param pivot
	 * @param value
	 * @return Integer reply: the length of the list after the insert operation, or
	 *         -1 when the value pivot was not found.
	 */
	Integer linsert(byte[] key, InsertPosition position, byte[] pivot, byte[] value);

	/**
	 * https://redis.io/commands/llen<br/>
	 * <br/>
	 * Returns the length of the list stored at key. If key does not exist, it is
	 * interpreted as an empty list and 0 is returned. An error is returned when the
	 * value stored at key is not a list.
	 * 
	 * @param key
	 * @return Integer reply: the length of the list at key.
	 */
	Integer llen(byte[] key);

	/**
	 * https://redis.io/commands/lmove<br/>
	 * <br/>
	 * Atomically returns and removes the first/last element (head/tail depending on
	 * the wherefrom argument) of the list stored at source, and pushes the element
	 * at the first/last element (head/tail depending on the whereto argument) of
	 * the list stored at destination.
	 * 
	 * For example: consider source holding the list a,b,c, and destination holding
	 * the list x,y,z. Executing LMOVE source destination RIGHT LEFT results in
	 * source holding a,b and destination holding c,x,y,z.
	 * 
	 * If source does not exist, the value nil is returned and no operation is
	 * performed. If source and destination are the same, the operation is
	 * equivalent to removing the first/last element from the list and pushing it as
	 * first/last element of the list, so it can be considered as a list rotation
	 * command (or a no-op if wherefrom is the same as whereto).
	 * 
	 * This command comes in place of the now deprecated RPOPLPUSH. Doing LMOVE
	 * RIGHT LEFT is equivalent.
	 * 
	 * @param source
	 * @param destination
	 * @param position1
	 * @param position2
	 * @return Bulk string reply: the element being popped and pushed.
	 */
	byte[] lmove(byte[] source, byte[] destination, MovePosition position1, MovePosition position2);

	/**
	 * https://redis.io/commands/lpop<br/>
	 * <br/>
	 * Removes and returns the first elements of the list stored at key.
	 * 
	 * By default, the command pops a single element from the beginning of the list.
	 * When provided with the optional count argument, the reply will consist of up
	 * to count elements, depending on the list's length.
	 * 
	 * @param key
	 * @param count
	 * @return When called without the count argument:
	 * 
	 *         Bulk string reply: the value of the first element, or nil when key
	 *         does not exist.
	 * 
	 *         When called with the count argument:
	 * 
	 *         Array reply: the values of the first elements, or nil when key does
	 *         not exist.
	 */
	List<byte[]> lpop(byte[] key, Integer count);

	/**
	 * https://redis.io/commands/lpush<br/>
	 * <br/>
	 * Insert all the specified values at the head of the list stored at key. If key
	 * does not exist, it is created as empty list before performing the push
	 * operations. When key holds a value that is not a list, an error is returned.
	 * 
	 * It is possible to push multiple elements using a single command call just
	 * specifying multiple arguments at the end of the command. Elements are
	 * inserted one after the other to the head of the list, from the leftmost
	 * element to the rightmost element. So for instance the command LPUSH mylist a
	 * b c will result into a list containing c as first element, b as second
	 * element and a as third element.
	 * 
	 * @param key
	 * @param elements
	 * @return Integer reply: the length of the list after the push operations.
	 */
	Integer lpush(byte[] key, byte[]... elements);

	/**
	 * https://redis.io/commands/lpushx<br/>
	 * <br/>
	 * Inserts specified values at the head of the list stored at key, only if key
	 * already exists and holds a list. In contrary to LPUSH, no operation will be
	 * performed when key does not yet exist.
	 * 
	 * @param key
	 * @param elements
	 * @return Integer reply: the length of the list after the push operation.
	 */
	Integer lpushx(byte[] key, byte[]... elements);

	/**
	 * https://redis.io/commands/lrange<br/>
	 * <br/>
	 * 
	 * Returns the specified elements of the list stored at key. The offsets start
	 * and stop are zero-based indexes, with 0 being the first element of the list
	 * (the head of the list), 1 being the next element and so on.
	 * 
	 * @param key
	 * @param start
	 * @param stop
	 * @return Array reply: list of elements in the specified range.
	 */
	List<byte[]> lrange(byte[] key, int start, int stop);

	/**
	 * https://redis.io/commands/lrem<br/>
	 * <br/>
	 * Removes the first count occurrences of elements equal to element from the
	 * list stored at key. The count argument influences the operation in the
	 * following ways:
	 * 
	 * count > 0: Remove elements equal to element moving from head to tail. count <
	 * 0: Remove elements equal to element moving from tail to head. count = 0:
	 * Remove all elements equal to element. For example, LREM list -2 "hello" will
	 * remove the last two occurrences of "hello" in the list stored at list.
	 * 
	 * Note that non-existing keys are treated like empty lists, so when key does
	 * not exist, the command will always return 0.
	 * 
	 * @param key
	 * @param count
	 * @param element
	 * @return Integer reply: the number of removed elements.
	 */
	Integer lrem(byte[] key, int count, byte[] element);

	/**
	 * https://redis.io/commands/lset<br/>
	 * <br/>
	 * Sets the list element at index to element. For more information on the index
	 * argument, see LINDEX.
	 * 
	 * An error is returned for out of range indexes.
	 * 
	 * @param key
	 * @param index
	 * @param element
	 * @return Simple string reply
	 */
	byte[] lset(byte[] key, int index, byte[] element);

	/**
	 * https://redis.io/commands/ltrim
	 * 
	 * @param key
	 * @param start
	 * @param stop
	 * @return Simple string reply
	 */
	byte[] ltrim(byte[] key, int start, int stop);

	/**
	 * https://redis.io/commands/rpop<br/>
	 * <br/>
	 * Removes and returns the last elements of the list stored at key.
	 * 
	 * By default, the command pops a single element from the end of the list. When
	 * provided with the optional count argument, the reply will consist of up to
	 * count elements, depending on the list's length.
	 * 
	 * History >= 6.2: Added the count argument.
	 * 
	 * @param key
	 * @param count
	 * @return When called without the count argument:
	 * 
	 *         Bulk string reply: the value of the last element, or nil when key
	 *         does not exist.
	 * 
	 *         When called with the count argument:
	 * 
	 *         Array reply: the values of the last elements, or nil when key does
	 *         not exist.
	 */
	List<byte[]> rpop(byte[] key, Integer count);

	/**
	 * https://redis.io/commands/rpoplpush<br/>
	 * <br/>
	 * Atomically returns and removes the last element (tail) of the list stored at
	 * source, and pushes the element at the first element (head) of the list stored
	 * at destination.
	 * 
	 * For example: consider source holding the list a,b,c, and destination holding
	 * the list x,y,z. Executing RPOPLPUSH results in source holding a,b and
	 * destination holding c,x,y,z.
	 * 
	 * If source does not exist, the value nil is returned and no operation is
	 * performed. If source and destination are the same, the operation is
	 * equivalent to removing the last element from the list and pushing it as first
	 * element of the list, so it can be considered as a list rotation command.
	 * 
	 * As per Redis 6.2.0, RPOPLPUSH is considered deprecated. Please prefer LMOVE
	 * in new code.
	 * 
	 * @param source
	 * @param destination
	 * @return Bulk string reply: the element being popped and pushed.
	 */
	byte[] rpoplpush(byte[] source, byte[] destination);

	/**
	 * https://redis.io/commands/rpush<br/>
	 * <br/>
	 * Insert all the specified values at the tail of the list stored at key. If key
	 * does not exist, it is created as empty list before performing the push
	 * operation. When key holds a value that is not a list, an error is returned.
	 * 
	 * It is possible to push multiple elements using a single command call just
	 * specifying multiple arguments at the end of the command. Elements are
	 * inserted one after the other to the tail of the list, from the leftmost
	 * element to the rightmost element. So for instance the command RPUSH mylist a
	 * b c will result into a list containing a as first element, b as second
	 * element and c as third element.
	 * 
	 * @param key
	 * @param elements
	 * @return Integer reply: the length of the list after the push operation.
	 */
	Integer rpush(byte[] key, byte[]... elements);

	/**
	 * https://redis.io/commands/rpushx<br/>
	 * <br/>
	 * Inserts specified values at the tail of the list stored at key, only if key
	 * already exists and holds a list. In contrary to RPUSH, no operation will be
	 * performed when key does not yet exist.
	 * 
	 * @param key
	 * @param elements
	 * @return Integer reply: the length of the list after the push operation.
	 */
	Integer rpushx(byte[] key, byte[]... elements);
}
