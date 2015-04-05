package heuristic;

import java.util.*;

import core.Variable;
import core.TermList;

/**
 * <h4>Degree Centrality</h4>
 * <p>
 * In graph theory, the degree centrality of a node is the number of edges
 * connected to said node.
 * </p>
 * <p>
 * Every variable from the data set represents a node in a graph. Every pair of 
 * variables in a minterm forms an edge between the corresponding nodes.
 * </br>
 * The <i>overall degree centrality</i> of a variable is the degree centrality 
 * of a variable from the onset subtract the degree centrality of the same 
 * variable from the offset.
 * 
 * @author lvl2pillow
 *
 */
public class DegreeCentrality implements HeuristicStrategy<Variable, Integer> {
	private static Map<Variable, Integer> degreeCentrality;
	
	public DegreeCentrality(Set<Variable> variables, TermList onset, TermList offset) {
		if (degreeCentrality == null) {
			degreeCentrality = degreeCentrality(variables, onset, offset);
		}
	}

	@SuppressWarnings("static-access")
	@Override
	public Map<Variable, Integer> getHeuristic() { return this.degreeCentrality; }
	
	/**
	 * 
	 * @param onset
	 * @param offset
	 * @return mapping of every literal with its degree centrality.
	 */
	private static Map<Variable, Integer> degreeCentrality(Set<Variable> variables, 
			TermList onset, TermList offset) {
		Map<Variable, Integer> degreeCentrality = new HashMap<Variable, Integer>();
		for (Variable variable : variables)
			degreeCentrality.put(variable, 0);
		int[] onsetDegreeCentrality = degreeCentrality(variables, onset);
		int[] offsetDegreeCentrality = degreeCentrality(variables, offset);
		for (int i = 0; i < variables.size(); ++i) {
			degreeCentrality.put(new Variable(i), 
					onsetDegreeCentrality[i]-offsetDegreeCentrality[i]);
		}
		return degreeCentrality;
	}
	
	/**
	 * 
	 * @param dataSet
	 * @return the degree centrality of every variable in a data set.
	 */
	private static int[] degreeCentrality(Set<Variable> variables, TermList dataSet) {
		int nVariables = variables.size();
		int[] degreeCentrality = new int[nVariables];
		// re-using method
		int[][] adjacencyMatrix = Connectivity.adjacencyMatrix(dataSet);
		for (int i = 0; i < nVariables; ++i) {
			for (int j = 0; j < nVariables; ++j) {
				if (adjacencyMatrix[i][j] == 1) {
					++degreeCentrality[i];
					++degreeCentrality[j];
				}
			}
		}
		return degreeCentrality;
	}
	
}
