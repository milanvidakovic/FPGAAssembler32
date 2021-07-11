package fpga.parser.instructions;

import java.util.HashMap;
import java.util.Map;

import fpga.parser.instructions.arithm.ArithmInstruction;
import fpga.parser.instructions.io.InOutInstruction;
import fpga.parser.instructions.irq.IntInstruction;
import fpga.parser.instructions.irq.IrqInstruction;
import fpga.parser.instructions.jumpcall.CallInstruction;
import fpga.parser.instructions.jumpcall.JumpInstruction;
import fpga.parser.instructions.mem.LdInstruction;
import fpga.parser.instructions.mem.StInstruction;
import fpga.parser.instructions.mov.BlitInstruction;
import fpga.parser.instructions.mov.MovInstruction;
import fpga.parser.instructions.mov.SexInstruction;
import fpga.parser.instructions.mov.ZexInstruction;
import fpga.parser.instructions.stack.PushPopInstruction;
import fpga.parser.instructions.stack.RetInstruction;

public class Instructions {
	public Map<String, Instruction> instructions;
	
	public Instructions() {
		this.instructions = new HashMap<String, Instruction>();
		
		this.instructions.put("nop", new NopHaltInstruction("nop"));
		this.instructions.put("mov.w", new MovInstruction("mov.w"));
		this.instructions.put("mov.s", new MovInstruction("mov.s"));
		this.instructions.put("mov.b", new MovInstruction("mov.b"));
		
		this.instructions.put("in", new InOutInstruction("in"));
		this.instructions.put("out", new InOutInstruction("out"));
		
		this.instructions.put("push", new PushPopInstruction("push"));
		this.instructions.put("pop", new PushPopInstruction("pop"));
		this.instructions.put("ret", new RetInstruction("ret"));
		this.instructions.put("iret", new RetInstruction("iret"));
		this.instructions.put("swap", new MovInstruction("swap"));
		this.instructions.put("irq", new IrqInstruction("irq"));
		this.instructions.put("halt", new NopHaltInstruction("halt"));
		
		this.instructions.put("j", new JumpInstruction("j"));
		this.instructions.put("jz", new JumpInstruction("jz"));
		this.instructions.put("jnz", new JumpInstruction("jnz"));
		this.instructions.put("jc", new JumpInstruction("jc"));
		this.instructions.put("jnc", new JumpInstruction("jnc"));
		this.instructions.put("jp", new JumpInstruction("jp"));
		this.instructions.put("jge", new JumpInstruction("jge"));
		this.instructions.put("jnp", new JumpInstruction("jnp"));
		this.instructions.put("js", new JumpInstruction("js"));
		this.instructions.put("jg", new JumpInstruction("jg"));
		this.instructions.put("jse", new JumpInstruction("jse"));
		this.instructions.put("jr", new JumpInstruction("jr"));
		this.instructions.put("jgs", new JumpInstruction("jgs"));
		this.instructions.put("jges", new JumpInstruction("jges"));
		this.instructions.put("jss", new JumpInstruction("jss"));
		this.instructions.put("jses", new JumpInstruction("jses"));

		this.instructions.put("call", new CallInstruction("call"));
		this.instructions.put("callz", new CallInstruction("callz"));
		this.instructions.put("callnz", new CallInstruction("callnz"));
		this.instructions.put("callr", new CallInstruction("callr"));

		this.instructions.put("ld.w", new LdInstruction("ld.w"));
		this.instructions.put("ld.s", new LdInstruction("ld.s"));
		this.instructions.put("ld.b", new LdInstruction("ld.b"));
		this.instructions.put("st.w", new StInstruction("st.w"));
		this.instructions.put("st.s", new StInstruction("st.s"));
		this.instructions.put("st.b", new StInstruction("st.b"));
		
		
		this.instructions.put("add.w", new ArithmInstruction("add.w"));
		this.instructions.put("add.s", new ArithmInstruction("add.s"));
		this.instructions.put("add.b", new ArithmInstruction("add.b"));
		this.instructions.put("sub.w", new ArithmInstruction("sub.w"));
		this.instructions.put("sub.s", new ArithmInstruction("sub.s"));
		this.instructions.put("sub.b", new ArithmInstruction("sub.b"));
		this.instructions.put("and.w", new ArithmInstruction("and.w"));
		this.instructions.put("and.s", new ArithmInstruction("and.s"));
		this.instructions.put("and.b", new ArithmInstruction("and.b"));
		this.instructions.put("or.w", new ArithmInstruction("or.w"));
		this.instructions.put("or.s", new ArithmInstruction("or.s"));
		this.instructions.put("or.b", new ArithmInstruction("or.b"));
		this.instructions.put("xor.w", new ArithmInstruction("xor.w"));
		this.instructions.put("xor.s", new ArithmInstruction("xor.s"));
		this.instructions.put("xor.b", new ArithmInstruction("xor.b"));
		this.instructions.put("neg.w", new ArithmInstruction("neg.w"));
		this.instructions.put("neg.s", new ArithmInstruction("neg.s"));
		this.instructions.put("neg.b", new ArithmInstruction("neg.b"));
		this.instructions.put("shl.w", new ArithmInstruction("shl.w"));
		this.instructions.put("shl.s", new ArithmInstruction("shl.s"));
		this.instructions.put("shl.b", new ArithmInstruction("shl.b"));
		this.instructions.put("shr.w", new ArithmInstruction("shr.w"));
		this.instructions.put("shr.s", new ArithmInstruction("shr.s"));
		this.instructions.put("shr.b", new ArithmInstruction("shr.b"));
		this.instructions.put("mul.w", new ArithmInstruction("mul.w"));
		this.instructions.put("mul.s", new ArithmInstruction("mul.s"));
		this.instructions.put("mul.b", new ArithmInstruction("mul.b"));
		this.instructions.put("div.w", new ArithmInstruction("div.w"));
		this.instructions.put("div.s", new ArithmInstruction("div.s"));
		this.instructions.put("div.b", new ArithmInstruction("div.b"));
		this.instructions.put("inc.w", new ArithmInstruction("inc.w"));
		this.instructions.put("inc.s", new ArithmInstruction("inc.s"));
		this.instructions.put("inc.b", new ArithmInstruction("inc.b"));
		this.instructions.put("dec.w", new ArithmInstruction("dec.w"));
		this.instructions.put("dec.s", new ArithmInstruction("dec.s"));
		this.instructions.put("dec.b", new ArithmInstruction("dec.b"));
		this.instructions.put("cmp.w", new ArithmInstruction("cmp.w"));
		this.instructions.put("cmp.s", new ArithmInstruction("cmp.s"));
		this.instructions.put("cmp.b", new ArithmInstruction("cmp.b"));
		this.instructions.put("inv.w", new ArithmInstruction("inv.w"));
		this.instructions.put("inv.s", new ArithmInstruction("inv.s"));
		this.instructions.put("inv.b", new ArithmInstruction("inv.b"));

		this.instructions.put("fadd", new ArithmInstruction("fadd"));
		this.instructions.put("fsub", new ArithmInstruction("fsub"));
		this.instructions.put("fmul", new ArithmInstruction("fmul"));
		this.instructions.put("fdiv", new ArithmInstruction("fdiv"));
		this.instructions.put("int",  new IntInstruction("int"));

		this.instructions.put("zex.b", new ZexInstruction("zex.b"));
		this.instructions.put("zex.s", new ZexInstruction("zex.s"));
		this.instructions.put("sex.b", new SexInstruction("sex.b"));
		this.instructions.put("sex.s", new SexInstruction("sex.s"));
		this.instructions.put("blit", new BlitInstruction("blit"));

	}
}
