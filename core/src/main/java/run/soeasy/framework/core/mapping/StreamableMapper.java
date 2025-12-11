package run.soeasy.framework.core.mapping;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.streaming.Mapping;

/**
 * 继承{@link FilterableMapper}的**集合级映射器**，专注于两个映射集合（如列表、数组）间的元素级批量转换。
 * 核心通过「键匹配策略」按顺序遍历目标集合元素，在源集合中查找键相等的元素，使用内部映射器完成元素转换，
 * 并移除已处理的源元素以避免重复映射，同时继承父类的过滤器链能力，支持集合级与元素级的双重过滤逻辑。
 * 
 * <p>
 * <b>与父类的核心差异：</b> FilterableMapper专注于单一映射单元的处理，而ArrayMapper扩展为**集合维度的批量映射**，
 * 通过迭代目标元素、匹配源元素、执行单元素映射的流水线，实现"多对多"的集合转换，同时保留过滤器链对每个元素映射的干预能力。
 * 
 * <p>
 * <b>核心特性详解：</b>
 * <ul>
 * <li><strong>键驱动的匹配机制</strong>：基于{@link ObjectUtils#equals(Object, Object)}比较元素键（K），
 * 仅当源元素与目标元素的键严格相等时才执行映射，确保数据关联的准确性；</li>
 * <li><strong>一次性映射保障</strong>：源元素被成功映射后立即从源列表中移除（通过{@link Iterator#remove()}），
 * 避免同一源元素被多个目标元素重复匹配（即使存在键相同的目标元素）；</li>
 * <li><strong>顺序化处理流程</strong>：严格按照目标集合的迭代顺序处理元素，适用于对顺序有要求的场景（如有序列表映射）；</li>
 * <li><strong>嵌套上下文支持</strong>：通过{@link MappingContext#current(KeyValue)}和{@link MappingContext#nested(KeyValue)}
 * 为每个元素创建独立的子上下文，实现集合整体上下文与元素局部上下文的隔离与传递；</li>
 * <li><strong>双重过滤能力</strong>：既支持集合级过滤器（作用于整体映射前/后），也支持元素级过滤器（通过父类机制作用于每个元素映射过程）。</li>
 * </ul>
 * 
 * <p>
 * <b>映射流程（核心步骤）：</b>
 * <ol>
 * <li><strong>上下文校验</strong>：验证源/目标上下文是否包含有效的映射集合（{@link Mapping}），
 * 且禁止同时包含单键值对（{@link KeyValue}）—— 确保操作对象是集合而非单个元素；</li>
 * <li><strong>源集合预处理</strong>：将源集合元素转换为可修改列表（{@link List}），以便移除已处理元素；</li>
 * <li><strong>目标元素遍历</strong>：按目标集合的迭代顺序，逐个取出目标元素（{@link KeyValue}）；</li>
 * <li><strong>源元素匹配</strong>：对每个目标元素，遍历源列表查找键相等的源元素；</li>
 * <li><strong>元素级映射</strong>：找到匹配的源元素后，创建嵌套上下文，调用父类{@link #doFilterableMapping}
 * 执行过滤器链与内部映射器（单元素映射逻辑）；</li>
 * <li><strong>源元素清理</strong>：若元素映射成功，从源列表中移除该源元素，避免重复处理；</li>
 * <li><strong>结果判定</strong>：统计成功映射的元素数量，只要有一个元素映射成功则返回true，否则返回false。</li>
 * </ol>
 * 
 * <p>
 * <b>适用场景：</b>
 * <ul>
 * <li>集合间的批量转换（如DTO列表与实体列表的相互映射）；</li>
 * <li>需要按键关联的多元素映射（如订单列表与订单项列表的匹配映射）；</li>
 * <li>需避免重复映射的场景（如一次性数据同步，确保每个源元素只被处理一次）；</li>
 * <li>结合过滤器实现集合级数据清洗（如过滤无效元素后再执行批量映射）。</li>
 * </ul>
 * 
 * <p>
 * <b>使用示例：</b>
 * 
 * <pre>{@code
 * // 1. 定义元素级映射器（处理单个元素的核心转换）
 * Mapper<Long, OrderValue, OrderMapping> elementMapper = new OrderElementMapper();
 * 
 * // 2. 定义集合级过滤器（如过滤状态无效的元素）
 * List<MappingFilter<Long, OrderValue, OrderMapping>> filters = Collections.singletonList(new InvalidStatusFilter());
 * 
 * // 3. 创建数组映射器实例
 * ArrayMapper<Long, OrderValue, OrderMapping> arrayMapper = new ArrayMapper<>(filters, elementMapper);
 * 
 * // 4. 准备源/目标集合上下文（包含订单列表）
 * MappingContext<Long, OrderValue, OrderMapping> sourceContext = new OrderMappingContext(sourceOrderList); // 源订单列表
 * MappingContext<Long, OrderValue, OrderMapping> targetContext = new OrderMappingContext(targetOrderList); // 目标订单列表（待填充）
 * 
 * // 5. 执行集合映射
 * boolean success = arrayMapper.doMapping(sourceContext, targetContext);
 * // 结果：targetOrderList中键匹配的元素已完成映射，sourceOrderList中已处理元素被移除
 * }</pre>
 * 
 * @param <K> 映射键的类型（如订单ID、用户ID），用于匹配源集合与目标集合中的元素
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}，支持元素值的类型安全访问
 * @param <T> 映射上下文的类型，需实现{@link Mapping}，提供集合级映射的规则定义（如元素迭代方式）
 * 
 * @author soeasy.run
 * @see FilterableMapper 父类，提供过滤器链与基础映射能力
 * @see MappingContext#current(KeyValue) 创建元素级当前上下文
 * @see MappingContext#nested(KeyValue) 创建元素级嵌套上下文
 * @see ObjectUtils#equals(Object, Object) 键匹配的底层实现
 */
