package fpga.parser.instructions.stack;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class PushPopInstruction extends Instruction {
	boolean isPush;
	
	boolean pushReg;
	
	public PushPopInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		if (this.isPush) {
			if (this.pushReg) {
				// push reg
				return new byte[]{(byte)this.destReg, 0x50};
			} else {
				// push number
				byte[] operandBytes = super.toIntBytes(this.operand);
				return new byte[]{0, 0x60, operandBytes[0], operandBytes[1], operandBytes[2], operandBytes[3]};
			}
		} else {
			// pop reg
			return new byte[]{ (byte)this.destReg, 0x70};
		}
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		if (this.source.startsWith("push")) {
			this.isPush = true;
			// push
			if (super.parseReg(Instruction.DEST_REG)) {
				// push register
				this.pushReg = true;
			} else {
				// push number
				this.pushReg = false;
				if (!super.parseNumber()) {
					if (!super.parseLabel(parser, f)) {
						throw new FPGAParseException("Unknown PUSH argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
					}
				}
			}
		} else { 
			// pop
			this.isPush = false;
			if (!super.parseReg(Instruction.DEST_REG)) 
				throw new FPGAParseException("Unknown POP argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
			
		}
	}
	
}
