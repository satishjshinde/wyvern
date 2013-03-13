package wyvern.tools.parsing.extensions;

import static wyvern.tools.parsing.ParseUtils.parseSymbol;
import wyvern.tools.parsing.LineParser;
import wyvern.tools.parsing.ParseUtils;
import wyvern.tools.rawAST.ExpressionSequence;
import wyvern.tools.rawAST.Symbol;
import wyvern.tools.typedAST.TypedAST;
import wyvern.tools.typedAST.binding.TypeBinding;
import wyvern.tools.typedAST.extensions.TypeInstance;
import wyvern.tools.typedAST.extensions.declarations.PropDeclaration;
import wyvern.tools.typedAST.extensions.declarations.ValDeclaration;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Unit;
import wyvern.tools.util.Pair;

public class PropParser implements LineParser {
	private PropParser() { }
	private static PropParser instance = new PropParser();
	public static PropParser getInstance() { return instance; }
	

	@Override
	public TypedAST parse(TypedAST first, Pair<ExpressionSequence,Environment> ctx) {
		Symbol s = ParseUtils.parseSymbol(ctx);
		
		String varName = s.name;
		int line = s.getLine();
		
		if (ParseUtils.checkFirst(":", ctx)) {
			parseSymbol(":", ctx);
			String typeName = ParseUtils.parseSymbol(ctx).name;
			if (ParseUtils.checkFirst("?", ctx)) {
				// typeName = typeName + "?"; // FIXME: Just hack for now until NULL/NON-NULL types done.
				ParseUtils.parseSymbol("?", ctx); 
			}
			TypeBinding tb = ctx.second.lookupType(typeName);
			if (tb == null) {
				tb = new TypeBinding(typeName, Unit.getInstance()); // TODO: Implement proper Type for "type"!
			}
			return new PropDeclaration(varName, tb, line);
		} else {
			throw new RuntimeException("Error parsing prop expression : expected.");
		}
	}
}