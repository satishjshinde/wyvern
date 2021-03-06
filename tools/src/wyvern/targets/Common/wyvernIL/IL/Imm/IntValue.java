package wyvern.targets.Common.wyvernIL.IL.Imm;

import wyvern.targets.Common.wyvernIL.IL.visitor.OperandVisitor;

public class IntValue implements Operand {

	private int value;

	public IntValue(int value) {
		this.value = value;
	}

	@Override
	public <R> R accept(OperandVisitor<R> visitor) {
		return visitor.visit(this);
	}

	public int getValue() {
		return value;
	}
	@Override
	public String toString() {
		return ""+value;
	}
}
