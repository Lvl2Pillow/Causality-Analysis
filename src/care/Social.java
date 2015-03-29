package care;

import java.util.HashMap;
import java.util.Map;

import core.Literal;
import core.Term;
import core.TermList;

public class Social {
	private final TermList onset;
	private final TermList offset;
	private final double DENSITYCAP = 0.25;
	
	public Social(TermList onset, TermList offset) {
		this.onset = onset;
		this.offset = offset;
	}
	
	
	public static void main(String[ args]) {
		
		
		
		BetweennessCentrality bc = new BetweennessCentrality(null);
		
		
	}
	
	/**
	 * 
	 * @return mapping of literals and their social score.
	 */
	public Map<Literal, Double> getSocialHeuristic() {
		
		
		
		
		// TODO check if degree centralities and betweenness centralities have equal length
		Map<Literal, Double> degreeCentralities = getDegreeCentralities
		
		
		return null;
	}
	
	
	/**
	 * 
	 * 
	 * @param dataSet
	 * @return
	 */
	private Graph toGraph(TermList dataSet) {
		int[][] adjacencyMatrix = new int[dataSet.size()][dataSet.size()];
		for (Term minterm : dataSet) {
			for (int i = 0; i < minterm.size(); ++i) {
				// only half of the adjacency matrix needs to be filled
				for (int j = i+1; j < minterm.size(); ++j) {
					if (minterm.contains(new Literal()))
				}
			}
		}
	}
	
	/**
	 * Returns the degree centrality of every node in a graph.
	 * 
	 * @param graph
	 * @return mapping of every node with their degree centrality.
	 */
	private Map<Literal, Double> getDegreeCentralities() {
		return new HashMap<Literal, Double>();
	}
	
	/**
	 * Returns the betweenness centrality of every node in a graph.
	 * 
	 * @param graph
	 * @return mapping of every node with their betweenness centrality.
	 */
	private Map<Literal, Double> getBetweennessCentralities() {
		return new HashMap<Literal, Double>();
	}
	
}
