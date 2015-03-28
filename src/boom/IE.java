package boom;

/*
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
*/

import core.Literal;
import core.LiteralSet;
import core.Term;
import core.MintermList;
import core.TermSet;

/**
 * Implicant Expansion for BOOM.
 * 
 * @author lvl2pillow
 *
 */
public class IE extends core.IE {
	public IE(MintermList offset, TermSet implicants) {
		super(offset, implicants);
	}

	/*
	 * This method is more complete and more extendible, however slower.
	 * Regardless, it should behave similar.
	@Override
	protected LiteralSet getLiteralCandidates(Term currentImplicant) {
		LiteralSet literalCandidates = new LiteralSet();
		Map<Literal, Integer> manhattanDistanceHeuristic = 
				getManhattanDistanceHeuristic(currentImplicant);
		// if min manhattan distance > 0, then the literal can be removed without
		// causing an intersection with the offset
		for (Entry<Literal, Integer> entry : manhattanDistanceHeuristic.entrySet()) {
			Literal literal = entry.getKey();
			int minDist = entry.getValue();
			if (minDist > 0)
				literalCandidates.add(literal);
		}
		// no literal candidates to remove
		if (literalCandidates.isEmpty()) return literalCandidates;
		// return single random literal candidate
		LiteralSet randomLiteralCandidate = new LiteralSet();
		randomLiteralCandidate.add(literalCandidates.getRandomLiteral());
		return randomLiteralCandidate;
	}
	*/
	
	@Override
	protected LiteralSet getLiteralCandidates(Term currentImplicant) {
		LiteralSet literalCandidates = new LiteralSet();
		for (Literal literal : currentImplicant) {
			// try remove
			Term newTerm = new Term(currentImplicant);
			newTerm.remove(literal);
			if (super.isPrimeImplicant(newTerm))
				literalCandidates.add(literal);
		}
		// no literal candidates to remove
		if (literalCandidates.isEmpty()) return literalCandidates;
		// return single random literal candidate
		LiteralSet randomLiteralCandidate = new LiteralSet();
		randomLiteralCandidate.add(literalCandidates.getRandomLiteral());
		return randomLiteralCandidate;
	}
}
