package core.board;

import core.primitives.MoveNotAllowed;
import core.primitives.Stone;
import core.table.Table;
import utils.Copyable;
import core.color.IColor;

import java.util.*;

/**
 * Created by maxus on 24.02.16.
 */
public class Board<F, G, C extends IColor<F, G> & Copyable<C>> {

	private Deque<F> moves = new ArrayDeque<>();
	private HashSet<Table<F, G, C>> snaps = new HashSet<>();
	private Deque<Table<F, G, C>> history = new ArrayDeque<>();

	private Table<F, G, C> table;
	private Stone currstone = Stone.WHITE;

	public Board(Table<F, G, C> table) {
		this.table = table;
	}

	public void put(F field) {

		Table<F, G, C> newtable = this.table.copy();

		newtable.put(currstone, field);
		if (snaps.contains(newtable)) {
			throw new MoveNotAllowed(); //positional super-ko rule
		}

		moves.add(field);
		snaps.add(newtable);
		history.add(newtable);
		currstone = currstone.opposite();
		this.table = newtable;

	}

	public F undo() {
		currstone = currstone.opposite();
		snaps.remove(table);
		history.removeLast();
		table = history.peekLast();
		return moves.removeLast();
	}

	public Table<F, G, C> getTable() {
		return table;
	}
	public Stone getStone(F field) {
		return table.getStone(field);
	}

}
