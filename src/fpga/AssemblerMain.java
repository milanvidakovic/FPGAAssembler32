package fpga;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fpga.parser.FPGAParseException;
import fpga.parser.Parser;

/*
 * small.s  sprintf.s  string.s  stdio.s  graphics.s  spi.s  umm_malloc.s  fat.s  enc28j60.s  tcpip.s keyboard.s files.s graphics320.s graphics640.s fonts.s consts.s
 */
public class AssemblerMain {

	public static void main(String[] args) {
		List<String> fileNames;
		boolean verbose = false;
		boolean generateSymFile = false;
		int baseAddr = 0xB000;
		boolean fill = false;
		fileNames = new ArrayList<String>();
		for (String s : args) {
			if (s.equals("-v") || s.equals("--verbose")) {
				verbose = true;
				continue;
			} else if (s.equals("-s") || s.equals("--symbol")) {
				generateSymFile = true;
				continue;
			} else if (s.startsWith("-b=")) {
				s = s.substring(s.indexOf("=") + 1).trim();
				if (s.startsWith("0x")) {
					baseAddr = Integer.parseInt(s.substring(2), 16);
				} else {
					baseAddr = Integer.parseInt(s);
				}
				continue;
			} else if (s.equals("-f") || s.equals("--fill")) {
				fill = true;
				continue;
			}
			fileNames.add(s);
		}
		// TODO: -sp=addr -> set the stack pointer in the main function
		if (fileNames.size() == 0) {
			System.out.println("FPGA Assembler. Usage:");
			System.out.println("java -jar FPGA_Assembler.jar [-v|--verbose] [-s|--symbol] [-f|--fill] [-b=base_address] fileName [ fileName ...]");
			System.out.println("-s|--symbol\tGenerates symbol file for each assembler file (used by the emulator, for debugging purpose).\n\t\tDefault is false.");
			System.out.println("-f|--fill\tFills binary file with zeros until base address is reached.\n\t\tDefault is false.");
			System.out.println("-b=base_address\tSets the base address of the executable. Default address is 0xB000.");
			return;
		}
		Parser parser = new Parser(fileNames.get(0));
		BufferedReader in;
		try {
			for (String fileName : fileNames) {
				in = new BufferedReader(new FileReader(fileName));
				String line;
				parser.current = null;
				int lineCount = 0;
				while ((line = in.readLine()) != null) {
					line = line.trim();
					lineCount ++;
					if (line.startsWith("#") || line.equals(""))
						continue;
					parser.parse(line, lineCount, fileName, verbose);
				}
				in.close();
				parser.parse("", lineCount, fileName, verbose); // end
			}
			parser.flattenLabels();
			parser.prepare(baseAddr);
			parser.assemble();
			if (verbose)
				parser.printText();
			parser.save(fixName(fileNames.get(0)) + ".bin", (fill)? baseAddr : 0);
			if (generateSymFile) {
				parser.saveSymFile(fixName(fileNames.get(0)) + ".sym");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FPGAParseException e) {
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
	}

	private static String fixName(String f) {
		if (f.contains(".")) {
			return f.substring(0, f.indexOf('.'));
		} else {
			return f;
		}
	}
}
