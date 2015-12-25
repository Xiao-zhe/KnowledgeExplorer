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
		{"Bridgman","Richards"}, //��7����ʵ
//		{"Alabama","Lincoln"},
//		{"Illinois","Hannibal"},
//		{"Mileva","Switzerland"},
//		{"Lamborghini","Ducati"},
		{"vicksburg,","250px|center|dan","totapuri"},
		{"yulee-mallory-reid","methyl-methylimino-oxidoazanium"},//pathNumһֱ����
		{"BBC","News"},
		{"Scotland", "Wales"},//���͵���ҪƵ����ھ�
		{"tower", "yellow", "china"},//�õ�һ��������
		{"Turing","Building"},
		{"George", "Nineteen"},//ѭ����
		{"Scotland", "Ninewells"},
		{"confucius", "america"},
		{"DiCaprio", "Gordon-Levitt", "Page"},//top-100��һ����ʵ��
		{"beijing", "london", "tokyo"},
		{"changjiang", "huanghe"},
		{"ducinfieldi", "colliar"},
		{"losantville,", "s\u00e9niergues", "gcona\u00edle"},
		{"06326", "25,000+"},
		{"4eg", "chenalotte", "conforming"}
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
