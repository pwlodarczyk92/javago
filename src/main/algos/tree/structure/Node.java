package algos.tree.structure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

import static java.lang.Double.isNaN;

/**
 * Created by maxus on 07.04.16.
 */
public class Node<F, S>{

	final static protected Logger logger = LoggerFactory.getLogger(TreeGame.class.getName());

	public final S state;
	public final boolean white;

	public final F lastmove;
	public final Node<F, S> predecessor;
	protected final HashMap<F, Node<F, S>> successors;

	public final double rawscore;
	protected double treescore;

	public double score () {
		if (!isNaN(treescore)) return treescore;
		else return rawscore;
	}

	public Node<F, S> expand(S state, F move, double rawscore) {
		Node<F, S> newnode = new Node<>(state, rawscore, this, move, !white);
		successors.put(move, newnode);
		updateScore();
		return newnode;
	}

	public void setinvalid(F move) {
		successors.put(move, null);
	}

	private void updateScore() {

		double oldscore = treescore;
		if(white) this.treescore = successors.values().stream().filter(i->i!=null).mapToDouble(Node::score).max().getAsDouble();
		else this.treescore = successors.values().stream().filter(i->i!=null).mapToDouble(Node::score).min().getAsDouble();
		if (predecessor != null && (isNaN(oldscore) || oldscore != treescore)) predecessor.updateScore();

	}

	public Node(S state, double rawscore, Node<F, S> predecessor, F move, boolean white) {

		this.state = state;
		this.white = white;

		this.predecessor = predecessor;
		this.lastmove = move;
		this.successors = new HashMap<>();

		this.rawscore = rawscore;
		this.treescore = Double.NaN;
	}


	public Node(S state, double score, boolean white) {
		this(state, score, null, null, white);
	}

}