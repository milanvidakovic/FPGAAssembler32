package fpga.parser.instructions.mov;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class SexInstruction extends Instruction {

	public SexInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		if (this.name.endsWith(".b")) {
			// sex.b reg, reg
			return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x6b };
		} else if (this.name.endsWith(".s")) {
			// sex.s reg, reg
			return new byte[] { (byte) ((this.srcReg << 4) + this.destReg), (byte) 0x7b };
		}
		return null;
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {

		if (!super.parseRegReg(Instruction.SRC_REG) || !super.parseRegReg(Instruction.DEST_REG)) {
			throw new FPGAParseException(
					"Unknown SEX argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
		}
	}
}
