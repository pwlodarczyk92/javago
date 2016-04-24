package algos.tree;

import core.board.IGame;
import core.board.IState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
* Created by maxus on 22.04.16.
*/
class NodeGame<F, G> implements IGame<F, G> {

	private final TreeGame<F, G> tree;
	private final IGame<F, G> rootgame;
	private Node<F, IState<F, G>> currentNode;

	NodeGame(TreeGame<F, G> tree, IGame<F, G> rootgame, Node<F, IState<F, G>> node) {
		this.tree = tree;
		this.rootgame = rootgame;
		this.currentNode = node;
	}

	@Override
	public Set<F> put(F field) {
		this.currentNode = tree.makeMove(currentNode, field);
		return currentNode.lastResult;
	}

	@Override
	public F undo() {
		if (this.currentNode.predecessor == null) throw new RuntimeException();
		F result = currentNode.lastMove;
		this.currentNode = this.currentNode.predecessor;
		return result;
	}

	@Override
	public IState<F, G> getState() {
		return currentNode.state;
	}

	@Override
	public List<F> getMoves() {
		ArrayList<F> moves = new ArrayList<>(rootgame.getMoves());
		ArrayList<F> treeMoves = new ArrayList<>(6);
		Node<F, IState<F, G>> node = currentNode;
		while (node != tree.root) {
			treeMoves.add(node.lastMove);
			node = node.predecessor;
		}
		Collections.reverse(treeMoves);
		moves.addAll(treeMoves);
		return moves;
	}

	@Override
	public boolean isKoViolatedBy(IState<F, G> state) {
		if (rootgame.isKoViolatedBy(state))
			return true;

		Node<F, IState<F, G>> node = currentNode;
		while (node != tree.root) {
			if (node.state.hashCode() == state.hashCode() && node.state.equals(state))
				return true;
			node = node.predecessor;
		}
		return false;
	}

	@Override
	public IGame<F, G> fork() {
		return new NodeGame<>(this.tree, this.rootgame, this.currentNode);
	}
}