@Getter
@Setter
public class StreamableMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends FilterableMapper<K, V, T> {

	/**
	 * 构造仅包含过滤器的ArrayMapper实例
	 * <p>
	 * 适用于仅需执行过滤逻辑，无需核心映射（或通过子类重写映射逻辑）的场景
	 * 
	 * @param filters 集合级与元素级过滤器链，不可为null
	 */
	public StreamableMapper(@NonNull Iterable<MappingFilter<K, V, T>> filters) {
		super(filters);
	}

	/**
	 * 构造包含过滤器与内部映射器的ArrayMapper实例
	 * <p>
	 * filters负责过滤逻辑，mapper负责元素级核心映射，组合实现完整的集合映射流程
	 * 
	 * @param filters 集合级与元素级过滤器链，不可为null
	 * @param mapper  元素级核心映射器，负责单个元素的具体转换逻辑
	 */
	public StreamableMapper(@NonNull Iterable<MappingFilter<K, V, T>> filters, Mapper<K, V, T> mapper) {
		super(filters, mapper);
	}

	/**
	 * 重写父类方法，实现集合级批量映射逻辑
	 * <p>
	 * 核心逻辑：验证上下文合法性 → 预处理源集合 → 遍历目标元素 → 匹配源元素 → 执行元素映射 → 清理已处理元素
	 * 
	 * @param sourceContext 源集合上下文，不可为null，需包含{@link Mapping}类型的集合数据
	 * @param targetContext 目标集合上下文，不可为null，需包含{@link Mapping}类型的集合数据
	 * @param filters       本次映射使用的过滤器链（可能是初始过滤器或合并后的过滤器），不可为null
	 * @return boolean：true表示至少有一个元素映射成功；false表示无元素映射成功（或上下文不合法）
	 */
	@Override
	protected boolean doFilterableMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext, @NonNull Iterable<MappingFilter<K, V, T>> filters) {
		// 验证上下文条件：必须包含映射集合且不能同时包含单键值对
		if (sourceContext.hasKeyValue()
				|| targetContext.hasKeyValue() && !(sourceContext.hasMapping() && targetContext.hasMapping())) {
			return false;
		}

		// 获取源映射集合元素列表（转换为可修改列表以便移除元素）
		List<KeyValue<K, V>> sourceList = sourceContext.getMapping().collect(Collectors.toList());
		if (sourceList.isEmpty()) {
			return false;
		}

		// 记录成功映射的元素数量
		int count = 0;

		// 遍历目标映射集合
		try (Stream<KeyValue<K, V>> targetStream = targetContext.getMapping().stream()) {
			Iterator<KeyValue<K, V>> targetIterator = targetStream.iterator();
			while (targetIterator.hasNext()) {
				KeyValue<K, V> target = targetIterator.next();
				Iterator<KeyValue<K, V>> sourceIterator = sourceList.iterator();

				// 在源集合中查找匹配键的元素
				while (sourceIterator.hasNext()) {
					KeyValue<K, V> source = sourceIterator.next();

					// 键不匹配则跳过
					if (!ObjectUtils.equals(source.getKey(), target.getKey())) {
						continue;
					}

					// 使用内部映射器处理元素映射
					if (super.doFilterableMapping(sourceContext.current(source), targetContext.nested(target),
							filters)) {
						// 映射成功后从源集合移除，避免重复处理
						sourceIterator.remove();
						count++;
					}
				}
			}
		}
		return count > 0;
	}
}