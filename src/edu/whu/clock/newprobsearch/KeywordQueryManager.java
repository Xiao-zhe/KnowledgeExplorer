package edu.whu.clock.newprobsearch;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import edu.whu.clock.newgraph.GraphManager;

public class KeywordQueryManager {
	
	private static ArrayList<String> allKeywords;

	public final static String[][] QUERIES = {
		{"04001440", "318i/ci", "6c"}, 
		{"04001577", "owghlan"},
		
		{"Mileva","Switzerland"},
		{"f.i.s.t.","04001476"},
//		{"hawkenbury","fenouillet-du-raz\u00e8s"},
		{"warbler","5r"},//pathNum一直增加
//		{"'n","quartet.","\"vivre\""},
		{"cianci,", "1974-1991","bluewell,"},//典型的需要频繁项集挖掘
		{"1-", "yurok"},//得到一个合理结果
//		{"0-00-615583-9","1:","br\u00e9ole"},
		{"ashwatthama", "bettne"},//循环？
		{"'5", "shusteris"},
		{"renewable", "04001598"},
		{"sanpunkan", "macarthur,"},//top-100有一个真实的
		{"woobay", "04001458"},
		{"yuran", "crawley"},
		{"pelly,", "syrmian","aroona"},
		{"04001432", "$2"},
		{"tico-tico", "exalarius"},
		{"bapaume", "kayumov", "diffissa"},
//		{"abashova", "*i", "verdun\""},
		{"pellex", "@", "04001412"},
		{"bapaume", "owghlan"},
		{"hypotheclini", "04001529", "04001430"},
		{"quartet", "samakal"},
//		{"\u0f58\u0f41\u0f62\u0f0b", "75929", "04001456"},
		{"muteesa", "omului", "*4"},
		{"0268-1242", "4-amino-n-(5-methylisoxazol-3-yl)-benzenesulfonamide", "catsharks"},
		{"einsatzgruppe", "04001405"},
//		{"garland", "-2", "vauxc\u00e9r\u00e9"},
		{"15id(rr)badge", "2", "1%"},
		{"cutajar", "3n"},
		{"07", "56011,"},
		{"-3", "8s,9s,10r,11r,13s,14s,17s)-11,17-dihydroxy-10,13,17-trimethyl-3-oxo-7,8,9,11,12,14,15,16-octahydro-6h-cyclopentaaphenanthrene-2-carbaldehyde", "16"},
		{"cortalim", "quartery"},
//		{"szabolcsb\u00e1ka", "04001445", "3f"},
		{"48","52"},
		{"55","meibom"},
		{"listening!", "978-0-7653-0369-1"},
		{"areolana", "vargas-aguilera", "zorita"},
		{"pellia", "(p"},
		{"calceranica", "ovalipennis", "yuran"},
//		{"albicilla", "zapala\u010d", "tetraceratobunus"},
//		{"\"amediyah\",", "6f"},
		{"(j", "attend,", "kpai-lp"},
		{"garryales", "yurei", "58"},
		{"hajjam", "sully,", "hyper-gravity"},
		{"wisc-tv)", "magnoleptus", "aronya"},
		{"aronde", "560078"},
		{"70", "49", "8-"},
		{"aeolothripidae", "04001430", "[(2s,3as,7as)-1((r,r)-2-phenylcyclopropyl-"},
		{"#a", "ex-elemir"}
		
	};
	
	public static String[] randomKeywordQuery(int queryMaxSize){
		if (allKeywords == null) {
			allKeywords = new ArrayList<String>(1400000);
			try {
				BufferedReader br = new BufferedReader(new FileReader(GraphManager.KEYWORD_FILE));
				while(br.ready()){
					allKeywords.add(br.readLine());
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Random random = new Random(new Date().getTime());
		int querySize = random.nextInt(queryMaxSize - 1) + 2;
		String[] keywordQuery = new String[querySize];
		int max = allKeywords.size();
		for(int i=0;i<querySize;i++){
			int randomIndex = random.nextInt(max);
			keywordQuery[i] = allKeywords.get(randomIndex);
		}
		return keywordQuery;
	}
}
