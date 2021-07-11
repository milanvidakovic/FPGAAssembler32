package fpga.parser.instructions;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.objects.Function;

public class NopHaltInstruction extends Instruction {

	public NopHaltInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		if (this.source.equals("nop")) {
			return new byte[] {0, 0}; // nop
		} else {
			return new byte[] {(byte) 0xFF, (byte) 0xF0}; // halt
		}
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		
	}

}
