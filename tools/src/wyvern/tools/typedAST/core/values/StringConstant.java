package wyvern.tools.typedAST.core.values;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.AbstractValue;
import wyvern.tools.typedAST.core.expressions.Invocation;
import wyvern.tools.typedAST.extensions.interop.java.Util;
import wyvern.tools.typedAST.extensions.interop.java.objects.JavaObj;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.extensions.Str;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.Hashtable;
import java.util.Map;

public class StringConstant extends AbstractValue implements InvokableValue, CoreAST {
	private String value;
	
	public StringConstant(String s) { this.value = s; }

	@Override
	public Type getType() {
		return new Str();
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(this.value);
	}
	
	public String getValue() {
		return value;
	}
	

	@Override
	public Value evaluateInvocation(Invocation exp, EvaluationEnvironment env) {
		String operator = exp.getOperationName();
		if (!operator.equals("+") && !operator.equals("==")) {
			try {
				return ((Obj) Util.javaToWyvObj(value)).evaluateInvocation(exp, env);
			} catch (Exception e) {
				throw new RuntimeException("forgot to typecheck!");
			}
		}
		Value argValue =  exp.getArgument().evaluate(env);

		if (operator.equals("==")) {
			if (argValue instanceof StringConstant) {
				return new BooleanConstant(this.getValue().equals(((StringConstant) argValue).getValue()));
			} else if (argValue instanceof JavaObj) {
				return new BooleanConstant(((String)((JavaObj) argValue).getObj()).equals(this.getValue()));
			}
			throw new RuntimeException("forgot to typecheck!");
		}

		if (argValue instanceof StringConstant) {
			return new StringConstant(this.value + ((StringConstant) argValue).value);
		} else	if (argValue instanceof IntegerConstant) {
			return new StringConstant(this.value + ((IntegerConstant) argValue).getValue());
		} else if (argValue instanceof JavaObj) {
			return new StringConstant(this.value + ((JavaObj) argValue).getObj());
		} else
		{
//			shouldn't get here.
			throw new RuntimeException("forgot to typecheck!");
		}
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		return children;
	}

	@Override
	public TypedAST cloneWithChildren(Map<String, TypedAST> nc) {
		return new StringConstant(value);
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}
