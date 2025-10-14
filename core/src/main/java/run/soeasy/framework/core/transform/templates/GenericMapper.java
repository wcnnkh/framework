package run.soeasy.framework.core.transform.templates;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.convert.value.TypedValueAccessor;

/**
 * 继承{@link FilterableMapper}的**全场景自适应映射器**，核心能力是根据源/目标上下文的数据类型，
 * 自动路由到对应的子映射器（{@link ArrayMapper} 或 {@link MapMapper}），同时原生支持单键值对映射，
 * 实现“单键值对→数组集合→Map集合”三类场景的无缝覆盖，无需业务层手动选择映射策略。
 * 
 * <p><b>设计定位与层级关系：</b>
 * GenericMapper 处于映射器体系的“策略路由层”，承上启下：
 * <ul>
 * <li>承上：继承{@link FilterableMapper}的过滤器链能力，确保所有层级的映射都能应用统一的过滤逻辑（如数据校验、格式转换）；</li>
 * <li>启下：内部封装{@link ArrayMapper}和{@link MapMapper}，根据上下文类型动态选择具体映射器，屏蔽场景差异；</li>
 * <li>核心创新：通过“自引用设计”（将自身作为子映射器的基础映射器），支持嵌套结构的递归映射（如数组嵌套Map、Map嵌套数组）。</li>
 * </ul>
 * 
 * <p><b>核心特性详解：</b>
 * <ul>
 * <li><strong>智能策略路由</strong>：无需手动指定映射器，通过上下文的两个核心方法自动判断场景：
 *     - 单键值对场景：{@link MappingContext#hasKeyValue()} 为 true，直接调用父类逻辑；
 *     - 集合场景：{@link MappingContext#hasMapping()} 为 true，再通过 {@link Mapping#isMap()} 判断是否为Map类型，
 *       是则用{@link MapMapper}，否则用{@link ArrayMapper}；</li>
 * <li><strong>递归嵌套处理</strong>：内部创建子映射器（Array/MapMapper）时，将当前{@code GenericMapper}实例作为基础映射器（{@code mapper}参数），
 *     当遇到嵌套结构（如Map的值是数组、数组的元素是Map）时，子映射器会再次调用GenericMapper的策略路由逻辑，实现多层级自动映射；</li>
 * <li><strong>过滤器链透传</strong>：初始化子映射器时，将当前过滤器集合（{@code filters}）传递给子映射器，
 *     确保“父映射→子映射”的全链路都能应用过滤逻辑（如顶层数组过滤无效元素，嵌套Map过滤空值）；</li>
 * <li><strong>类型安全保障</strong>：值类型{@code V}需实现{@link TypedValueAccessor}，确保所有层级的映射都能安全获取值的类型信息，
 *     避免嵌套结构中的类型转换异常。</li>
 * </ul>
 * 
 * <p><b>映射策略判定流程（核心逻辑）：</b>
 * <ol>
 * <li><strong>场景1：单键值对映射</strong>
 *     - 判定条件：源上下文{@code sourceContext.hasKeyValue() == true} <b>且</b> 目标上下文{@code targetContext.hasKeyValue() == true}；
 *     - 处理逻辑：直接调用父类{@link FilterableMapper#doFilterableMapping}，执行过滤器链后完成单个键值对的映射；
 *     - 适用场景：独立的键值对转换（如单个用户信息、单个订单数据）。</li>
 * <li><strong>场景2：集合映射</strong>
 *     - 判定条件：源上下文{@code sourceContext.hasMapping() == true} <b>且</b> 目标上下文{@code targetContext.hasMapping() == true}；
 *     - 子策略1（Map集合）：若{@code sourceContext.getMapping().isMap() == true}（源是Map类型），
 *       创建{@link MapMapper}实例（传入当前过滤器和this作为基础映射器），调用其{@code doMapping}处理“一键多值”映射；
 *     - 子策略2（数组集合）：若源不是Map类型，创建{@link ArrayMapper}实例（参数同上），调用其{@code doMapping}处理“顺序匹配”映射；
 *     - 适用场景：批量数据转换（如用户列表、订单Map）、嵌套结构转换（如“用户列表→每个用户的订单Map”）。</li>
 * <li><strong>场景3：默认处理</strong>
 *     - 判定条件：不满足上述两种场景（如源是单键值对、目标是集合）；
 *     - 处理逻辑：调用父类{@link FilterableMapper#doFilterableMapping}，按基础映射逻辑执行（可能返回false，需业务层确保上下文类型匹配）。</li>
 * </ol>
 * 
 * <p><b>典型适用场景：</b>
 * <ul>
 * <li>通用数据转换组件：如框架级的DTO/实体转换工具，无需关心输入输出是单值、数组还是Map，统一用GenericMapper处理；</li>
 * <li>嵌套复杂结构映射：如“商品分类数组→每个分类下的商品Map”“用户Map→每个用户的地址数组”等多层级结构；</li>
 * <li>业务层轻量化需求：业务代码无需手动创建ArrayMapper/MapMapper，仅通过GenericMapper即可覆盖所有映射场景，减少代码冗余。</li>
 * </ul>
 * 
 * <p><b>使用示例（嵌套结构映射）：</b>
 * <pre>{@code
 * // 1. 定义基础映射器（处理最底层的键值对转换，如Product→ProductDTO）
 * Mapper<Long, ProductValue, ProductMapping> baseMapper = new ProductBaseMapper();
 * 
 * // 2. 定义全局过滤器（如过滤已下架商品、日期格式统一）
 * List<MappingFilter<Long, ProductValue, ProductMapping>> globalFilters = Arrays.asList(
 *     new OffShelfProductFilter(), 
 *     new DateFormatUnifyFilter()
 * );
 * 
 * // 3. 创建通用映射器（自动路由策略）
 * GenericMapper<Long, ProductValue, ProductMapping> genericMapper = 
 *     new GenericMapper<>(globalFilters, baseMapper);
 * 
 * // 4. 准备嵌套结构上下文：源是“分类ID→商品Map”（Map集合），目标是“分类数组→每个分类的商品列表”（数组嵌套列表）
 * // 源上下文：MappingContext（hasMapping=true，isMap=true）
 * MappingContext<Long, ProductValue, ProductMapping> sourceContext = 
 *     new ProductMappingContext(categoryProductMap); // 源：Map<Long, Map<Long, Product>>
 * // 目标上下文：MappingContext（hasMapping=true，isMap=false）
 * MappingContext<Long, ProductValue, ProductMapping> targetContext = 
 *     new ProductMappingContext(categoryProductList); // 目标：List<CategoryDTO>（每个DTO含List<ProductDTO>）
 * 
 * // 5. 执行映射（自动路由：先MapMapper处理源Map，再ArrayMapper处理目标数组，递归完成嵌套转换）
 * boolean success = genericMapper.doMapping(sourceContext, targetContext);
 * // 结果：targetContext中的分类数组已填充商品列表，所有层级均应用了globalFilters
 * }</pre>
 * 
 * @param <K> 映射键的类型（如商品ID、分类ID），用于键匹配与层级关联
 * @param <V> 映射值的类型，需实现{@link TypedValueAccessor}，确保所有层级映射的类型安全
 * @param <T> 映射上下文的类型，需实现{@link Mapping}，提供数据类型判断（isMap()）与元素访问能力
 * 
 * @author soeasy.run
 * @see FilterableMapper 父类，提供全局过滤器链与基础映射能力
 * @see ArrayMapper 子映射器，处理非Map类型的集合（如列表、数组）
 * @see MapMapper 子映射器，处理Map类型的集合（一键多值场景）
 * @see Mapping#isMap() 判定集合是否为Map类型的核心方法
 * @see MappingContext#hasKeyValue() 判定是否为单键值对场景的核心方法
 */
