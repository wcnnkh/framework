package scw.aop;

public class ProxyMethodInvoker extends MethodInvokerWrapper{
	private static final long serialVersionUID = 1L;
	
	public ProxyMethodInvoker(MethodInvoker source) {
		super(source);
	}

}
