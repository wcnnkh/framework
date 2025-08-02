package run.soeasy.framework.aop;

/**
 * 被代理对象接口，定义了被AOP代理的对象需实现的契约，
 * 主要用于标识对象已被代理，并提供获取代理容器ID的方法，
 * 便于AOP框架管理代理对象与代理容器之间的关联关系。
 * 
 * <p>该接口为被代理的对象提供统一标识，通过{@link #getProxyContainerId()}方法，
 * 可获取获取到管理该代理对象的容器ID，支持代理容器对代理对象的追踪、管理与生命周期控制，
 * 是AOP框架中代理对象与容器交互的基础接口。
 * 
 * @author soeasy.run
 */
public interface DelegatedObject {

    /**
     * 获取代理容器ID的方法名常量（固定为"getProxyContainerId"），
     * 用于反射调用或方法匹配，确保代理对象正确实现该方法。
     */
    public static final String PROXY_CONTAINER_ID_METHOD_NAME = "getProxyContainerId";

    /**
     * 获取管理当前代理对象的AOP容器ID
     * 
     * <p>该ID用于唯一标识管理当前代理对象的容器，AOP框架可通过此ID定位到对应的容器，
     * 实现代理对象与容器的关联，支持容器级别的代理配置、拦截器管理等功能。
     * 
     * @return 代理容器的唯一标识ID（非空字符串）
     */
    String getProxyContainerId();
}