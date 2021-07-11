package fpga.parser.instructions.mov;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class ZexInstruction extends Instruction {

	public ZexInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		if (this.name.endsWith(".b")) {
			// zex.b regx, regy -> mov.w regx, regy; and.w regx, 0x000000FF
			// and.w {dest: reg}, {value} ->  4'0x0 @ dest[3:0] @ 4'0x2 @ 4'0xd @ value[31:16]  
			int high = (this.srcReg << 4) + this.destReg;
			return new byte[] { (byte) (high), 0x10 , (byte)(0 + this.destReg), 0x2d, 0x00, 0x00, 0x00, (byte)0xFF};
		} else if (this.name.endsWith(".s")) {
			// zex.s regx, regy -> mov.w regx, regy; and.w regx, 0x0000FFFF
			// and.w {dest: reg}, {value} ->  4'0x0 @ dest[3:0] @ 4'0x2 @ 4'0xd @ value[31:16]  
			int high = (this.srcReg << 4) + this.destReg;
			return new byte[] { (byte) (high), 0x10 , (byte)(0 + this.destReg), 0x2d, 0x00, 0x00, (byte)0xFF, (byte)0xFF};
		}
		return null;
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {

		if (!super.parseRegReg(Instruction.SRC_REG) || !super.parseRegReg(Instruction.DEST_REG)) {
			throw new FPGAParseException(
					"Unknown ZEX argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
		}
	}
}
