package run.soeasy.framework.redis.sequences;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.function.LongSupplier;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import lombok.NonNull;
import run.soeasy.framework.context.ContextUtils;
import run.soeasy.framework.core.StringUtils;

/**
 * 基于 Spring Data Redis 的 {@link StringRedisTemplate} 实现的分布式序列生成器。
 * <p>
 * 该类是 {@link AbstractRedisSequence} 的具体实现，利用 {@link StringRedisTemplate} 来与 Redis 进行交互。
 * 其核心优势在于通过预编译的 Lua 脚本保证了在分布式环境下序列生成的原子性，避免了并发问题。
 * <p>
 * 核心特性：
 * <ul>
 *     <li>依赖 Spring Data Redis 的 {@link StringRedisTemplate} 进行数据操作</li>
 *     <li>在类加载时预编译并缓存 Lua 脚本，提升执行效率</li>
 *     <li>所有序列操作（递增、设置、删除）均通过 Redis 命令或 Lua 脚本实现，确保原子性</li>
 * </ul>
 *
 * @author soeasy.run
 */
public class StringRedisTemplateSequence extends AbstractRedisSequence {

    /**
     * Spring Data Redis 提供的字符串操作模板，用于与 Redis 进行交互，不能为空。
     */
    @NonNull
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 用于生成下一个序列值的预编译 Redis Lua 脚本对象。
     * <p>
     * 该脚本被设计为原子操作，用于处理序列的递增逻辑，支持在序列不存在时根据是否提供默认值来决定是初始化还是返回 null。
     */
    private static final DefaultRedisScript<Long> NEXT_VALUE_REDIS_SCRIPT = new DefaultRedisScript<>();

    static {
        // 构建 Lua 脚本文件在 classpath 下的路径
        String location = AbstractRedisSequence.class.getPackage().getName().replace(".", "/")
                + "/stringRedisTemplate.lua";
        String script;
        try {
            // 通过 XUtils 工具类读取脚本内容
            script = ContextUtils.getResource(location).decode(StandardCharsets.UTF_8.name()).readToString();
        } catch (IOException e) {
            // 如果脚本文件读取失败，抛出致命异常，终止类加载
            throw new IllegalStateException("无法获取脚本资源:" + location);
        }
        // 设置脚本内容和返回值类型
        NEXT_VALUE_REDIS_SCRIPT.setScriptText(script);
        NEXT_VALUE_REDIS_SCRIPT.setResultType(Long.class);
    }

    /**
     * 构造函数，创建一个新的 StringRedisTemplateSequence 实例。
     *
     * @param key                  序列在 Redis 中存储的键名，不能为空
     * @param initialValueSupplier 当序列首次创建时，用于提供初始值的 {@link LongSupplier}，不能为空
     * @param stringRedisTemplate  Spring Data Redis 的字符串操作模板，不能为空
     */
    public StringRedisTemplateSequence(String key, LongSupplier initialValueSupplier,
                                       StringRedisTemplate stringRedisTemplate) {
        super(key, initialValueSupplier);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 具体实现：调用预编译的 Lua 脚本来执行原子递增操作。
     * <p>
     * Lua 脚本会处理以下逻辑：
     * <ol>
     *     <li>检查键是否存在。</li>
     *     <li>如果存在，将其值增加指定的步长 {@code step} 并返回新值。</li>
     *     <li>如果不存在：
     *         <ul>
     *             <li>若提供了 {@code defaultValue}，则将键值设为 {@code defaultValue} 并返回它。</li>
     *             <li>若未提供 {@code defaultValue}，则返回 {@code null}。</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * @param key          序列键
     * @param step         递增步长
     * @param defaultValue 序列不存在时的默认初始值，可为 {@code null}
     * @return 递增后的序列值，或默认值，或 {@code null}
     */
    @Override
    protected Long executeNextValue(String key, Long step, Long defaultValue) {
        return stringRedisTemplate.execute(NEXT_VALUE_REDIS_SCRIPT, Arrays.asList(key), step.toString(), defaultValue == null? null:defaultValue.toString());
    }

    /**
     * {@inheritDoc}
     * <p>
     * 具体实现：使用 {@link StringRedisTemplate} 的 {@code opsForValue().get(key)} 方法获取字符串值，
     * 并将其转换为 {@link Long}。如果值为空或转换失败，返回 {@code null}。
     *
     * @param key 序列键
     * @return 长整型值；如果键不存在、值为空或无法转换为长整型，则返回 {@code null}
     */
    @Override
    protected Long getKeyAsLong(String key) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return Long.parseLong(value);
    }

    /**
     * {@inheritDoc}
     * <p>
     * 具体实现：使用 {@link StringRedisTemplate} 的 {@code opsForValue().set(key, value)} 方法，
     * 将 {@link Long} 值转换为字符串后存入 Redis。
     *
     * @param key   序列键
     * @param value 要设置的长整型值
     */
    @Override
    protected void setKey(String key, long value) {
        stringRedisTemplate.opsForValue().set(key, String.valueOf(value));
    }

    /**
     * {@inheritDoc}
     * <p>
     * 具体实现：使用 {@link StringRedisTemplate} 的 {@code delete(key)} 方法删除 Redis 中的键。
     *
     * @param key 序列键
     */
    @Override
    protected void deleteKey(String key) {
        stringRedisTemplate.delete(key);
    }
}