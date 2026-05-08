package run.soeasy.framework.codec.crypto;

/**
 * 具备工作模式能力的接口，定义获取工作模式名称的规范，
 * 适用于需要区分不同工作模式的组件（如加密算法、协议处理器等），
 * 用于标识和获取当前的工作模式（如加密算法中的CBC、ECB模式等）。
 * 
 * @author soeasy.run
 */
public interface WorkModeCapable {

    /**
     * 获取当前的工作模式名称
     * 
     * @return 工作模式名称（如加密模式中的"CBC"、"ECB"，或其他业务定义的模式名称）
     */
    String getWorkModeName();
}