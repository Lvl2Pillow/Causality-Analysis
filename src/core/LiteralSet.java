package core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * A {@link HashSet} of {@code Literal} elements.
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
	 * 
	 * @return
	 */
	public Set<Literal> removeComplimentIfNormalExists() {
		Set<Literal> normalLiterals = new HashSet<Literal>();
		for (Literal literal : this) {
			if (literal.isNormal())
				normalLiterals.add(literal);
		}
		// remove compliment literal if normal literal exists in the set
		Set<Literal> removedLiterals = new HashSet<Literal>();
		Iterator<Literal> i = this.iterator();
		while (i.hasNext()) {
			Literal literal = i.next();
			if (literal.isCompliment() && 
					normalLiterals.contains(new Literal(literal.getIndex(), true)))
				removedLiterals.add(literal);
				i.remove();
		}
		return removedLiterals;
	}
	
	/**
	 * This {@code add} method prevents adding the same variable to the set. The
	 * compliment form will be discarded in favor of the normal form.
	 * 
	 */
//	@Override
//	public boolean add(Literal literal) {
//		Literal compliment;
//		if (literal.isNormal() && 
//				this.contains(compliment = new Literal(literal.getIndex(), false)))
//			this.remove(compliment);
//		return super.add(literal);
//	}
	
	/**
	 * 
	 * @return a random literal from the set.
	 */
	public Literal randomLiteral() {
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
