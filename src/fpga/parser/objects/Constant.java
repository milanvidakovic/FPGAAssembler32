package fpga.parser.objects;

public class Constant extends AssemblerObject {

	public Constant(String name, int val) {
		this.name = name;
		this.address = val;
	}
}
