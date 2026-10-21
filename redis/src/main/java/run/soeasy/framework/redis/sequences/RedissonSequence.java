package run.soeasy.framework.redis.sequences;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.LongSupplier;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import lombok.NonNull;
import run.soeasy.framework.context.ContextUtils;

/**
 * 基于 Redisson 客户端实现的分布式序列生成器。
 * <p>
 * 该类继承自 {@link AbstractRedisSequence}，利用 Redisson 框架提供的高级功能（如分布式原子长整型 {@link RAtomicLong}
 * 和 Lua 脚本执行）来实现高效、线程安全的序列生成。Redisson 本身提供了强大的分布式锁和原子操作支持，
 * 确保了在高并发和分布式环境下序列的唯一性和一致性。
 * <p>
 * 核心特性：
 * <ul>
 *     <li>依赖 Redisson 客户端，充分利用其分布式特性和连接池管理。</li>
 *     <li>通过预加载的 Lua 脚本执行复杂的递增逻辑，保证操作的原子性。</li>
 *     <li>使用 {@link RAtomicLong} 进行简单的 get/set/delete 操作，API 友好且高效。</li>
 *     <li>在序列键不存在时能正确返回 {@code null}，避免了使用默认值 0 带来的歧义。</li>
 * </ul>
 *
 * @author soeasy.run
 */
public class RedissonSequence extends AbstractRedisSequence {

    /**
     * Lua脚本1：用于执行不带默认值的序列递增操作。
     * <p>
     * 脚本逻辑：如果键存在，则递增指定步长并返回新值；如果键不存在，则返回 {@code null}。
     */
    private static final String SCRIPT1;

    /**
     * Lua脚本2：用于执行带默认值的序列递增操作。
     * <p>
     * 脚本逻辑：如果键存在，则递增指定步长并返回新值；如果键不存在，则设置为默认值并返回该默认值。
     */
    private static final String SCRIPT2;

    static {
        // 加载第一个Lua脚本（无默认值）
        String location = AbstractRedisSequence.class.getPackage().getName().replace(".", "/")
                + "/redissonSequence1.lua";
        try {
            SCRIPT1 = ContextUtils.getResource(location).decode(StandardCharsets.UTF_8.name()).readToString();
        } catch (IOException e) {
            throw new IllegalStateException("无法获取脚本资源:" + location);
        }
    }

    static {
        // 加载第二个Lua脚本（带默认值）
        String location = AbstractRedisSequence.class.getPackage().getName().replace(".", "/")
                + "/redissonSequence2.lua";
        try {
            SCRIPT2 = ContextUtils.getResource(location).decode(StandardCharsets.UTF_8.name()).readToString();
        } catch (IOException e) {
            throw new IllegalStateException("无法获取脚本资源:" + location);
        }
    }

    /**
     * Redisson 客户端实例，用于与 Redis 集群/节点进行交互，不能为空。
     */
    @NonNull
    private final RedissonClient redissonClient;

    /**
     * 构造函数，创建一个新的 RedissonSequence 实例。
     *
     * @param key                  序列在 Redis 中存储的键名，不能为空。
     * @param initialValueSupplier 当序列首次创建时，用于提供初始值的 {@link LongSupplier}，不能为空。
     * @param redissonClient       Redisson 客户端实例，不能为空。
     */
    public RedissonSequence(String key, LongSupplier initialValueSupplier, RedissonClient redissonClient) {
        super(key, initialValueSupplier);
        this.redissonClient = redissonClient;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 具体实现：调用 {@code SCRIPT1} 来执行原子递增操作。
     * 如果序列键不存在，此方法会返回 {@code null}。
     *
     * @param key  序列键
     * @param step 递增步长
     * @return 递增后的序列值，如果键不存在则返回 {@code null}
     */
    @Override
    protected Long executeNextValue(String key, Long step) {
        return redissonClient.getScript(StringCodec.INSTANCE).eval(Mode.READ_WRITE, SCRIPT1, ReturnType.INTEGER,
                Arrays.asList(key), step);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 具体实现：调用 {@code SCRIPT2} 来执行原子递增操作。
     * 如果序列键不存在，它会被初始化为 {@code defaultValue}，并返回该值。
     *
     * @param key          序列键
     * @param step         递增步长
     * @param defaultValue 序列不存在时的默认初始值
     * @return 递增后的序列值，或初始化后的默认值
     */
    @Override
    protected Long executeNextValue(String key, Long step, Long defaultValue) {
        return redissonClient.getScript(StringCodec.INSTANCE).eval(Mode.READ_WRITE, SCRIPT2, ReturnType.INTEGER,
                Arrays.asList(key), step, defaultValue);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 具体实现：通过 Redisson 的 {@link RAtomicLong} 来获取值。
     * 为了严格遵守契约（键不存在时返回 {@code null}），此方法会先调用 {@link RAtomicLong#isExists()}
     * 检查键是否存在。这是一个最佳实践，可以避免将不存在的键的默认值（0）误判为有效序列值。
     *
     * @param key 序列键
     * @return 长整型值；如果键不存在，则返回 {@code null}
     */
    @Override
    protected Long getKeyAsLong(String key) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong(key);
        if (atomicLong.isExists()) {
            return atomicLong.get();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 具体实现：使用 Redisson 的 {@link RAtomicLong#set(long)} 方法来设置值，该操作是原子的。
     *
     * @param key   序列键
     * @param value 要设置的长整型值
     */
    @Override
    protected void setKey(String key, long value) {
        redissonClient.getAtomicLong(key).set(value);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 具体实现：使用 Redisson 的 {@link RAtomicLong#delete()} 方法来删除键，该操作是原子的。
     *
     * @param key 序列键
     */
    @Override
    protected void deleteKey(String key) {
        redissonClient.getAtomicLong(key).delete();
    }
}