package fpga.parser.instructions.jumpcall;

import org.apache.commons.lang3.ArrayUtils;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class CallInstruction extends Instruction{

	public CallInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		switch(super.name) {
		case "call":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x02 },
					super.toIntBytes(this.operand));
		case "callz":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x12 },
					super.toIntBytes(this.operand));
		case "callnz":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x22 },
					super.toIntBytes(this.operand));
		case "callr":
			return new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0xf2 };
		}
		
		return null;
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		if (this.name.equals("callr")) {
			if (!super.parseReg(Instruction.SRC_REG)) {
				throw new FPGAParseException("Unknown CALLR argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
			}
		}
		else if (!super.parseNumber()) {
			if (!super.parseLabel(parser, f)) {
				throw new FPGAParseException("Unknown CALL argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
			}
		}
	}

}
