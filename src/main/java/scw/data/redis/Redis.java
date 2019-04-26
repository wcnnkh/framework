package scw.data.redis;

public interface Redis {
	Commands getCommands();

	BinaryCommands getBinaryCommands();

	BinaryScriptingCommands getBinaryScriptingCommands();

	ScriptingCommands getScriptingCommands();

	MultiKeyBinaryCommands getMultiKeyBinaryCommands();

	MultiKeyCommands getMultiKeyCommands();
}
