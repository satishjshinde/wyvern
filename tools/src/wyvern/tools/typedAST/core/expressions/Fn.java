package wyvern.tools.typedAST.core.expressions;

import wyvern.tools.errors.FileLocation;
import wyvern.tools.typedAST.abs.CachingTypedAST;
import wyvern.tools.typedAST.core.evaluation.Closure;
import wyvern.tools.typedAST.core.binding.NameBinding;
import wyvern.tools.typedAST.core.binding.NameBindingImpl;
import wyvern.tools.typedAST.interfaces.*;
import wyvern.tools.types.Environment;
import wyvern.tools.types.Type;
import wyvern.tools.types.TypeResolver;
import wyvern.tools.types.extensions.Arrow;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.EvaluationEnvironment;
import wyvern.tools.util.TreeWriter;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Fn extends CachingTypedAST implements CoreAST, BoundCode {
	private List<NameBinding> bindings;
	TypedAST body;

	public Fn(List<NameBinding> bindings, TypedAST body) {
		this.bindings = bindings;
		this.body = body;
	}

	@Override
	public void writeArgsToTree(TreeWriter writer) {
		writer.writeArgs(bindings, body);
	}

	@Override
	protected Type doTypecheck(Environment env, Optional<Type> expected) {
		Type argType = null;
		for (int i = 0; i < bindings.size(); i++) {
			NameBinding bdgs = bindings.get(i);
			bindings.set(i, new NameBindingImpl(bdgs.getName(), TypeResolver.resolve(bdgs.getType(), env)));
		}

		if (bindings.size() == 0)
			argType = new Unit();
		else if (bindings.size() == 1)
			argType = bindings.get(0).getType();
		else
			// TODO: implement multiple args
			throw new RuntimeException("tuple args not implemented");
		
		Environment extEnv = env;
		for (NameBinding bind : bindings) {
			extEnv = extEnv.extend(bind);
		}

		Type resultType = body.typecheck(extEnv, expected.map(exp -> ((Arrow)exp).getResult()));
		return new Arrow(argType, resultType);
	}

	@Override
	public Value evaluate(EvaluationEnvironment env) {
		return new Closure(this, env);
	}

	@Override
	public List<NameBinding> getArgBindings() {
		return bindings;
	}

	@Override
	public TypedAST getBody() {
		return body;
	}

	@Override
	public void accept(CoreASTVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public Map<String, TypedAST> getChildren() {
		Hashtable<String, TypedAST> children = new Hashtable<>();
		children.put("body", body);
		return children;
	}

	@Override
	public TypedAST doClone(Map<String, TypedAST> nc) {
		return new Fn(bindings, nc.get("body"));
	}

	private FileLocation location = FileLocation.UNKNOWN;
	public FileLocation getLocation() {
		return this.location;
	}
}
