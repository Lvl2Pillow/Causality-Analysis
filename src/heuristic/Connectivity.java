package heuristic;

import java.util.*;

import core.TermList;
import core.Variable;
import core.Term;

/**
 * <h4>Connectivity</h4>
 * <p>
 * Connectivity describes the minimum number of elements (nodes or edges) that 
 * need to be removed to disconnect a subgraph from the remaining graph.
 * </p>
 * <p>
 * Every variable from the data set represents a node in a graph. Every pair of 
 * variables in a minterm forms an edge between the corresponding nodes.
 * </br>
 * The connectivity of a variable is the number of distinct edges between the 
 * variable and any variable from a specified term. The <i>overall connectivity</i>
 * of a variable is the connectivity from the onset subtract the connectivity
 * from the offset.
 * </p>
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Connectivity_(graph_theory)">Connectivity</a>
 * @author lvl2pillow
 *
 */
public class Connectivity implements HeuristicStrategy<Variable, Integer> {
	private Map<Variable, Integer> literalConnectivity;
	
	public Connectivity(Set<Variable> variables, Term term, 
			TermList onset, TermList offset) {
		this.literalConnectivity = connectivity(variables, term, onset, offset);
		
	}
	
	private Map<Variable, Integer> connectivity(Set<Variable> variables, 
			Term term, TermList onset, TermList offset) {
		Map<Variable, Integer> onsetLiteralConnectivity = 
				connectivity(variables, term, onset);
		Map<Variable, Integer> offsetLiteralConnectivity = 
				connectivity(variables, term, offset);
		for (Variable variable : onsetLiteralConnectivity.keySet()) {
			// onset-offset
			onsetLiteralConnectivity.put(variable, 
					onsetLiteralConnectivity.get(variable)-
					offsetLiteralConnectivity.get(variable));
		}
		return onsetLiteralConnectivity;
	}
	
	/**
	 * 
	 * @param variables set of all variables.
	 * @param term current term.
	 * @param dataSet
	 * @return mapping of variables to connectivity with the current term.
	 */
	private Map<Variable, Integer> connectivity(Set<Variable> variables, 
			Term term, TermList dataSet) {
		// initialize
		Map<Variable, Integer> literalConnectivity = new HashMap<Variable, Integer>();
		for (Variable variable : variables)
			literalConnectivity.put(variable, 0);
		// adjacency matrix
		int[][] adjacencyMatrix = adjacencyMatrix(dataSet);
		// count edges
		for (int i = 0; i < adjacencyMatrix.length; ++i) {
			for (int j = 0; j < adjacencyMatrix.length; ++j) {
				Variable I = new Variable(i);
				Variable J = new Variable(j);
				if (term.contains(I) && term.contains(J));
						// do nothing
				else if (term.contains(I))
					literalConnectivity.put(J, 
							literalConnectivity.get(J)+adjacencyMatrix[i][j]);
				else if (term.contains(J))
					literalConnectivity.put(I, 
							literalConnectivity.get(I)+adjacencyMatrix[i][j]);
			}
		}
		return literalConnectivity;
	}
	
	/**
	 * <p>
	 * Returns an adjacency matrix representing a data set. Every variable from
	 * the data set represents a node in the graph. Every pair of variables in 
	 * a minterm forms an edge between the corresponding nodes.
	 * </p>
	 * <p>
	 * The values in adjacency matrix represents the edge weight of the 
	 * corresponding edge between the two nodes. 0 value represents no edge.
	 * </p>
	 * <p>
	 * <b>NOTE:</b> Only half of the matrix is filled (undirected graph).
	 * </p>
	 * 
	 * @param dataSet
	 * @return
	 */
	public static int[][] adjacencyMatrix(TermList dataSet) {
		int nVariables = dataSet.get(0).size();
		int[][] adjacencyMatrix = new int[nVariables][nVariables];
		for (Term minterm : dataSet) {
			for (int i = 0; i < nVariables; ++i) {
				for (int j = i+1; j < nVariables; ++j) {
					// pair of variables in minterm
					if (minterm.get(i).isNormal() && minterm.get(j).isNormal())
						// increment corresponding edge weight
						++adjacencyMatrix[i][j];
				}
			}
		}
		return adjacencyMatrix;
	}

	@Override
	public Map<Variable, Integer> getHeuristic() {
		return this.literalConnectivity;
	}
	
}
