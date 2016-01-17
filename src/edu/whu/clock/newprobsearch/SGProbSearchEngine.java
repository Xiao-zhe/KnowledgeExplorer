package edu.whu.clock.newprobsearch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import edu.whu.clock.generalsearch.SearchPath_ET;
import edu.whu.clock.generalsearch.UnfoldedPatternTree_ET;
import edu.whu.clock.kgraphsearch.SimpleSearchAlgorithm_KG_ET;
import edu.whu.clock.naivesearch_et.SGNaiveSearchAlgorithm_ET;
import edu.whu.clock.newgraph.GraphManager;
import edu.whu.clock.newgraphmatch.ResultTreeTyped;
import edu.whu.clock.newgraphmatch.RevisedVF2Algorithm;

public class SGProbSearchEngine {

	public static final String RESULT_FILE_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/result_file/";
	public static final String LOG_FILE_DIR = "D:/experiment data/knowledge graph explorer/dbpedia-old/search logs/";
	
	private SimpleSearchAlgorithm_KG_ET ssAlgorithm;
	private SGProbSearchTypedAlgorithm psAlgorithm;
	private SGProbSearchTypedAlgorithm psAlgorithm_fnns;
	private JointProbTopkSearchAlgorithm_SG_ET jpsAlgorithm;
	private SGNaiveSearchAlgorithm_ET nsAlgorithm;
	private RevisedVF2Algorithm gmAlgorithm;
	private GraphManager graphManager;
	private BufferedWriter testResultWriter;

	private boolean needLog = true;

	public SGProbSearchEngine(GraphManager graphManager) {
		this.graphManager = graphManager;
	}

