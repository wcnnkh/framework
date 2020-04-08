package scw.mvc.output;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;
import scw.mvc.output.support.ConfigurationOutput;

@AutoImpl({ConfigurationOutput.class})
public interface Output {
	boolean canWrite(Channel channel, Object body);
	
	void write(Channel channel, Object body) throws Throwable;
}
