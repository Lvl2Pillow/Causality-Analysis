package boom;

import java.util.Map;
import java.util.Map.Entry;

import core.Literal;
import core.LiteralSet;
import core.Term;
import core.TermList;
import core.TermSet;

/**
 * Coverage-Directed Search for BOOM.
 * 
 * @author lvl2pillow
 *
 */
public class CDS extends core.CDS {
	private final int nIterations;		// number of iterations to run CDS
	
	public CDS(TermList onset, TermList offset, int nIterations) {
		super(onset, offset);
		this.nIterations = nIterations;
	}
	
	@Override
	public TermSet run() {
		TermSet allImplicants = new TermSet();
		for (int i = 0; i < nIterations; ++i)
			allImplicants.addAll(super.run());
		return allImplicants;
	}
	
	/**
	 * BOOM only uses the literal frequency to select literal candidates. A random
	 * choice is made if there are multiple candidates.
	 * 
	 */
	@Override
	protected LiteralSet getLiteralCandidates(Term term, TermList currentOnset) {
		LiteralSet literalCandidates = new LiteralSet();
		double maxLiteralFrequency = 0.0;		
		Map<Literal, Double> literalFrequencyHeuristic = 
				getLiteralFrequencyHeuristic(term, currentOnset);
		for (Entry<Literal, Double> entry : literalFrequencyHeuristic.entrySet()) {
			Literal literal = entry.getKey();
			double literalFrequency = entry.getValue();
			if (literalFrequency > maxLiteralFrequency) {
				maxLiteralFrequency = literalFrequency;
				literalCandidates.clear();
				literalCandidates.add(literal);
			} else if (literalFrequency == maxLiteralFrequency) {
				literalCandidates.add(literal);
			}	
		}
		// no literal candidates to add
		if (literalCandidates.isEmpty()) return literalCandidates;
		// return single random literal candidate
		LiteralSet randomLiteralCandidate = new LiteralSet();
		randomLiteralCandidate.add(literalCandidates.getRandomLiteral());
		return randomLiteralCandidate;
	}

}