	/**
	 * @为了做完整的对比分析，初始化所有变量。
	 */
	public void initAll() {
		graphManager.genClassManager();
		graphManager.genInstanceManager();
		graphManager.genEdgeTypeManager();
		graphManager.genSummaryGraphTyped();
		graphManager.genEntityGraphTyped();
		graphManager.initPKIndexManager_EdgeCount();
		graphManager.loadCPTableTypedManager();
		graphManager.loadFNSetManager();

		Date now = new Date();
		String logDir = LOG_FILE_DIR + now.toString().replace(":", "-") + "/";
		File dir = new File(logDir);
		if (!dir.mkdir()) {
			System.out.println("Error: cannot make the log dir.");
			return;
		}
		try {
			ssAlgorithm = new SimpleSearchAlgorithm_KG_ET(graphManager.entityGraphTyped, graphManager.pkIndex, logDir);
			jpsAlgorithm = new JointProbTopkSearchAlgorithm_SG_ET(
					graphManager.summaryGraphTyped, graphManager.pkIndex,
					graphManager.cpTableTyped, graphManager.fnSet, logDir);
			psAlgorithm_fnns = new SGProbSearchTypedAlgorithm(
					graphManager.summaryGraphTyped, graphManager.pkIndex,
					graphManager.cpTableTyped, graphManager.fnSet, logDir);
			psAlgorithm = new SGProbSearchTypedAlgorithm(
					graphManager.summaryGraphTyped, graphManager.pkIndex,
					graphManager.cpTableTyped, logDir);
			nsAlgorithm = new SGNaiveSearchAlgorithm_ET(
					graphManager.summaryGraphTyped, graphManager.pkIndex, logDir);
			gmAlgorithm = new RevisedVF2Algorithm(graphManager, logDir);
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

//	public void init(boolean needFNSet) {
//		graphManager.genClassManager();
//		graphManager.genInstanceManager();
//		graphManager.genEdgeTypeManager();
//		graphManager.genSummaryGraphTyped();
//		graphManager.genEntityGraphTyped();
//		graphManager.initPKIndexManager();
//		// graphManager.genPKIndexManager(true);
//		graphManager.loadCPTableTypedManager();
////		graphManager.initEntityInvertedIndexManager();
//
//		nsAlgorithm = new SGNaiveSearchAlgorithm_ET(
//				graphManager.summaryGraphTyped, graphManager.pkIndex);
//		if (needFNSet) {
//			graphManager.loadFNSetManager();
//			psAlgorithm = new SGProbSearchTypedAlgorithm(
//					graphManager.summaryGraphTyped, graphManager.pkIndex,
//					graphManager.cpTableTyped, graphManager.fnSet);
//		} else {
//			psAlgorithm = new SGProbSearchTypedAlgorithm(
//					graphManager.summaryGraphTyped, graphManager.pkIndex,
//					graphManager.cpTableTyped);
//		}
//	}

	public void shutdown() throws IOException {
		if (gmAlgorithm != null) gmAlgorithm.closeLogWriter();
		if (jpsAlgorithm != null) jpsAlgorithm.closeLogWriter();
		if (psAlgorithm_fnns != null) psAlgorithm_fnns.closeLogWriter();
		if (psAlgorithm != null) psAlgorithm.closeLogWriter();
		if (nsAlgorithm != null) nsAlgorithm.closeLogWriter();
		if (ssAlgorithm != null) ssAlgorithm.closeLogWriter();
		if (graphManager.pkIndex != null) graphManager.pkIndex.close();
		if (testResultWriter != null) testResultWriter.close();
	}

	/**
	 * 随机生成一个关键词查询，然后使用多种搜索算法进行处理，并对搜索结果做对比分析。
	 * @param k 返回pattern tree的个数
	 * @param queryMaxSize 随机查询包含关键词的最大个数
	 * @throws IOException
	 */
	public void randomComparisonTest(int k, int queryMaxSize)
			throws IOException {
		String[] keywords = KeywordQueryManager
				.randomKeywordQuery(queryMaxSize);
		System.out.println(Arrays.toString(keywords));
		testResultWriter.write(Arrays.toString(keywords));
		testResultWriter.newLine();
		testResultWriter.write("searching top-" + k);
		testResultWriter.newLine();
		int num = 0;
		int success = 0;
		int position = 0;
		Date start = new Date();
		UnfoldedPatternTree_ET[] result = psAlgorithm_fnns.run(keywords, k, needLog);
		Date end = new Date();
		long time1 = end.getTime()-start.getTime();
		for (UnfoldedPatternTree_ET queryGraph : result) {
			if (queryGraph != null) {
				num++;
				if (verify(keywords, queryGraph)) {
					success++;
					position += num;
				}
			}
		}
		double successRate1 = (double) success / num;
		double positionAvg1 = (double) position / success;
		num = 0;
		success = 0;
		position = 0;
		start = new Date();
		result = psAlgorithm.run(keywords, k, needLog);
		end = new Date();
		long time2 = end.getTime()-start.getTime();
		for (UnfoldedPatternTree_ET queryGraph : result) {
			if (queryGraph != null) {
				num++;
				if (verify(keywords, queryGraph)) {
					success++;
					position += num;
				}
			}
		}
		double successRate2 = (double) success / num;
		double positionAvg2 = (double) position / success;
		num = 0;
		success = 0;
		position = 0;
		start = new Date();
		UnfoldedPatternTree_ET[] ns = nsAlgorithm.run(keywords, k, needLog);
		end = new Date();
		long time3 = end.getTime()-start.getTime();
		for (UnfoldedPatternTree_ET queryGraph : ns) {
			if (queryGraph != null) {
				num++;
				if (verify(keywords, queryGraph)) {
					success++;
					position += num;
				}
			}
		}
		double successRate3 = (double) success / num;
		double positionAvg3 = (double) position / success;
		testResultWriter.write("<success rate: " + successRate1 + " | "
				+ successRate2 + " | " + successRate3 + ">");
		testResultWriter.newLine();
		testResultWriter.write("<position avg: " + positionAvg1 + " | "
				+ positionAvg2 + " | " + positionAvg3 + ">");
		testResultWriter.newLine();
		testResultWriter.write("<response time: " + time1 + " | "
				+ time2 + " | " + time3 + ">");
		testResultWriter.newLine();
	}
	
	public void randomComparisonTest(int radix, int series, int queryMaxSize)
			throws IOException {
		String[] keywords = KeywordQueryManager
				.randomKeywordQuery(queryMaxSize);
		ssAlgorithm.run(keywords, radix*series);
		int x = ssAlgorithm.numOfResult();
		testResultWriter.write("number of real answers: " + x);
		testResultWriter.newLine();
		if (x == 0) return;
		System.out.println(Arrays.toString(keywords));
		testResultWriter.write(Arrays.toString(keywords));
		testResultWriter.newLine();
		int num1 = 0, num2 = 0, num3 = 0, num4 = 0;
		int success1 = 0, success2 = 0, success3 = 0, success4 = 0;
		int position1 = 0, position2 = 0, position3 = 0, position4 = 0;
		Date start = new Date();
		UnfoldedPatternTree_ET[] result1 = jpsAlgorithm.run(keywords, radix*series, needLog);
		Date end = new Date();
		long time1 = end.getTime()-start.getTime();
		start = new Date();
		UnfoldedPatternTree_ET[] result2 = psAlgorithm_fnns.run(keywords, radix*series, needLog);
		end = new Date();
		long time2 = end.getTime()-start.getTime();
		start = new Date();
		UnfoldedPatternTree_ET[] result3 = psAlgorithm.run(keywords, radix*series, needLog);
		end = new Date();
		long time3 = end.getTime()-start.getTime();
		start = new Date();
		UnfoldedPatternTree_ET[] result4 = nsAlgorithm.run(keywords, radix*series, needLog);
		end = new Date();
		long time4 = end.getTime()-start.getTime();
		testResultWriter.write("<response time: " + time1 + " | "
				+ time2 + " | " + time3 + " | " + time4 + ">");
		testResultWriter.newLine();
		for (int i = 0; i < series; i++) {
			testResultWriter.write("searching top-" + radix*(i+1));
			testResultWriter.newLine();
			for (int j = radix*i; j < radix*(i+1); j++) {
				if (result1[j] == null) continue;
				num1++;
				if (verify(keywords, result1[j])) {
					success1++;
					position1 += num1;
				}
				System.out.println("[1]: " + j + " verified");
			}
			double successRate1 = (double) success1 / num1;
			double positionAvg1 = (double) position1 / success1;
			
			for (int j = radix*i; j < radix*(i+1); j++) {
				if (result2[j] == null) continue;
				num2++;
				if (verify(keywords, result2[j])) {
					success2++;
					position2 += num2;
				}
				System.out.println("[2]: " + j + " verified");
			}
			double successRate2 = (double) success2 / num2;
			double positionAvg2 = (double) position2 / success2;
			
			for (int j = radix*i; j < radix*(i+1); j++) {
				if (result3[j] == null) continue;
				num3++;
				if (verify(keywords, result3[j])) {
					success3++;
					position3 += num3;
				}
				System.out.println("[3]: " + j + " verified");
			}
			double successRate3 = (double) success3 / num3;
			double positionAvg3 = (double) position3 / success3;
			
			for (int j = radix*i; j < radix*(i+1); j++) {
				if (result4[j] == null) continue;
				num4++;
				if (verify(keywords, result4[j])) {
					success4++;
					position4 += num4;
				}
				System.out.println("[4]: " + j + " verified");
			}
			double successRate4 = (double) success4 / num4;
			double positionAvg4 = (double) position4 / success4;

			testResultWriter.write("<success rate: " + successRate1 + " | "
					+ successRate2 + " | " + successRate3 + " | " + successRate4 + ">");
			testResultWriter.newLine();
			testResultWriter.write("<position avg: " + positionAvg1 + " | "
					+ positionAvg2 + " | " + positionAvg3 + " | " + positionAvg4 + ">");
			testResultWriter.newLine();
		}
	}

	public void comparisonTest(String[] keywords, int k) throws IOException {
		ssAlgorithm.run(keywords, k);
		int x = ssAlgorithm.numOfResult();
		testResultWriter.write("number of real answers: " + x);
		testResultWriter.newLine();
		if (x == 0) return;
		System.out.println(Arrays.toString(keywords));
		testResultWriter.write(Arrays.toString(keywords));
		testResultWriter.newLine();
		int num1 = 0, num2 = 0, num3 = 0, num4 = 0;
		int success1 = 0, success2 = 0, success3 = 0, success4 = 0;
		int position1 = 0, position2 = 0, position3 = 0, position4 = 0;
		Date start = new Date();
		UnfoldedPatternTree_ET[] result1 = jpsAlgorithm.run(keywords, k, needLog);
		Date end = new Date();
		long time1 = end.getTime()-start.getTime();
		start = new Date();
		UnfoldedPatternTree_ET[] result2 = psAlgorithm_fnns.run(keywords, k, needLog);
		end = new Date();
		long time2 = end.getTime()-start.getTime();
		start = new Date();
		UnfoldedPatternTree_ET[] result3 = psAlgorithm.run(keywords, k, needLog);
		end = new Date();
		long time3 = end.getTime()-start.getTime();
		start = new Date();
		UnfoldedPatternTree_ET[] result4 = nsAlgorithm.run(keywords, k, needLog);
		end = new Date();
		long time4 = end.getTime()-start.getTime();
		testResultWriter.write("<response time: " + time1 + " | "
				+ time2 + " | " + time3 + " | " + time4 + ">");
		testResultWriter.newLine();
		testResultWriter.write("searching top-" + k);
		testResultWriter.newLine();
		for (int j = 0; j < k; j++) {
			if (result1[j] == null) continue;
			num1++;
			if (verify(keywords, result1[j])) {
				success1++;
				position1 += num1;
			}
			System.out.println("[1]: " + j + " verified");
		}
		double successRate1 = (double) success1 / num1;
		double positionAvg1 = (double) position1 / success1;
		
		for (int j = 0; j < k; j++) {
			if (result2[j] == null) continue;
			num2++;
			if (verify(keywords, result2[j])) {
				success2++;
				position2 += num2;
			}
			System.out.println("[2]: " + j + " verified");
		}
		double successRate2 = (double) success2 / num2;
		double positionAvg2 = (double) position2 / success2;
		
		for (int j = 0; j < k; j++) {
			if (result3[j] == null) continue;
			num3++;
			if (verify(keywords, result3[j])) {
				success3++;
				position3 += num3;
			}
			System.out.println("[3]: " + j + " verified");
		}
		double successRate3 = (double) success3 / num3;
		double positionAvg3 = (double) position3 / success3;
		
		for (int j = 0; j < k; j++) {
			if (result4[j] == null) continue;
			num4++;
			if (verify(keywords, result4[j])) {
				success4++;
				position4 += num4;
			}
			System.out.println("[4]: " + j + " verified");
		}
		double successRate4 = (double) success4 / num4;
		double positionAvg4 = (double) position4 / success4;

		testResultWriter.write("<success rate: " + successRate1 + " | "
				+ successRate2 + " | " + successRate3 + " | " + successRate4 + ">");
		testResultWriter.newLine();
		testResultWriter.write("<position avg: " + positionAvg1 + " | "
				+ positionAvg2 + " | " + positionAvg3 + " | " + positionAvg4 + ">");
		testResultWriter.newLine();
	}

	public UnfoldedPatternTree_ET[] probSearch(String[] keywords, int k)
			throws IOException {
		return psAlgorithm.run(keywords, k, needLog);
	}
	
	public UnfoldedPatternTree_ET[] naiveSearch(String[] keywords, int k)
			throws IOException {
		return nsAlgorithm.run(keywords, k, needLog);
	}

	public boolean verify(String[] keywords, UnfoldedPatternTree_ET queryGraph) throws IOException {
		ResultTreeTyped result = gmAlgorithm.run(keywords, queryGraph);
		if (result == null) {
//			System.out.println("No matched entity tree.");
			return false;
		}
//		printResultTreeTyped(result);
		return true;
	}

	// If we allow that a logical keyword in the query can have multiple words
	// private PKIndexEntry getEntry(String keyword) {
	// String[] words = keyword.split(" ");
	// if (words.length == 1) {
	// return index.get(words[0]);
	// }
	// else if (words.length == 2) {
	// return combineTwoEntries(words[0], words[1]);
	// }
	// else {
	// System.out.println(keyword + " contains too many words!");
	// return null;
	// }
	// }
	//
	// private ProbIndexEntry combineTwoEntries(String a, String b) {
	// ProbIndexEntry ea = index.get(a);
	// ProbIndexEntry eb = index.get(b);
	// if (ea == null || eb == null) {
	// return null;
	// }
	// int pos = 0;
	// ArrayList<Integer> newClassIDList = new ArrayList<Integer>();
	// ArrayList<Double> newProbList = new ArrayList<Double>();
	// for (int i = 0; i < ea.getClassIDList().length; i++) {
	// for (int j = pos; j < eb.getClassIDList().length; j++) {
	// if (ea.getClassID(i) == eb.getClassID(j)) {
	// newClassIDList.add(ea.getClassID(i));
	// newProbList.add(ea.getProb(i) * eb.getProb(j));
	// pos = j + 1;
	// break;
	// }
	// else if (ea.getClassID(i) < eb.getClassID(j)) {
	// pos = j;
	// break;
	// }
	// pos++;
	// }
	// }
	// int[] classIDList = new int[newClassIDList.size()];
	// double[] probList = new double[newClassIDList.size()];
	// for (int i = 0; i < classIDList.length; i++) {
	// classIDList[i] = newClassIDList.get(i);
	// probList[i] = newProbList.get(i);
	// }
	// return new ProbIndexEntry(classIDList, probList);
	// }

	public void initTestResultWriter() throws IOException {
		Date now = new Date();
		String filename = RESULT_FILE_DIR + "result "
				+ now.toString().replaceAll(":", "-") + ".txt";
		setTestResultWriter(new BufferedWriter(new FileWriter(filename)));
	}

	public void setTestResultWriter(BufferedWriter testResultWriter) {
		this.testResultWriter = testResultWriter;
	}

	public void print(UnfoldedPatternTree_ET[] result) {
		int i = 1;
		for (UnfoldedPatternTree_ET answer : result) {
			if (answer != null) {
				System.out.println("Top-" + i + "  "
						+ answer.getString(graphManager.summaryGraphTyped));
				// printUnfoldedProbPatternTreeTyped(answer);
				i++;
			}
		}
	}

	public void printUnfoldedPatternTree_ET(UnfoldedPatternTree_ET tree) {
		System.out.print("{" + tree.getRoot() + "."
				+ graphManager.classManager.getClassName(tree.getRoot())
				+ " --- ");
		SearchPath_ET[] paths = tree.getPaths();
		for (SearchPath_ET path : paths) {
			String str = "(";
			for (int i = 0; i < path.nodeNum() - 1; i++) {
				str += ", "
						+ path.getNode(path.nodeNum() - i - 1)
						+ "."
						+ graphManager.classManager.getClassName(path
								.getNode(path.nodeNum() - i - 1));
			}
			System.out.print(str + ")");
		}
		System.out.println("} : " + tree.getScore());
	}

	public void printResultTreeTyped(ResultTreeTyped result) {
		for (int i = 0; i < result.getPaths().length; i++) {
			System.out.print("path" + i + "  ");
			for (int j = 0; j < result.getPath(i).length; j++) {
				// System.out.print(result.getNode(i, j) + "  ");
				System.out.print(result.getNode(i, j)
						+ "."
						+ graphManager.instanceManager.getInstanceName(result
								.getNode(i, j)) + " <-- ");
			}
			System.out.println();

		}
	}

	public static void main(String[] args) {
		GraphManager graphManager = new GraphManager();
		SGProbSearchEngine se = null;
		try {
			se = new SGProbSearchEngine(graphManager);
			se.initAll();
			se.initTestResultWriter();
			// se.setTestResultWriter(new BufferedWriter(new
			// FileWriter(RESULT_FILE_DIR+"result.txt")));
//			se.comparisonTest(new String[]{"45170", "15663", "wittrockiella"}, 10);
//			se.comparisonTest(KeywordQueryManager.QUERIES[0], 10);
			for (int i = 0; i < 10; i++) {
				System.out.println("query #" + i);
				se.randomComparisonTest(10, 1, 4);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				se.shutdown();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
//	public static void main(String[] args) {
//		GraphManager graphManager = new GraphManager();
//		SGProbSearchEngine se = null;
//		try {
//			se = new SGProbSearchEngine(graphManager);
//			se.initAll();
//			se.initTestResultWriter();
//			UnfoldedPatternTree_ET[] result = se.naiveSearch(KeywordQueryManager.QUERIES[16], 10);
//			se.print(result);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				se.shutdown();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

}
