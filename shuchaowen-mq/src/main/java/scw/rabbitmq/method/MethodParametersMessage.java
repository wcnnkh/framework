package scw.rabbitmq.method;

import java.io.Serializable;

public class MethodParametersMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private Object[] args;
	
	public MethodParametersMessage(Object[] args){
		this.args = args;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}
}
