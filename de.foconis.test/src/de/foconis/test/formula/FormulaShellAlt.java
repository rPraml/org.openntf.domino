/* Generated By:JJTree&JavaCC: Do not edit this line. AtFormulaParser.java */
package de.foconis.test.formula;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jline.ANSIBuffer;
import jline.ArgumentCompletor;
import jline.Completor;
import jline.ConsoleReader;
import jline.SimpleCompletor;
import jline.Terminal;

import org.openntf.domino.Database;
import org.openntf.domino.Document;
import org.openntf.domino.ext.Session.Fixes;
import org.openntf.domino.formula.ASTNode;
import org.openntf.domino.formula.DominoFormatter;
import org.openntf.domino.formula.FormulaContext;
import org.openntf.domino.formula.FormulaParseException;
import org.openntf.domino.formula.FormulaParser;
import org.openntf.domino.formula.Function;
import org.openntf.domino.formula.FunctionFactory;
import org.openntf.domino.formula.impl.NotImplemented;
import org.openntf.domino.thread.DominoThread;
import org.openntf.domino.utils.DominoUtils;
import org.openntf.domino.utils.Factory;

public class FormulaShellAlt implements Runnable {
	private static boolean cacheAST = false;
	private static int count = 10;
	Map<String, ASTNode> astCache = new HashMap<String, ASTNode>();
	private Database db;

	public static void main(final String[] args) {
		DominoThread thread = new DominoThread(new FormulaShellAlt(), "My thread");
		thread.start();
	}

	public FormulaShellAlt() {
		// whatever you might want to do in your constructor, but stay away from Domino objects
	}

	// Some Formatters
	public String NTF(final Object o) {
		ANSIBuffer ab = new ANSIBuffer();
		return ab.cyan(o.toString()).toString();
	}

	public String LOTUS(final Object o) {
		ANSIBuffer ab = new ANSIBuffer();
		return ab.magenta(o.toString()).toString();
	}

	public String ERROR(final Object o) {
		ANSIBuffer ab = new ANSIBuffer();
		return ab.red(o.toString()).toString();
	}

	String formatTime(final long time) {
		return String.format("%.3f ms", (double) time / 1000000);
	}

