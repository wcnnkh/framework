package run.soeasy.framework.data;

import java.util.concurrent.atomic.AtomicLong;

import lombok.NonNull;
import run.soeasy.framework.core.collection.CollectionUtils;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.KeyValue;

/**
 * 通用键值型数据访问接口，定义**键（K）-值（V）映射存储**的标准化操作规范，
 * 旨在统一数据库、缓存（如Redis）、键值数据库（如LevelDB）等各类存储的访问逻辑，
 * 降低多存储场景下的适配成本，同时通过强制约束（如键值非空、统一结果格式）提升代码健壮性。
 * 
 * <p>
 * 核心设计目标：
 * <ul>
 * <li><strong>接口统一</strong>：抽象“增删改查”核心操作，屏蔽不同存储的实现差异，上层业务无需关注底层存储类型；</li>
 * <li><strong>约束强制</strong>：通过{@link NonNull}注解和文档约定，确保键（K）和值（V）不可为null，避免空指针异常；</li>
 * <li><strong>结果标准化</strong>：查询结果统一封装为{@link Elements}（可迭代的键值对集合），无匹配时返回空集合而非null，简化结果处理；</li>
 * <li><strong>扩展性兼容</strong>：支持自定义实现类根据存储特性（如数据库主键约束、缓存覆盖策略）调整行为，同时提供默认批量操作实现，减少重复编码。</li>
 * </ul>
 *
 * <p>
 * 通用约束（所有实现类需遵守）：
 * <ul>
 * <li>键（K）需具备**唯一性**：用于唯一标识或定位数据（如数据库主键、缓存Key），同一存储内不可出现“同键不同值”的未定义行为；</li>
 * <li>值（V）需为**业务实体数据**：通常是POJO、DTO等结构化数据，不可为基础类型包装类（如Integer、String），避免与键的语义混淆；</li>
 * <li>操作幂等性（建议）：非插入类操作（如delete、update）建议实现幂等，即重复调用不影响最终结果（如重复删除不存在的键返回false，无副作用）。</li>
 * </ul>
 *
 * @param <K> 键类型（Key Type）：用于唯一标识或定位数据，可为基础类型包装类（如Long、String）或自定义查询条件类（如UserQuery），<strong>不可为null</strong>
 * @param <V> 值类型（Value Type）：实际存储的业务实体数据（如User、Order），<strong>不可为null</strong>
 * @see Elements 统一的结果集合类型，支持迭代和批量处理
 * @see KeyValue 键值对封装类，关联单个键与对应的值
 * @see NonNull Lombok注解，强制参数非空，空值时抛出{@link NullPointerException}
 */
public interface Repository<K, V> {

	/**
	 * 插入单个键值对数据到存储中
	 * <p>
	 * 核心逻辑：通过键（K）定位存储位置，将值（V）与键关联存储，需处理“键已存在”的场景，具体行为由实现类定义：
	 * <ul>
	 * <li>覆盖策略（如缓存）：键已存在时用新值覆盖旧值，返回true；</li>
	 * <li>拒绝策略（如数据库主键）：键已存在时不执行插入，返回false（或抛出约束异常，需在实现类文档中说明）。</li>
	 * </ul>
	 *
	 * @param key   唯一标识数据的键，不可为null（由{@link NonNull}注解强制，空值抛{@link NullPointerException}）
	 * @param value 与键关联的业务实体数据，不可为null（空值抛{@link NullPointerException}）
	 * @return boolean：true表示插入成功（新键添加或已存在键被覆盖）；false表示插入失败（如键冲突且不允许覆盖）
	 * @throws RuntimeException 实现类可抛出存储相关异常（如数据库连接异常、缓存超时），需在具体实现类文档中补充
	 */
	boolean insert(@NonNull K key, @NonNull V value);

	/**
	 * 批量插入键值对数据（默认实现）
	 * <p>
	 * 默认逻辑：遍历{@link Elements}中的每个{@link KeyValue}，调用{@link #insert(K, V)}执行单条插入，
	 * 用{@link AtomicLong}原子计数成功插入的数量（确保多线程环境下计数准确）。
	 * <p>
	 * 扩展建议：复杂场景（如数据库批量插入）可重写此方法，使用底层存储的批量API（如JDBC batch）提升性能，默认实现仅适用于简单场景。
	 *
	 * @param elements 待插入的键值对集合，封装为{@link Elements}（可迭代），每个元素为{@link KeyValue<K, V>}，<strong>不可为null</strong>
	 * @return long：成功插入的键值对数量（仅统计{@link #insert(K, V)}返回true的次数）
	 * @throws NullPointerException 若elements为null，或elements中包含键/值为null的KeyValue（由{@link #insert(K, V)}的{@link NonNull}注解触发）
	 * @throws RuntimeException     底层{@link #insert(K, V)}抛出的存储相关异常会向上传递
	 */
	default long batchInsert(@NonNull Elements<? extends KeyValue<K, V>> elements) {
		AtomicLong totalCount = new AtomicLong();
		CollectionUtils.acceptAll(elements, (e) -> {
			if (insert(e.getKey(), e.getValue())) {
				totalCount.incrementAndGet();
			}
		});
		return totalCount.get();
	}

