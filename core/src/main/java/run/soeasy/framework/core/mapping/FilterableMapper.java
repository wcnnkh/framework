package run.soeasy.framework.core.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.streaming.Streamable;

/**
 * 实现{@link Mapper}接口的**可过滤映射器**，核心通过「责任链模式」在映射流程中嵌入多个过滤器，
 * 支持对映射上下文进行预处理、后处理或流程控制，最终由基础映射器执行核心映射逻辑。
 * 
 * <p>
 * 核心设计逻辑： 映射流程分为三个阶段： 1.
 * 过滤器链执行：按过滤器迭代顺序依次调用每个{@link MappingFilter}，完成上下文修改、数据验证等操作； 2.
 * 流程中断判断：若任一过滤器中断流程（如验证失败），则直接返回映射结果，不执行后续逻辑； 3.
 * 核心映射执行：所有过滤器正常执行完毕后，调用{@link #mapper}（基础映射器）完成最终数据映射。
 * 
 * <p>
 * <b>核心特性详解：</b>
 * <ul>
 * <li><strong>过滤器链流水线</strong>：过滤器按迭代顺序执行，支持多步骤预处理（如数据清洗、权限校验）与后处理（如结果格式化），
 * 每个过滤器可获取/修改{@link MappingContext}中的源/目标数据，形成灵活的映射处理链路；</li>
 * <li><strong>灵活可组合</strong>：支持两种组合方式： - 实例化时传入初始过滤器集合，固定基础映射逻辑； -
 * 调用{@link #doMapping(MappingContext, MappingContext, Iterable)}时追加额外过滤器，临时扩展映射逻辑；</li>
 * <li><strong>延迟执行机制</strong>：过滤器链与核心映射逻辑仅在调用{@code doMapping}方法时触发，避免初始化阶段的无效计算；</li>
 * <li><strong>类型安全约束</strong>：值类型{@code V}需实现{@link TypedValueAccessor}，确保映射过程中可安全获取值的类型信息与实际数据；
 * 上下文类型{@code T}需实现{@link Mapping}，确保包含映射所需的键值关联规则。</li>
 * </ul>
 * 
 * <p>
 * <b>典型使用场景：</b>
 * <ul>
 * <li>数据校验：在映射前通过过滤器验证源数据合法性（如非空校验、格式校验），不合法则中断映射；</li>
 * <li>数据转换：预处理阶段统一格式化源数据（如日期格式转换、数值单位转换），减少基础映射器的逻辑复杂度；</li>
 * <li>权限控制：过滤器中判断当前操作是否有权限访问源/目标数据，无权限则返回映射失败；</li>
 * <li>结果增强：后处理阶段为目标数据补充默认值（如给空字段设置默认值）或附加额外信息（如添加映射时间戳）。</li>
 * </ul>
 * 
 * <p>
 * <b>使用示例：</b>
 * 
 * <pre>{@code
 * // 1. 定义基础映射器（负责核心数据映射）
 * Mapper<String, UserValue, UserMapping> baseMapper = new UserBaseMapper();
 * 
 * // 2. 定义初始过滤器（如数据校验过滤器、格式转换过滤器）
 * List<MappingFilter<String, UserValue, UserMapping>> initialFilters = Arrays.asList(new UserDataValidateFilter(),
 * 		new DateFormatConvertFilter());
 * 
 * // 3. 创建可过滤映射器实例
 * FilterableMapper<String, UserValue, UserMapping> filterableMapper = new FilterableMapper<>(initialFilters,
 * 		baseMapper);
 * 
 * // 4. 准备源/目标映射上下文（包含待映射数据与配置）
 * MappingContext<String, UserValue, UserMapping> sourceContext = new UserMappingContext(sourceData);
 * MappingContext<String, UserValue, UserMapping> targetContext = new UserMappingContext();
 * 
 * // 5. 执行映射（使用初始过滤器链）
 * boolean success = filterableMapper.doMapping(sourceContext, targetContext);
 * 
 * // 6. 临时添加额外过滤器（如权限校验过滤器），执行特殊场景映射
 * MappingFilter<String, UserValue, UserMapping> extraFilter = new PermissionCheckFilter(currentUser);
 * boolean specialSuccess = filterableMapper.doMapping(sourceContext, targetContext, Arrays.asList(extraFilter));
 * }</pre>
 * 
 * @param <K> 映射键的类型（如用户ID、订单编号），用于唯一标识映射数据的关联关系
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}以支持类型安全的取值操作（如获取值类型、原始数据）
 * @param <T> 映射上下文的类型，需实现{@link Mapping}以提供键值映射的规则定义（如键与值的关联逻辑）
 * 
 * @author soeasy.run
 * @see Mapper 映射器核心接口，定义映射执行规范
 * @see MappingFilter 映射过滤器接口，定义过滤器的执行逻辑
 * @see MappingContext 映射上下文，封装源/目标数据与映射配置
 * @see TypedValueAccessor 值类型访问器，确保值的类型安全操作
 * @see ChainMapper 过滤器责任链实现类，负责调度过滤器与基础映射器
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FilterableMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> implements Mapper<K, V, T> {
	/**
	 * 映射过滤器集合，不可为null
	 * <p>
	 * 过滤器按迭代顺序依次执行，构成映射的预处理/后处理流水线； 支持包含预处理过滤器（如数据校验）与后处理过滤器（如结果增强），具体职责由过滤器实现类定义。
	 */
	@NonNull
	private final Iterable<MappingFilter<K, V, T>> filters;

	/**
	 * 基础映射器，负责执行最终的核心映射逻辑
	 * <p>
	 * 需在所有过滤器正常执行完毕后调用（若过滤器中断流程则不执行）； 若未设置（null），则过滤器链执行完毕后直接返回true（仅执行过滤逻辑，无核心映射）。
	 */
	private Mapper<K, V, T> mapper;

	/**
	 * 执行带过滤逻辑的映射转换（使用初始过滤器链）
	 * <p>
	 * 执行流程： 1. 构建{@link ChainMapper}实例，封装初始过滤器链与基础映射器； 2.
	 * 调用ChainMapper执行过滤器链（按顺序调用每个过滤器）； 3. 过滤器链执行完毕后，调用基础映射器执行核心映射； 4.
	 * 返回整体映射结果（过滤器中断则返回中断时的结果，否则返回基础映射器的结果）。
	 * 
	 * @param sourceContext 源映射上下文，不可为null，包含待映射的源数据、键值关联规则等信息
	 * @param targetContext 目标映射上下文，不可为null，用于接收映射后的目标数据
	 * @return boolean：true表示映射成功（过滤器未中断且基础映射执行成功，或仅过滤逻辑执行成功）；
	 *         false表示映射失败（过滤器中断流程，或基础映射执行失败）
	 */
	@Override
	public final boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext) {
		// 创建链式映射器，将过滤器集合转换为迭代器，并设置基础映射器为最终处理器
		return doFilterableMapping(sourceContext, targetContext, filters);
	}

	/**
	 * 执行带额外过滤器的映射转换（合并初始过滤器与额外过滤器）
	 * <p>
	 * 与{@link #doMapping(MappingContext, MappingContext)}的区别：
	 * 不创建新的FilterableMapper实例，而是通过{@link Streamable#concat(Streamable)}将初始过滤器与额外过滤器合并为新的过滤器链，
	 * 适用于临时扩展映射逻辑的场景（如特定请求需额外权限校验）。
	 * 
	 * @param sourceContext 源映射上下文，不可为null，包含待映射的源数据、键值关联规则等信息
	 * @param targetContext 目标映射上下文，不可为null，用于接收映射后的目标数据
	 * @param filters       额外的过滤器集合，不可为null，将在初始过滤器之后执行
	 * @return boolean：true表示映射成功（合并后的过滤器链未中断且基础映射执行成功）；
	 *         false表示映射失败（过滤器中断流程，或基础映射执行失败）
	 */
	public final boolean doMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext, @NonNull Iterable<MappingFilter<K, V, T>> filters) {
		Streamable<MappingFilter<K, V, T>> elements = Streamable.of(this.filters).concat(Streamable.of(filters));
		return doFilterableMapping(sourceContext, targetContext, elements.toList());
	}

	/**
	 * 可扩展的过滤映射执行方法（保护方法，供子类重写）
	 * <p>
	 * 默认实现：通过{@link ChainMapper}调度过滤器链与基础映射器，子类可重写此方法以自定义过滤映射逻辑，
	 * 例如调整过滤器执行顺序、添加全局异常处理等。
	 * 
	 * @param sourceContext 源映射上下文，不可为null
	 * @param targetContext 目标映射上下文，不可为null
	 * @param filters       待执行的过滤器链，不可为null（可能是初始过滤器或合并后的过滤器）
	 * @return boolean：映射执行结果（成功/失败）
	 */
	protected boolean doFilterableMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext, @NonNull Iterable<MappingFilter<K, V, T>> filters) {
		ChainMapper<K, V, T> chain = new ChainMapper<>(filters.iterator(), mapper);
		return chain.doMapping(sourceContext, targetContext);
	}
}