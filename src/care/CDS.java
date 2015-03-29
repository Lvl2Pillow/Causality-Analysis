package care;

import java.util.Map;
import java.util.Map.Entry;

import core.*;

public class CDS extends core.CDS {
	private Map<Literal, Double> socialHeuristic;
	private final double interactivity;
//	private final double similarity;
	
	public CDS(MintermList onset, MintermList offset, Map<Literal, Double> socialHeuristic, 
			double interactivity) {
		super(onset, offset);
		this.socialHeuristic = socialHeuristic;
		// check that 0<=interactivity<=1
		if (!(interactivity >= 0 || interactivity <= 1))
			throw new IllegalArgumentException();
		this.interactivity = interactivity;
	}
	
	/**
	 * CARE integrates the social heuristic with the literal frequency heuristic.
	 * 
	 */
	@Override
	protected LiteralSet getLiteralCandidates(Term term, MintermList currentOnset) {
		// literals with highest overall score
		LiteralSet literalCandidates = new LiteralSet();
		double maxOverallScore = 0.0;		
		Map<Literal, Double> literalFrequencyHeuristic = 
				getLiteralFrequencyHeuristic(term, currentOnset);
		for (Entry<Literal, Double> entry : literalFrequencyHeuristic.entrySet()) {
			Literal literal = entry.getKey();
			double literalFrequency = entry.getValue();
			double overallScore = interactivity*socialHeuristic.get(literal)+
					(1-interactivity)*literalFrequency;
			if (overallScore > maxOverallScore) {
				maxOverallScore = overallScore;
				literalCandidates.clear();
				literalCandidates.add(literal);
			} else if (overallScore == maxOverallScore) {
				literalCandidates.add(literal);
			}	
		}
		return literalCandidates;
	}
	
}
