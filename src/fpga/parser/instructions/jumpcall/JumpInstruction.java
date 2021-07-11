package fpga.parser.instructions.jumpcall;

import org.apache.commons.lang3.ArrayUtils;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class JumpInstruction extends Instruction{

	public JumpInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		switch(super.name) {
		case "j":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x01 },
					super.toIntBytes(this.operand));
		case "jz":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x11 },
					super.toIntBytes(this.operand));
		case "jnz":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x21 },
					super.toIntBytes(this.operand));
		case "jc":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x31 },
					super.toIntBytes(this.operand));
		case "jnc":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x41 },
					super.toIntBytes(this.operand));
		case "jp":
		case "jge":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x71 },
					super.toIntBytes(this.operand));
		case "jnp":
		case "js":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x81 },
					super.toIntBytes(this.operand));
		case "jg":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0x91 },
					super.toIntBytes(this.operand));
		case "jse":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0xa1 },
					super.toIntBytes(this.operand));
		case "jr":
			return new byte[] { (byte) ((this.srcReg<<4) + this.destReg), (byte)0xf1 };
		case "jgs":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0xb1 },
					super.toIntBytes(this.operand));
		case "jges":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0xc1 },
					super.toIntBytes(this.operand));
		case "jss":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0xd1 },
					super.toIntBytes(this.operand));
		case "jses":
			return ArrayUtils.addAll(new byte[] { 0x00, (byte)0xe1 },
					super.toIntBytes(this.operand));
		}
		
		return null;
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		if (super.name.equals("jr")) {
			if (!super.parseReg(SRC_REG)) {
				throw new FPGAParseException("Unknown JR argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
			}
			return;
		}
		if (!super.parseNumber()) {
			if (!super.parseLabel(parser, f)) {
				throw new FPGAParseException("Unknown JUMP argument in " + f.fileName + ", line " + this.lineCount + ": " + this.source);
			}
		}
	}

}
