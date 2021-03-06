package wyvern.targets.Common.wyvernIL.interpreter.core;

import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeFunction;
import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeRef;
import wyvern.targets.Common.wyvernIL.interpreter.values.BytecodeValue;

import java.util.HashMap;
import java.util.Map;

/**
 * implementation of the BytecodeContext interface
 * 
 * @author Tal Man
 * 
 */
public class BytecodeContextImpl implements BytecodeContext {

	private final Map<String, BytecodeValue> context;

	/**
	 * initiates an empty context
	 */
	public BytecodeContextImpl() {
		context = new HashMap<String, BytecodeValue>();
	}

	/**
	 * copy constructor for the context
	 * 
	 * @param c
	 *            the context to be copied
	 */
	public BytecodeContextImpl(BytecodeContext c) {
		this();
		context.putAll(((BytecodeContextImpl) c).context);
	}

	@Override
	public boolean existsInContext(String valName) {
		return context.containsKey(valName);
	}

	@Override
	public BytecodeValue getValue(String valName) {
		if (!context.containsKey(valName)) {
			String msg = "searching for value that doesn't exist in context: "
					+ valName;
			throw new RuntimeException(msg);
		}
		return context.get(valName);
	}

	@Override
	public void addToContext(String name, BytecodeValue val) {
		context.put(name, val);
	}

	@Override
	public String toString() {
		return contextToString(false);
	}

	@Override
	public String toSimpleString() {
		return contextToString(true);
	}

	/**
	 * helper method for prints
	 * 
	 * @param simple
	 *            whether we require a simplified version of the context or not
	 * @return a string representing the contents of the context
	 */
	private String contextToString(boolean simple) {
		StringBuilder sb = new StringBuilder();
		for (String s : context.keySet()) {
			if (simple && s.contains("$")) {
				continue;
			}
			BytecodeValue val = context.get(s);
			if (val instanceof BytecodeFunction) {
				sb.append(s + val + "\n");
			} else {
				sb.append(s + " = " + val + "\n");
			}
		}
		return sb.toString();
	}

	@Override
	public void setThis(BytecodeValue thisClass) {
		BytecodeRef ref = (BytecodeRef) context.get("this");
		ref.setValue(thisClass);
	}
}
