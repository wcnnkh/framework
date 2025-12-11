package run.soeasy.framework.core.mapping;

import java.util.Iterator;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 继承{@link FilterableMapper}的**键值对集合映射器**，专注于处理“键到多值”的映射场景。
 * 核心通过「键匹配+多值遍历」策略，遍历目标集合中的键值对，在源集合中查找所有同键值，
 * 并使用内部映射器对每个值执行单独转换，同时保留父类的过滤器链能力，支持键级与值级的双重过滤逻辑。
 * 
 * <p>
 * <b>与ArrayMapper的核心差异：</b>
 * <ul>
 * <li>映射关系：ArrayMapper是“目标元素→单个源元素”的一对一映射（且源元素会被移除）；
 * MapMapper是“目标键→多个源值”的一对多映射（源元素不会被移除，支持重复映射）；</li>
 * <li>处理逻辑：ArrayMapper按目标元素顺序匹配并消耗源元素；MapMapper按目标键聚合源值，批量处理同键下的所有值；</li>
 * <li>适用场景：ArrayMapper适用于“集合同步”（如列表数据迁移）；MapMapper适用于“键值聚合”（如按用户ID聚合订单）。</li>
 * </ul>
 * 
 * <p>
 * <b>核心特性详解：</b>
 * <ul>
 * <li><strong>键驱动的多值处理</strong>：基于键（K）的相等性（通过{@link ObjectUtils#equals(Object, Object)}判断），
 * 对目标键对应的所有源值执行映射，支持源集合中“一键多值”场景（如一个用户ID对应多条订单记录）；</li>
 * <li><strong>非消耗性映射</strong>：源集合中的值不会被移除，同一键的源值可被多个目标键（若存在相同键）重复映射，
 * 适用于需要多次复用源数据的场景（如统计分析中的多维度映射）；</li>
 * <li><strong>值级上下文隔离</strong>：通过{@link MappingContext#current(KeyValue)}为每个源值创建独立上下文，
 * 确保同键下不同值的映射逻辑互不干扰（如同一用户的不同订单单独转换）；</li>
 * <li><strong>双重过滤支持</strong>：既支持集合级过滤器（如过滤无效键），也支持值级过滤器（如过滤源值为空的记录），
 * 过滤器可通过上下文获取当前处理的键与值，实现精细化控制。</li>
 * </ul>
 * 
 * <p>
 * <b>映射流程（核心步骤）：</b>
 * <ol>
 * <li><strong>上下文校验</strong>：验证源/目标上下文是否包含有效的映射集合（{@link Mapping}），
 * 且禁止包含单键值对（{@link KeyValue}）—— 确保操作对象是键值对集合；</li>
 * <li><strong>目标键遍历</strong>：按目标集合的迭代顺序，逐个取出目标键值对（{@link KeyValue}）；</li>
 * <li><strong>源值聚合</strong>：对每个目标键，通过{@link Mapping#getValues(Object)}从源集合中获取所有同键值，
 * 封装为{@link Streamable}（可迭代的多值集合）；</li>
 * <li><strong>值级映射</strong>：遍历聚合后的源值，为每个值创建源上下文（包含当前键值对）和目标上下文（包含当前目标键值对），
 * 调用父类{@link #doFilterableMapping}执行过滤器链与内部映射器（单值转换逻辑）；</li>
 * <li><strong>结果判定</strong>：统计成功映射的源值数量，只要有一个值映射成功则返回true，否则返回false。</li>
 * </ol>
 * 
 * <p>
 * <b>适用场景：</b>
 * <ul>
 * <li>一键多值的批量转换（如用户ID对应多个订单，需将所有订单转换为订单DTO）；</li>
 * <li>源数据需复用的多维度映射（如同一批商品数据，需按不同目标键多次转换）；</li>
 * <li>键值对集合的聚合映射（如Map&lt;String, List&lt;User&gt;&gt;与Map&lt;String,
 * List&lt;UserDTO&gt;&gt;的转换）；</li>
 * <li>带键级过滤的映射（如仅处理状态为“有效”的键对应的所有值）。</li>
 * </ul>
 * 
 * <p>
 * <b>使用示例：</b>
 * 
 * <pre>{@code
 * // 1. 定义值级映射器（处理单个键值对的转换，如Order→OrderDTO）
 * Mapper<String, OrderValue, OrderMapping> valueMapper = new OrderValueMapper();
 * 
 * // 2. 定义过滤器（如过滤金额为0的订单、验证键格式）
 * List<MappingFilter<String, OrderValue, OrderMapping>> filters = Arrays.asList(new ZeroAmountFilter(), // 过滤金额为0的源值
 * 		new KeyFormatCheckFilter() // 校验键格式（如用户ID必须为数字）
 * );
 * 
 * // 3. 创建MapMapper实例
 * MapMapper<String, OrderValue, OrderMapping> mapMapper = new MapMapper<>(filters, valueMapper);
 * 
 * // 4. 准备源/目标上下文（包含键值对集合，如键=用户ID，值=订单列表）
 * MappingContext<String, OrderValue, OrderMapping> sourceContext = new OrderMappingContext(sourceMap); // 源：Map<String,
 * 																										// List<Order>>
 * MappingContext<String, OrderValue, OrderMapping> targetContext = new OrderMappingContext(targetMap); // 目标：Map<String,
 * 																										// List<OrderDTO>>（待填充）
 * 
 * // 5. 执行映射
 * boolean success = mapMapper.doMapping(sourceContext, targetContext);
 * // 结果：targetMap中，每个用户ID对应的OrderDTO列表已填充（仅包含映射成功的订单）
 * }</pre>
 * 
 * @param <K> 映射键的类型（如用户ID、商品分类ID），用于聚合源集合中的值并匹配目标集合
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}，支持值的类型安全访问与转换
 * @param <T> 映射上下文的类型，需实现{@link Mapping}，提供键值对集合的访问规则（如获取指定键的所有值）
 * 
 * @author soeasy.run
 * @see FilterableMapper 父类，提供过滤器链与基础映射能力
 * @see Mapping#getValues(Object) 源集合中获取指定键的所有值的核心方法
 * @see MappingContext#current(KeyValue) 为单个键值对创建上下文的方法
 * @see StreamableMapper 对比类，适用于一对一的集合同步场景
 */
@Getter
@Setter
public class MappedMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends FilterableMapper<K, V, T> {
	/**
	 * 构造仅包含过滤器的MapMapper实例
	 * <p>
	 * 适用于仅需执行过滤逻辑，无需核心映射（或通过子类重写映射逻辑）的场景
	 * 
	 * @param filters 集合级与值级过滤器链，不可为null
	 */
	public MappedMapper(@NonNull Iterable<MappingFilter<K, V, T>> filters) {
		super(filters);
	}

	/**
	 * 构造包含过滤器与内部映射器的MapMapper实例
	 * <p>
	 * filters负责过滤逻辑，mapper负责值级核心映射，组合实现完整的键值对集合映射
	 * 
	 * @param filters 集合级与值级过滤器链，不可为null
	 * @param mapper  值级核心映射器，负责单个键值对的具体转换逻辑
	 */
	public MappedMapper(@NonNull Iterable<MappingFilter<K, V, T>> filters, Mapper<K, V, T> mapper) {
		super(filters, mapper);
	}

	/**
	 * 重写父类方法，实现“键到多值”的集合映射逻辑
	 * <p>
	 * 核心逻辑：验证上下文合法性 → 遍历目标键值对 → 聚合源同键值 → 逐个值映射 → 统计成功次数
	 * 
	 * @param sourceContext 源集合上下文，不可为null，需包含{@link Mapping}类型的键值对集合
	 * @param targetContext 目标集合上下文，不可为null，需包含{@link Mapping}类型的键值对集合
	 * @param filters       本次映射使用的过滤器链（可能是初始过滤器或合并后的过滤器），不可为null
	 * @return boolean：true表示至少有一个值映射成功；false表示无值映射成功（或上下文不合法）
	 */
	@Override
	protected boolean doFilterableMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext, @NonNull Iterable<MappingFilter<K, V, T>> filters) {
		// 验证上下文必须包含映射集合且不能包含单键值对
		if (sourceContext.hasKeyValue()
				|| targetContext.hasKeyValue() && !(sourceContext.hasMapping() && targetContext.hasMapping())) {
			return false;
		}

		int count = 0;
		try (Stream<KeyValue<K, V>> sourceStream = sourceContext.getMapping().stream()) {
			Iterator<KeyValue<K, V>> iterator = sourceStream.iterator();
			while (iterator.hasNext()) {
				KeyValue<K, V> source = iterator.next();
				Streamable<V> targetStreamable = targetContext.getMapping().getValues(source.getKey());
				try (Stream<V> targetStream = targetStreamable.stream()) {
					Iterator<V> targetIterator = targetStream.iterator();
					while (targetIterator.hasNext()) {
						V targetValue = targetIterator.next();
						if (super.doFilterableMapping(sourceContext.current(source),
								targetContext.current(KeyValue.of(source.getKey(), targetValue)), filters)) {
							count++;
						}
					}
				}
			}
		}
		return count > 0;
	}
}