	/**
	 * 根据键删除存储中的单个数据
	 * <p>
	 * 核心逻辑：仅删除与指定键（K）<strong>严格匹配</strong>的数据，不影响其他键值对；若键不存在，返回false（无副作用）。
	 * <p>
	 * 注意：删除操作通常为“不可逆”，实现类需确保删除范围准确（如避免模糊匹配导致误删），建议在文档中说明匹配规则（如缓存的精确匹配、数据库的主键匹配）。
	 *
	 * @param key 待删除数据的键，不可为null（空值抛{@link NullPointerException}）
	 * @return boolean：true表示删除成功（键存在且已从存储中移除）；false表示删除失败（键不存在或未移除）
	 * @throws RuntimeException 实现类可抛出存储相关异常（如数据库锁冲突、缓存连接失败），需在具体实现类文档中补充
	 */
	boolean delete(@NonNull K key);

	/**
	 * 批量删除数据（默认实现）
	 * <p>
	 * 默认逻辑：遍历{@link Elements}中的每个键（K），调用{@link #delete(K)}执行单条删除，
	 * 用{@link AtomicLong}原子计数成功删除的数量（多线程环境下计数准确）。
	 * <p>
	 * 扩展建议：底层存储支持批量删除API（如Redis的DEL命令、数据库的IN条件删除）时，建议重写此方法以提升性能，默认实现仅适用于简单场景。
	 *
	 * @param keys 待删除的键集合，封装为{@link Elements}（可迭代），每个元素为键（K），<strong>不可为null</strong>
	 * @return long：成功删除的键数量（仅统计{@link #delete(K)}返回true的次数）
	 * @throws NullPointerException 若keys为null，或keys中包含null键（由{@link #delete(K)}的{@link NonNull}注解触发）
	 * @throws RuntimeException     底层{@link #delete(K)}抛出的存储相关异常会向上传递
	 */
	default long batchDelete(@NonNull Elements<? extends K> keys) {
		AtomicLong totalCount = new AtomicLong();
		CollectionUtils.acceptAll(keys, (e) -> {
			if (delete(e)) {
				totalCount.incrementAndGet();
			}
		});
		return totalCount.get();
	}

	/**
	 * 根据键更新存储中的单个数据
	 * <p>
	 * 核心逻辑：仅当键（K）已存在时执行更新，用新值（V）替换旧值；若键不存在，不执行任何操作并返回false（区别于{@link #insert(K, V)}的覆盖策略）。
	 * <p>
	 * 适用场景：需明确“仅更新已有数据”的业务（如订单状态更新、用户信息修改），避免误插入新数据。
	 *
	 * @param key   待更新数据的键，不可为null（空值抛{@link NullPointerException}）
	 * @param value 替换后的新业务实体数据，不可为null（空值抛{@link NullPointerException}）
	 * @return boolean：true表示更新成功（键存在且旧值已被新值替换）；false表示更新失败（键不存在，未执行任何操作）
	 * @throws RuntimeException 实现类可抛出存储相关异常（如数据库字段约束异常、缓存更新超时），需在具体实现类文档中补充
	 */
	boolean update(@NonNull K key, @NonNull V value);

	/**
	 * 批量更新数据（默认实现）
	 * <p>
	 * 默认逻辑：遍历{@link Elements}中的每个{@link KeyValue}，调用{@link #update(K, V)}执行单条更新，
	 * 用{@link AtomicLong}原子计数成功更新的数量（多线程环境下计数准确）。
	 * <p>
	 * 扩展建议：底层存储支持批量更新API（如数据库的CASE WHEN语句、Redis的MSET命令）时，建议重写此方法以提升性能，默认实现仅适用于简单场景。
	 *
	 * @param elements 待更新的键值对集合，封装为{@link Elements}（可迭代），每个元素为{@link KeyValue<K, V>}（键为待更新的标识，值为新数据），<strong>不可为null</strong>
	 * @return long：成功更新的键值对数量（仅统计{@link #update(K, V)}返回true的次数）
	 * @throws NullPointerException 若elements为null，或elements中包含键/值为null的KeyValue（由{@link #update(K, V)}的{@link NonNull}注解触发）
	 * @throws RuntimeException     底层{@link #update(K, V)}抛出的存储相关异常会向上传递
	 */
	default long batchUpdate(@NonNull Elements<? extends KeyValue<K, V>> elements) {
		AtomicLong totalCount = new AtomicLong();
		CollectionUtils.acceptAll(elements, (e) -> {
			if (update(e.getKey(), e.getValue())) {
				totalCount.incrementAndGet();
			}
		});
		return totalCount.get();
	}

