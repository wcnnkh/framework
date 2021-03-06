package scw.rpc.messageing;

import java.io.Serializable;

import scw.util.attribute.Attributes;

public interface RemoteRequestMessage extends MessageHeaders, Attributes<String, Object>, Serializable {
}
