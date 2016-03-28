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

	private LinkedList<F> moves = new LinkedList<>();
	private HashSet<Table<F, G, C>> snaps = new HashSet<>();
	private Deque<Table<F, G, C>> history = new ArrayDeque<>();

	private Table<F, G, C> table;
	protected Stone currstone = Stone.WHITE;
	protected Integer passcounter = 0;

	public Board(Table<F, G, C> table) {
		this.table = table;
		this.history.add(table);
	}

	public void put(F field) {

		if (passcounter > 2) {
			throw new MoveNotAllowed();
		}

		if (field == null) {
			moves.add(null);
			currstone = currstone.opposite();
			passcounter += 1;
			return;
		}

		Table<F, G, C> newtable = this.table.copy();

		newtable.put(currstone, field);
		if (snaps.contains(newtable)) {
			throw new MoveNotAllowed(); //positional super-ko rule
		}

		moves.add(field);
		snaps.add(newtable);
		history.add(newtable);
		currstone = currstone.opposite();
		passcounter = 0;
		this.table = newtable;

	}

	public F undoput() {

		F lastmove = moves.removeLast();
		currstone = currstone.opposite();

		if (lastmove != null) {
			Table<F, G, C> lasttable = history.removeLast();
			snaps.remove(lasttable);
			table = history.peekLast();
		}

		passcounter = 0;
		Iterator<F> passes = moves.descendingIterator();
		while (passes.hasNext()) {
			F move = passes.next();
			if (move != null) break;
			passcounter += 1;
		}

		return lastmove;

	}

	public Table<F, G, C> getTable() {
		return table;
	}

	public Stone getStone(F field) {
		return table.getStone(field);
	}

}