	@Override
	public void run() {
		try {

			for (Fixes fix : Fixes.values())
				Factory.getSession().setFixEnable(fix, true);
			DominoUtils.setBubbleExceptions(true);

			// RPr: I use  "http://jline.sourceforge.net/" to emulate a shell to test my formula engine
			// I put jline-1.0.jar in jvm/lib/ext
			// In detail: I do not know exactly what I'm doing here... I just need a shell :) 

			ConsoleReader reader = new ConsoleReader();
			reader.setBellEnabled(false);
			//reader.setDebug(new PrintWriter(new FileWriter("writer.debug", true)));

			List<Completor> completors = new LinkedList<Completor>();

			// This code is responsible for autocompletion
			FunctionFactory funcFact = FunctionFactory.getDefaultInstance();
			Collection<Function> funcs = funcFact.getFunctions().values();
			String[] autoComplete = new String[funcs.size() + 3];
			int i = 0;
			for (Function func : funcs) {
				if (func instanceof NotImplemented) {
					autoComplete[i++] = "NotImpl:" + func.getImage();
				} else {
					autoComplete[i++] = func.getImage() + "(";
				}
			}
			autoComplete[i++] = "count=";
			autoComplete[i++] = "astoff";
			autoComplete[i++] = "aston";
			completors.add(new SimpleCompletor(autoComplete));
			reader.addCompletor(new ArgumentCompletor(completors));

			String line;
			// we want some more comfort
			File historyFile = new File("history.txt");
			reader.getHistory().setHistoryFile(historyFile);

			// now start the main loop
			System.out
					.println(NTF("This is the formula shell. Quit with 'q' !!! If you get a NullpointerException, terminate your server!"));
			System.out.println("Session.convertMime is " + LOTUS(Factory.getSession().isConvertMime() ? "enabled" : "disabled"));
			System.out.println("AST-Cache is " + LOTUS(cacheAST ? "on" : "off"));
			System.out.println("Iteration count is " + LOTUS(cacheAST ? "on" : "off"));

			while ((line = reader.readLine("$> ")) != null) {
				if (line.startsWith("count")) {
					int p = line.indexOf('=');
					count = Integer.parseInt(line.substring(p + 1));
					System.out.println("Iteration count is set to: " + LOTUS(count));
					continue;
				}
				if (line.equalsIgnoreCase("astoff")) {
					cacheAST = false;
					System.out.println("AST Cache is set to off");
					continue;
				}

				if (line.equalsIgnoreCase("aston")) {
					cacheAST = true;
					astCache = new HashMap<String, ASTNode>();
					System.out.println("AST Cache is set to on");
					continue;
				}
				if (line.equalsIgnoreCase("q")) {
					break;
				}
				if (line.equalsIgnoreCase("test")) {
					test();
					continue;
				}
				try {
					execute(line);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Bye.");

		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(Factory.dumpCounters(true));
		db = null;
		Factory.terminate();
		System.out.println(Factory.dumpCounters(true));
	}

	private Document createDocument() {
		if (db == null)
			db = Factory.getSession().getDatabase("", "log.nsf");
		Document doc = db.createDocument();
		return doc;
	}

	private void fillDemoDoc(final Map<String, Object> doc, final double rndVal) {

		doc.put("rnd", new double[] { rndVal });

		//		doc.put("text1", "This is a test string");
		//		doc.put("text2", new String[] { "1", "2", "3" });
		//
		//		doc.put("int1", new int[] { 1 });
		//		doc.put("int2", new int[] { 1, 2, 3 });
		//		Map<String, String> map = new HashMap<String, String>();
		//		map.put("K1", "v1");
		//		map.put("K2", "v2");
		//		doc.put("mime1", map);

	}

	private ASTNode parse(final String line) throws FormulaParseException {
		ASTNode ast = null;
		if (cacheAST)
			ast = astCache.get(line);

		FormulaParser parser = FormulaParser.getDefaultInstance();
		if (ast == null) {
			ast = parser.parse(line);
			if (cacheAST)
				astCache.put(line, ast);
		}
		return ast;
	}

	private void test() {
		long time = System.nanoTime();
		int i;
		for (i = 1; i < 10000000;) {
			i = test2(i);
		}
		time = System.nanoTime() - time;
		System.out.println(formatTime(time));
	}

	private int test2(final int i) {
		return i + 1;
	}

	private void execute(final String line) {
		// TODO Auto-generated method stub

		List<Object> ntfDocResult = null;
		List<Object> ntfMapResult = null;
		List<Object> lotusResult = null;

		long time = 0;

		long setupTime = 0;
		long lotusTime = 0;
		long parseTime = 0;
		long docEvaluateTime = 0;
		long mapEvaluateTime = 0;

		for (int i = 0; i < count; i++) {

			// Setup procedure, prepare the demo docs & maps
			time = System.nanoTime();
			double rnd = Math.random();
			Document ntfDoc = createDocument();
			Document lotusDoc = createDocument();
			Map<String, Object> ntfMap = new HashMap<String, Object>();

			fillDemoDoc(ntfDoc, rnd);
			fillDemoDoc(lotusDoc, rnd);
			fillDemoDoc(ntfMap, rnd);
			// Setup completed
			setupTime += System.nanoTime() - time;

			try {
				// benchmark the lotus - evaluate
				time = System.nanoTime();
				lotusResult = Factory.getSession().evaluate(line, lotusDoc);
				lotusTime += System.nanoTime() - time;
			} catch (Exception e) {
				System.out.println(LOTUS("Lotus failed: ") + ERROR(e));
				i = count;
			}

			// benchmark the AtFormulaParser
			ASTNode ast = null;
			try {
				time = System.nanoTime();
				ast = parse(line);
				parseTime += System.nanoTime() - time;
			} catch (FormulaParseException e) {
				System.out.println(NTF("Parser failed: ") + ERROR(e));
				e.printStackTrace();
				break;
			}
			// benchmark the evaluate with a document as context

			try {
				time = System.nanoTime();
				FormulaContext ctx1 = new FormulaContext(ntfDoc, DominoFormatter.getDefaultInstance());
				ntfDocResult = ast.solve(ctx1);
				docEvaluateTime += System.nanoTime() - time;
			} catch (Exception e) {
				System.out.println(NTF("Doc-Evaluate failed: ") + ERROR(e));
				e.printStackTrace();
				i = count;
			}
			try {
				// benchmark the evaluate with a map as context
				time = System.nanoTime();
				FormulaContext ctx2 = new FormulaContext(ntfMap, DominoFormatter.getDefaultInstance());
				ntfMapResult = ast.solve(ctx2);
				mapEvaluateTime += System.nanoTime() - time;
			} catch (Exception e) {
				System.out.println(NTF("Map-Evaluate failed: ") + ERROR(e));
				e.printStackTrace();
				i = count;
			}
		}

		if (compareList(ntfDocResult, lotusResult)) {
			System.out.println("DocResults are equal");
			System.out.println(NTF("DOC\t") + dump(ntfDocResult));
		} else {
			System.out.println("DocResults differs");
			System.out.println(NTF("NTFDOC\t") + dump(ntfDocResult));
			System.out.println(LOTUS("LOTUS\t") + dump(lotusResult));
		}
		if (compareList(ntfMapResult, lotusResult)) {
			System.out.println("MapResults are equal");
		} else {
			System.out.println("MapResults differs");
			System.out.println(NTF("NTFMAP\t") + dump(ntfMapResult));
			System.out.println(LOTUS("LOTUS\t") + dump(lotusResult));
		}

		System.out.println("Setup Time:\t" + formatTime(setupTime));
		System.out.println(LOTUS("Lotus Time:\t" + formatTime(lotusTime)));

		System.out.println(NTF("Parse Time:\t" + formatTime(parseTime)));
		System.out.println(NTF("Evaluate Doc:\t" + formatTime(docEvaluateTime)));
		System.out.println(NTF("Evaluate Map:\t" + formatTime(mapEvaluateTime)));

		System.out.println("Parse+Doc:\t" + formatTime(parseTime + docEvaluateTime) + "\t" + vs(parseTime + docEvaluateTime, lotusTime));
		System.out.println("Parse+Map:\t" + formatTime(parseTime + mapEvaluateTime) + "\t" + vs(parseTime + mapEvaluateTime, lotusTime));

	}

	private String dump(final List<Object> lotus) {
		// TODO Auto-generated method stub
		int width = Terminal.getTerminal().getTerminalWidth() - 20;
		String s = lotus + "";
		if (s.length() > width) {
			s = s.substring(0, width) + "... (l=" + width + ")";
		}

		return s;

	}

	private String vs(final long ntfTime, final long lotusTime) {
		if (ntfTime < 2) {
			return "Unbelievable faster than lotus";
		}
		return String.format("%.2f times faster than lotus", (double) lotusTime / (double) ntfTime);

	}

	private boolean compareMaps(final Map<String, Object> map1, final Map<String, Object> map2) {
		return false;
	}

	private boolean compareList(final List<Object> list1, final List<Object> list2) {
		boolean equals = true;
		if (list1 == null && list2 == null)
			return true;
		if (list1 == null || list2 == null)
			return false;

		if (list1.size() == list2.size()) {
			for (int i = 0; i < list1.size(); i++) {
				Object a = list1.get(i);
				Object b = list2.get(i);
				if (a == null && b == null) {

				} else if (a == null || b == null) {
					equals = false;
					break;
				} else if (a instanceof Number && b instanceof Number) {
					if (Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue()) != 0) {
						equals = false;
						break;
					}
				} else if (!a.equals(b)) {
					equals = false;
					break;
				}
			}
		} else {
			equals = false;
		}
		return equals;
	}
}