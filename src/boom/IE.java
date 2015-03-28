package boom;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import core.Literal;
import core.LiteralSet;
import core.Term;
import core.TermList;
import core.TermSet;

/**
 * Implicant Expansion for BOOM.
 * 
 * @author lvl2pillow
 *
 */
public class IE extends core.IE {
	public IE(TermList offset, TermSet implicants) {
		super(offset, implicants);
	}

	@Override
	protected LiteralSet getLiteralCandidates(Term currentImplicant) {
		LiteralSet literalCandidates = new LiteralSet();
		// find the resulting manhattan distance for each literal removal
		Map<Literal, Integer> manhattanDistanceHeuristic = new HashMap<Literal, Integer>();
		for (Literal literal : currentImplicant) {
			// remove the literal
			Term newTerm = new Term(currentImplicant);
			newTerm.remove(literal);
			// calculate min manhattan distance
			manhattanDistanceHeuristic.put(literal, minManhattanDistanceBetween(newTerm));
		}
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
}
