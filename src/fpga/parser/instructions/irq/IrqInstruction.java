package fpga.parser.instructions.irq;

import org.apache.commons.lang3.ArrayUtils;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;
import fpga.parser.instructions.Instruction;
import fpga.parser.objects.Function;

public class IrqInstruction extends Instruction{

	public IrqInstruction(String name) {
		super(name);
	}

	@Override
	public byte[] generate() {
		// irq operand
		return ArrayUtils.addAll(new byte[] { (byte)this.operand, (byte)0xc0 });

	}

	@Override
	public void prepare(Parser parser, Function f) throws FPGAParseException {
		// irq 0bnnnn000[1|0]
		if (!super.parseNumber()) {
			throw new FPGAParseException("Unknown " + super.name.toUpperCase() + " argument in " + f.fileName + ", line "
					+ this.lineCount + ": " + this.source);
		}
	}

}
