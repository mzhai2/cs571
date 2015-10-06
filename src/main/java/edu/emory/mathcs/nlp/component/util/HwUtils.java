package edu.emory.mathcs.nlp.component.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class HwUtils {
	protected Map<String, String> map = new HashMap<>();
	
	public void createAndSetMap(String path) throws IOException
	{
		String line;
		Map<String, String> answer = new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		while((line = reader.readLine()) != null ) {
			String[] tmp = line.split("\\s");
			answer.put(tmp[1], replace(tmp[0]));
		}
		map = answer;
	}
	
	private String replace(String a) {
		return (a.replace("0", "a").replace("1", "b"));
	}

	public void addField(String path) throws IOException
	{
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(path));
		PrintWriter writer = new PrintWriter(path, "UTF-8");
		StringJoiner join = new StringJoiner("\t"); 
		while((line = reader.readLine()) != null) {
			String[] tmp = line.split("\\s");
			String append = map.get(tmp[1]);
			for (String item : tmp) join.add(item);
			join.add(append);
			writer.println(join.toString());
		}
		writer.close();
	}
}
