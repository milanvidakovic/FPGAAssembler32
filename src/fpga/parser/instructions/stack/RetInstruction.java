package fpga.parser.instructions.stack;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class RetInstruction extends Instruction {

	public RetInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		if (this.source.equals("ret")) {
			return new byte[] {0, (byte) 0x80};
		} else {
			return new byte[] {0, (byte) 0x90}; // iret
		}
	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		
	}

}
