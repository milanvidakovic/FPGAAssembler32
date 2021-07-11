package fpga.parser.objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import fpga.parser.FPGAParseException;
import fpga.parser.instructions.Instruction;

public class Variable extends AssemblerObject {
	public int size;
	public byte[] content;
	public int itemCount;
	
	public List<Instruction> referredFrom = new ArrayList<Instruction>();
	
	public Variable() {
		super();
		this.content = new byte[0];
		this.itemCount = 0;
	}

	public Variable(String name) {
		super(name);
		this.content = new byte[0];
		this.itemCount = 0;
	}
	
	public Variable(AssemblerObject current) {
		this.name = current.name;
		this.stringContent = current.stringContent;
	}

	public void setSize(int size) throws FPGAParseException {
		if (size == 0) throw new FPGAParseException("Wrong size (0) for the variable: " + name); 
		this.size = size;
		this.content = new byte[size];
	}
	
	public void add(byte[] b) {
		byte[] n = new byte[this.itemCount + b.length];
		for (int i = 0; i < this.itemCount; i++)
			n[i] = this.content[i];
		for (int i = 0; i < b.length; i++)
			n[this.itemCount + i] = b[i];
		this.content = n;
		this.itemCount = n.length;
	}

	@Override
	public String toString() {
		return "Variable [name= " + name + ", size=" + size + ", content=" + Hex.encodeHexString(content, false) + ", itemCount=" + itemCount
				+ ", type=" + type + ", dataLabelName=" + dataLabelName + ", address=" + address + "(" + String.format("%08x", address) + ")" + ", referencedFrom=" + super.getReferencedFrom() + "]";
	}
	
}
