package algos.tree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by maxus on 07.04.16.
 */
public class Node<F, S>{

	final static protected Logger logger = LoggerFactory.getLogger(TreeGame.class.getName());

	public final S state;
	public final F lastMove;
	public final Set<F> lastResult;
	public final Node<F, S> predecessor;
	public final Map<F, Node<F, S>> successors;
	private final HashMap<F, Node<F, S>> _successors;

	Node<F, S> expand(S state, F move, Set<F> result) {
		Node<F, S> newNode = new Node<>(state, this, move, result);
		_successors.put(move, newNode);
		return newNode;
	}

	void setInvalid(F move) {
		_successors.put(move, null);
	}

	Node(S state, Node<F, S> predecessor, F move, Set<F> result) {

		this.state = state;
		this.lastMove = move;
		this.predecessor = predecessor;
		this._successors = new HashMap<>();
		this.successors = Collections.unmodifiableMap(this._successors);
		this.lastResult = result == null ? null : Collections.unmodifiableSet(result);
	}

	Node(S state) {
		this(state, null, null, null);
	}

}