package run.soeasy.framework.core.transform;

/**
 * 转换器感知接口，用于标识实现类能够接收并持有{@link Transformer}实例。
 * <p>
 * 实现该接口的类可以通过{@link #setTransformer(Transformer)}方法
 * 接收外部传入的转换器，从而在处理对象转换时使用该转换器。 适用于需要动态配置转换器的场景，如数据处理组件、对象映射器等。
 *
 * <p>
 * <b>核心特性：</b>
 * <ul>
 * <li>转换器注入：支持通过方法注入{@link Transformer}实例</li>
 * <li>动态配置：允许在运行时动态设置转换器，提高组件灵活性</li>
 * <li>依赖解耦：通过接口解耦组件与具体转换器实现</li>
 * </ul>
 *
 * @author soeasy.run
 * @see Transformer
 */
public interface TransformerAware {

	/**
	 * 设置用于对象转换的转换器。
	 * <p>
	 * 实现类应确保在接收到转换器后，后续的转换操作使用该转换器。 建议在实现中添加空值校验，避免使用未初始化的转换器。
	 * 
	 * @param transformer 转换器实例，可能为null（需实现类自行处理空值场景）
	 */
	void setTransformer(Transformer transformer);
}