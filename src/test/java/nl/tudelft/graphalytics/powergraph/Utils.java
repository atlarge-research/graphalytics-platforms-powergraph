package nl.tudelft.graphalytics.powergraph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import nl.tudelft.graphalytics.validation.GraphStructure;

public class Utils {
	public static File writeGraphToFile(GraphStructure graph) throws IOException {
		File f = File.createTempFile("powergraph", ".txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(f));
		
		for (long v: graph.getVertices()) {
			for (long u: graph.getEdgesForVertex(v)) {
				w.write(String.format("%ld %ld\n", u, v));
			}
		}
		
		w.flush();
		w.close();

		return f;
	}
	
	public static <T> Map<Long, T> readResults(File f, Class<T> clazz) throws Exception {
		Map<Long, T> results = new HashMap<Long, T>();
		
		BufferedReader r = new BufferedReader(new FileReader(f));
		String line;
		
		while ((line = r.readLine()) != null) {
			String tokens[] = line.split(" ", 2);
			
			Long vertex = Long.valueOf(tokens[0]);
			T value = clazz.getDeclaredConstructor(String.class).newInstance(tokens[1]);
			
			results.put(vertex, value);
		}
		
		return results;
	}
}
