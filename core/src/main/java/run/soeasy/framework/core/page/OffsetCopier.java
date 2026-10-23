package run.soeasy.framework.core.page;

/**
 * 基于偏移量的数据拷贝器接口，定义从源对象到目标对象的分段拷贝策略。
 *
 * <p>该接口用于解决分页、切片或大对象处理场景下的数据搬运问题，
 * 允许在不暴露内部存储结构的情况下，按指定偏移量和长度进行数据拷贝。
 *
 * <p><b>核心特性：</b>
 * <ul>
 *   <li><b>分页友好：</b>通过 {@code targetOffset} 和 {@code length} 精确控制拷贝范围</li>
 *   <li><b>类型无关：</b>支持任意类型的源和目标载体</li>
 *   <li><b>策略扩展：</b>可作为扩展点，适配不同的数据结构拷贝规则</li>
 *   <li><b>函数式设计：</b>单方法接口，支持 Lambda 实现</li>
 * </ul>
 *
 * <p><b>典型应用场景：</b>
 * <ul>
 *   <li>内存分页与分段处理</li>
 *   <li>零拷贝数据传输</li>
 *   <li>协议解析与数据提取</li>
 *   <li>缓存填充与数据加载</li>
 * </ul>
 *
 * @param <S> 数据源类型（如 byte[]、ByteBuffer 等）
 * @param <T> 目标载体类型（如 byte[]、ByteBuffer 等）
 * @author soeasy.run
 */
@FunctionalInterface
public interface OffsetCopier<S, T> {

	/**
	 * 从源对象拷贝指定长度的数据到目标对象的指定偏移位置。
	 *
	 * @param source       数据源（不可为 null）
	 * @param target       目标载体（不可为 null）
	 * @param targetOffset 目标载体中的起始偏移量（≥ 0）
	 * @param length       拷贝的数据长度（≥ 0）
	 * @throws IndexOutOfBoundsException 当偏移量或长度超出源/目标的有效范围时抛出
	 * @throws NullPointerException      当 source 或 target 为 null 时抛出（可选，由实现决定）
	 */
	void copy(S source, T target, int targetOffset, int length);
}