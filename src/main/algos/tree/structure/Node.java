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
	public final boolean isWhite;

	public final F lastMove;
	public final Node<F, S> predecessor;
	protected final HashMap<F, Node<F, S>> successors;

	public final double rawScore;
	protected double treeScore;

	public double getScore() {
		if (!isNaN(treeScore)) return treeScore;
		else return rawScore;
	}

	public Node<F, S> expand(S state, F move, double rawScore) {
		Node<F, S> newNode = new Node<>(state, rawScore, this, move, !isWhite);
		successors.put(move, newNode);
		updateScore();
		return newNode;
	}

	public void setInvalid(F move) {
		successors.put(move, null);
	}

	private void updateScore() {

		double oldscore = treeScore;
		if(isWhite) this.treeScore = successors.values().stream().filter(i->i!=null).mapToDouble(Node::getScore).max().getAsDouble();
		else this.treeScore = successors.values().stream().filter(i->i!=null).mapToDouble(Node::getScore).min().getAsDouble();
		if (predecessor != null && (isNaN(oldscore) || oldscore != treeScore)) predecessor.updateScore();

	}

	public Node(S state, double rawScore, Node<F, S> predecessor, F move, boolean isWhite) {

		this.state = state;
		this.isWhite = isWhite;

		this.predecessor = predecessor;
		this.lastMove = move;
		this.successors = new HashMap<>();

		this.rawScore = rawScore;
		this.treeScore = Double.NaN;
	}


	public Node(S state, double score, boolean isWhite) {
		this(state, score, null, null, isWhite);
	}

}