@Getter
public class GenericMapper<K, V extends TypedValueAccessor, T extends Mapping<K, V>> extends FilterableMapper<K, V, T> {
	/**
	 * 构造通用映射器实例
	 * <p>需传入全局过滤器链与基础映射器，过滤器会透传到内部的ArrayMapper/MapMapper，
	 * 基础映射器用于处理最底层的单键值对转换（包括嵌套结构的叶子节点）。
	 * 
	 * @param filters 全局过滤器集合，不可为null，将应用于所有层级的映射（GenericMapper自身+内部子映射器）
	 * @param mapper  基础映射器，不可为null，负责最底层的单键值对核心转换逻辑（如DTO/实体字段映射）
	 */
	public GenericMapper(@NonNull Iterable<MappingFilter<K, V, T>> filters, Mapper<K, V, T> mapper) {
		super(filters, mapper);
	}

	/**
	 * 重写父类方法，实现“场景判定→策略路由→子映射器调用”的核心逻辑
	 * <p>该方法是GenericMapper的策略中枢，按“单键值对→Map集合→数组集合→默认”的优先级判定场景，
	 * 并创建对应的子映射器执行具体映射，同时确保过滤器链与基础映射器透传。
	 * 
	 * @param sourceContext 源映射上下文，不可为null，包含待转换的源数据（单键值对/集合）
	 * @param targetContext 目标映射上下文，不可为null，用于接收转换后的目标数据
	 * @param filters 本次映射使用的过滤器链（通常为全局过滤器，透传给子映射器），不可为null
	 * @return boolean：true表示至少有一个层级的映射成功；false表示所有层级映射失败（或上下文类型不匹配）
	 */
	@Override
	protected boolean doFilterableMapping(@NonNull MappingContext<K, V, T> sourceContext,
			@NonNull MappingContext<K, V, T> targetContext, @NonNull Iterable<MappingFilter<K, V, T>> filters) {
		// 场景1：单键值对映射（源和目标均为单个键值对）
		if (sourceContext.hasKeyValue() && targetContext.hasKeyValue()) {
			return super.doFilterableMapping(sourceContext, targetContext, filters);
		}
		// 场景2：集合映射（源和目标均为映射集合）
		else if (sourceContext.hasMapping() && targetContext.hasMapping()) {
			// 子策略：源是Map类型→用MapMapper，否则用ArrayMapper
			if (sourceContext.getMapping().isMap()) {
				// 创建MapMapper，传入当前过滤器和this（自引用）作为基础映射器，支持嵌套
				MapMapper<K, V, T> mapMapper = new MapMapper<>(filters, this);
				return mapMapper.doMapping(sourceContext, targetContext);
			} else {
				// 创建ArrayMapper，同理支持嵌套
				ArrayMapper<K, V, T> arrayMapper = new ArrayMapper<>(filters, this);
				return arrayMapper.doMapping(sourceContext, targetContext);
			}
		}
		// 场景3：默认逻辑（不匹配上述场景，按父类基础映射处理）
		return super.doFilterableMapping(sourceContext, targetContext, filters);
	}
}