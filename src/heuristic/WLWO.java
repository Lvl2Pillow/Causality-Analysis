package heuristic;

import java.util.HashMap;
import java.util.Map;

import core.Literal;
import core.Term;
import core.TermList;

/**
 * <b>Literal Weights and Output Weights heuristic (Kagliwal & Balachandran, 2012).</b>
 * <ul>
 * <li>Weights of Literals (<i>LW<sub>x</sub></i>): for each literal <i>x</i>, 
 * 	the weight <i>LW<sub>x</sub></i> is the count of the implicants which 
 * 	contains the literal <i>x</i>.</li>
 * <li>Weights of Outputs (<i>IC<sub>y</sub></i>): for each output 
 * 	<i>y<sub>j</sub></i>, the weight <i>IC<sub>j</sub></i> is the number 
 * 	of implicants in the onset of <i>y<sub>j</sub></i>, 
 * 	i.e. the cardinality of the onset.</li>
 * <li>Weighted Literal Count (<i>WL<sub>i</sub></i>): 
 * 	<i>&Sigma;(LW<sub>x</sub>), x &isin; i</i>.
 * <li>Weighted Output Count (<i>WO<sub>i</sub></i>):  
 * 	<i>&Sigma;(IC<sub>y</sub>), i &isin; y</i>.</li>
 * <li>Literal Weights and Output Weights (<i>WLWO<sub>i</sub></i>): 
 * 	<i>WL<sub>i</sub>*WO<sub>i</sub></i></li>
 * </ul>
 * NOTE: In our study, we only use a single output, that is the onset. As such, 
 * the <i>WO<sub>i</sub></i> part can be disregarded, and we only use the 
 * <i>WL<sub>i</sub></i>.
 * 
 * @author lvl2pillow
 *
 */
public class WLWO implements HeuristicStrategy<Term, Integer> {
	private static Map<Term, Integer> termWLWO;
	
	@SuppressWarnings("static-access")
	public WLWO(TermList primeImplicants) {
		this.termWLWO = weightedLiteralWeightedOutput(primeImplicants);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public Map<Term, Integer> getHeuristic() {
		return this.termWLWO;
	}
	
	/**
	 * Returns the Literal Weights and Output Weights (<i>WLWO</i>) of every
	 * prime implicant.
	 * </br>
	 * NOTE: This implementation only uses the Weighted Literal Count (<i>WL</i>).
	 * 
	 * @param primeImplicants
	 * @return literal weights and output weights <i>WLWO</i>.
	 */
	private static Map<Term, Integer> weightedLiteralWeightedOutput(TermList primeImplicants) {
		Map<Term, Integer> weightedLiteralWeightedOutput = new HashMap<Term, Integer>();
		// weights of literals
		Map<Literal, Integer> literalWeights = getLiteralWeights(primeImplicants);
		for (Term primeImplicant : primeImplicants) {
			if (!weightedLiteralWeightedOutput.containsKey(primeImplicant))	// new prime implicant
				weightedLiteralWeightedOutput.put(primeImplicant, 0);
			for (Literal literal : primeImplicant) {
				// update weighted literal count
				weightedLiteralWeightedOutput.replace(primeImplicant, 
						weightedLiteralWeightedOutput.get(primeImplicant)+
						literalWeights.get(literal));
			}
		}
		return weightedLiteralWeightedOutput;
	}
	
	/**
	 * 
	 * @param primeImplicants
	 * @return weights of literals <i>LW</i>.
	 */
	private static Map<Literal, Integer> getLiteralWeights(TermList primeImplicants) {
		Map<Literal, Integer> literalWeights = new HashMap<Literal, Integer>();
		for (Term primeImplicant : primeImplicants) {
			for (Literal literal : primeImplicant) {
				if (!literalWeights.containsKey(literal))					// new literal
					literalWeights.put(literal, 0);
				// increment literal count
				literalWeights.replace(literal, literalWeights.get(literal)+1);
			}
		}
		return literalWeights;
	}
	
}
