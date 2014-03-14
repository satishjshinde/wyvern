package wyvern2.parsing.tests;

import edu.umn.cs.melt.copper.runtime.logging.CopperParserException;
import org.junit.Assert;
import org.junit.Test;
import wyvern.stdlib.Globals;
import wyvern.tools.typedAST.core.declarations.DeclSequence;
import wyvern.tools.typedAST.interfaces.TypedAST;
import wyvern.tools.typedAST.interfaces.Value;
import wyvern.tools.types.Environment;
import wyvern.tools.types.extensions.Int;
import wyvern2.parsing.Wyvern;

import java.io.IOException;
import java.io.StringReader;

public class CopperTests {
	@Test
	public void testVal() throws IOException, CopperParserException {
		String input = "val yx = 2\nyx";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(2)");
	}
	@Test
	public void testAdd() throws IOException, CopperParserException {
		String input = "val yx = 2\nval ts = 9\nyx+ts";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(11)");
	}
	@Test
	public void testMult() throws IOException, CopperParserException {
		String input = "val yx = 2\nval ts = 9+7*2\nyx+ts";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(25)");
	}
	@Test
	public void testParens() throws IOException, CopperParserException {
		String input = "val yx = 2\nval ts = 9+(5+2)*2\nyx+ts";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		res.typecheck(Environment.getEmptyEnvironment());
		Value v = res.evaluate(Environment.getEmptyEnvironment());
		Assert.assertEquals(v.toString(), "IntegerConstant(25)");
	}
	@Test
	public void testDecls() throws IOException, CopperParserException {
		String input =
				"def foo():Int = 5\n" +
						"def bar():Int\n" +
						"	9\n" +
						"bar()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(9)");
	}
	@Test
	public void testFwdDecls() throws IOException, CopperParserException {
		String input =
				"def foo():Int = bar()+20\n" +
						"def bar():Int\n" +
						"	9\n" +
						"foo()";
		TypedAST res = (TypedAST)new Wyvern().parse(new StringReader(input), "test input");
		Assert.assertEquals(res.typecheck(Globals.getStandardEnv()), Int.getInstance());
		Assert.assertEquals(res.evaluate(Globals.getStandardEnv()).toString(), "IntegerConstant(29)");
	}

}