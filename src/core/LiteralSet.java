package core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

/**
 * A {@link HashSet} of {@link Literal} elements.
 * 
 * @author lvl2pillow
 *
 */
public class LiteralSet extends HashSet<Literal> {
	// auto-generated serialVersionUID
	private static final long serialVersionUID = 2873129103966479273L;
	
	public LiteralSet() {
		super();
	}
	
	public LiteralSet(Collection<Literal> literalSet) {
		super(literalSet);
	}
	
	/**
	 * This {@code add} method prevents adding the same variable to the set. The
	 * compliment form will be discarded in favor of the normal form.
	 * 
	 */
	@Override
	public boolean add(Literal literal) {
		Literal compliment;
		if (literal.isNormal() && this.contains(compliment = new Literal(literal.index(), false)))
			this.remove(compliment);
		return super.add(literal);
	}
	
	/**
	 * 
	 * @return a random literal from the set.
	 */
	public Literal getRandomLiteral() {
		int n = new Random().nextInt(this.size());
		int i = 0;
		for (Literal literal : this) {
			if (i == n) return literal;
			++i;
		}
		// TODO log if code reaches here
		return null;
	}
}
