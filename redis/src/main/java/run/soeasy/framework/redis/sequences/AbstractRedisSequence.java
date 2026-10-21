package run.soeasy.framework.redis.sequences;

import java.util.function.LongSupplier;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import run.soeasy.framework.commons.sequences.PersistentSequence;

/**
 * 基于 Redis 的分布式序列生成器抽象基类。
 * <p>
 * 该类作为分布式序列生成器的骨架，统一封装了序列生成过程中的公共逻辑，例如参数校验、 初始值获取、日志记录等。具体的 Redis 底层操作（如执行 Lua
 * 脚本、获取/设置键值对） 则由子类通过实现抽象方法来完成。
 * <p>
 * 核心特性包括：
 * <ul>
 * <li>支持自定义初始值提供者 {@link LongSupplier}</li>
 * <li>内置步长参数校验，确保步长为正数</li>
 * <li>提供序列重置、强制设置当前值等管理功能</li>
 * <li>通过模板方法模式，将不变的流程与可变的 Redis 操作解耦</li>
 * </ul>
 *
 * @author soeasy.run
 */
@Slf4j
@RequiredArgsConstructor
@Getter
public abstract class AbstractRedisSequence implements PersistentSequence {

	/**
	 * 序列在 Redis 中存储的键名，用于唯一标识一个序列。
	 */
	@NonNull
	protected final String key;

	/**
	 * 序列初始值的提供者。当序列首次被创建（即键不存在）时， 会通过此 {@link LongSupplier} 获取初始值。
	 */
	@NonNull
	protected final LongSupplier initialValueSupplier;

	/**
	 * {@inheritDoc}
	 * <p>
	 * 具体实现逻辑：
	 * <ol>
	 * <li>尝试从 Redis 中获取当前序列的值</li>
	 * <li>如果值存在，则直接返回</li>
	 * <li>如果值不存在，则调用 {@link #initialValueSupplier} 获取初始值并返回</li>
	 * </ol>
	 * 注意：此方法仅返回当前值，不会触发序列递增。
	 */
	@Override
	public long getAsLong() {
		Long currentValue = getCurrentValue();
		if (currentValue != null) {
			return currentValue;
		}
		return initialValueSupplier.getAsLong();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * 具体实现逻辑：
	 * <ol>
	 * <li>校验步长{@code step}必须为大于0的正整数，否则抛出{@link IllegalArgumentException}</li>
	 * <li>根据是否提供默认值{@code defaultValue}，调用不同的子类实现：
	 * <ul>
	 * <li>如果{@code defaultValue}为{@code null}，调用{@link #executeNextValue(String, Long)}</li>
	 * <li>如果{@code defaultValue}不为{@code null}，调用{@link #executeNextValue(String, Long, Long)}</li>
	 * </ul>
	 * </li>
	 * <li>如果生成成功且日志级别为{@code TRACE}，记录序列生成信息</li>
	 * </ol>
	 *
	 * @param step         序列递增的步长，必须大于0
	 * @param defaultValue 序列不存在时的默认初始值，可为{@code null}
	 * @return 递增后的序列值，若执行失败可能返回{@code null}
	 * @throws IllegalArgumentException 如果步长{@code step}小于等于0
	 */
	@Override
	public Long next(@NonNull Long step, Long defaultValue) {
		if (step <= 0) {
			throw new IllegalArgumentException("步长必须是大于0的正整数");
		}

		Long result = defaultValue == null ? executeNextValue(key, step) : executeNextValue(key, step, defaultValue);
		if (result != null && log.isTraceEnabled()) {
			log.trace("序列[{}]生成下一个值：{}", key, result);
		}
		return result;
	}

	/**
	 * 重置当前序列。
	 * <p>
	 * 该方法会删除 Redis 中存储序列的键。重置后，下一次调用{@link #next(Long, Long)}方法时，
	 * 序列将重新初始化（使用初始值提供者或默认值）。
	 */
	public void reset() {
		deleteKey(key);
		log.info("序列[{}]已重置（删除键）", key);
	}

	/**
	 * 强制设置序列的当前值。
	 * <p>
	 * 此方法会直接覆盖 Redis 中存储的序列值，而不进行任何递增操作。
	 * <strong>警告：</strong>该操作会破坏序列的连续性，仅在特殊场景下使用，例如数据修复。
	 *
	 * @param value 要设置的新值
	 */
	public void setValue(long value) {
		setKey(key, value);
		log.info("序列[{}]已强制设置值：{}", key, value);
	}

	/**
	 * 获取当前序列的当前值。
	 * <p>
	 * 与{@link #getAsLong()}不同，此方法在序列键不存在或值非法时，会返回{@code null}而非初始值。
	 *
	 * @return 当前序列值，如果键不存在或值非法则返回{@code null}
	 */
	public Long getCurrentValue() {
		return getKeyAsLong(key);
	}

	/**
	 * 模板方法：执行序列递增操作（无默认值）。
	 * <p>
	 * 如果序列不存在，则返回{@code null}。具体实现由子类完成。
	 *
	 * @param key  序列键
	 * @param step 递增步长
	 * @return 递增后的序列值，若序列不存在则返回{@code null}
	 */
	protected Long executeNextValue(String key, Long step) {
		return executeNextValue(key, step, null);
	}

	/**
	 * 模板方法：执行序列递增操作（带默认值）。
	 * <p>
	 * 如果序列不存在，则使用提供的{@code defaultValue}作为初始值，并在此基础上递增。 具体实现由子类完成。
	 *
	 * @param key          序列键
	 * @param step         递增步长
	 * @param defaultValue 序列不存在时的默认初始值
	 * @return 递增后的序列值
	 */
	protected abstract Long executeNextValue(String key, Long step, Long defaultValue);

	/**
	 * 抽象方法：从 Redis 中获取指定键的长整型值。
	 * <p>
	 * 子类需实现此方法来提供具体的 Redis 读取逻辑。
	 *
	 * @param key 键名
	 * @return 长整型值；如果键不存在或值无法转换为长整型，则返回{@code null}
	 */
	protected abstract Long getKeyAsLong(String key);

	/**
	 * 抽象方法：向 Redis 中设置指定键的长整型值。
	 * <p>
	 * 子类需实现此方法来提供具体的 Redis 写入逻辑。
	 *
	 * @param key   键名
	 * @param value 要设置的长整型值
	 */
	protected abstract void setKey(String key, long value);

	/**
	 * 抽象方法：删除 Redis 中指定的键。
	 * <p>
	 * 子类需实现此方法来提供具体的 Redis 删除逻辑。
	 *
	 * @param key 键名
	 */
	protected abstract void deleteKey(String key);
}