	/**
	 * 根据键查询匹配的键值对数据（核心查询操作）
	 * <p>
	 * 核心逻辑：以键（K）作为查询条件，返回所有匹配的键值对，具体匹配规则由实现类定义，常见场景包括：
	 * <ul>
	 * <li>精确匹配（如缓存、数据库主键查询）：键为唯一标识，返回0或1个键值对；</li>
	 * <li>模糊匹配（如数据库LIKE查询、缓存前缀匹配）：键为查询条件（如含通配符的字符串），返回多个匹配的键值对；</li>
	 * <li>范围匹配（如数据库范围查询）：键为范围条件（如时间区间、数值区间），返回范围内的键值对。</li>
	 * </ul>
	 * <p>
	 * 结果约定：无论是否有匹配数据，均返回{@link Elements<KeyValue<K, V>>}实例（非null），无匹配时返回“空Elements”（调用{@link Elements#isEmpty()}返回true），
	 * 调用方可直接遍历结果，无需额外判空。
	 *
	 * @param key 查询条件键，不可为null（空值抛{@link NullPointerException}），具体含义由实现类定义（如精确键、模糊条件、范围条件）
	 * @return {@link Elements<KeyValue<K, V>>}：匹配的键值对集合，每个元素包含“匹配的键”和“对应的业务数据”；无匹配时返回空集合（非null）
	 * @throws RuntimeException 实现类可抛出存储相关异常（如数据库查询超时、缓存连接异常），需在具体实现类文档中补充
	 * @see Elements#isEmpty() 判断查询结果是否为空
	 * @see KeyValue#getKey() 获取匹配的键
	 * @see KeyValue#getValue() 获取匹配键对应的业务数据
	 */
	Elements<KeyValue<K, V>> query(@NonNull K key);

	/**
	 * 批量查询多个键对应的键值对数据（默认实现）
	 * <p>
	 * 核心逻辑：通过{@link Elements#flatMap(Function)}遍历待查询键集合（{@code keys}），
	 * 对每个键调用{@link #query(K)}执行单键查询，再将所有单键查询的结果（每个结果为{@link Elements<KeyValue<K, V>>}）
	 * 合并为一个统一的{@link Elements<KeyValue<K, V>>}，实现“多键查询结果聚合”。
	 * <p>
	 * 结果约定：
	 * <ul>
	 * <li>非null性：无论是否有键匹配，均返回非null的{@link Elements}实例；</li>
	 * <li>顺序性：结果中键值对的顺序与{@code keys}的迭代顺序一致，同一键的匹配结果顺序继承{@link #query(K)}的顺序；</li>
	 * <li>空结果：若所有键均无匹配，返回“空Elements”（{@link Elements#isEmpty()}返回true）。</li>
	 * </ul>
	 * <p>
	 * 默认实现局限性：默认通过循环调用{@link #query(K)}实现批量查询，未利用底层存储的批量查询API，在键数量较多时可能存在性能瓶颈（如网络IO频繁、数据库连接次数过多）。
	 * <p>
	 * 扩展建议：底层存储支持批量查询能力时，需重写此方法以提升性能，例如：
	 * <ul>
	 * <li>缓存（如Redis）：使用{@code MGET}命令批量获取多个键值，减少网络交互次数；</li>
	 * <li>数据库（如MySQL）：使用{@code WHERE id IN (key1, key2, ...)}语句，单次查询获取所有匹配数据；</li>
	 * <li>键值数据库（如LevelDB）：使用批量迭代器批量读取多个键对应的value。</li>
	 * </ul>
	 *
	 * @param keys 待查询的键集合，封装为{@link Elements}（可迭代），每个元素为键（K），<strong>不可为null</strong>
	 * @return {@link Elements<KeyValue<K, V>>}：所有键的匹配结果聚合集合，每个元素为“键-业务数据”对；无匹配时返回空集合（非null）
	 * @throws NullPointerException 若{@code keys}为null，或{@code keys}中包含null键（触发{@link #query(K)}的{@link NonNull}注解校验，抛{@link NullPointerException}）
	 * @throws RuntimeException     底层{@link #query(K)}抛出的存储相关异常（如查询超时、连接失败）会直接向上传递
	 * @see #query(K) 单键查询方法，批量查询的结果规则与单键查询一致
	 * @see Elements#flatMap(Function) 结果聚合的核心方法，实现多键查询结果的合并
	 */
	default Elements<KeyValue<K, V>> batchQuery(@NonNull Elements<? extends K> keys) {
		return keys.flatMap(this::query);
	}
}