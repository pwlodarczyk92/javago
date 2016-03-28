package core.board;

import core.primitives.MoveNotAllowed;
import core.primitives.Stone;
import core.table.ITable;
import core.table.Table;
import core.table.TableView;
import utils.Copyable;
import core.color.IColor;

import java.util.*;

/**
 * Created by maxus on 24.02.16.
 */
public class Board<F, G, T extends ITable<F, G, V> & Copyable<T>, V extends TableView<F, G>> {

	private LinkedList<F> moves = new LinkedList<>();
	private HashSet<T> snaps = new HashSet<>();
	private Deque<T> history = new ArrayDeque<>();

	private T table;
	protected Stone currstone = Stone.WHITE;
	protected Integer passcounter = 0;

	public Board(T table) {
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

		T newtable = this.table.copy();

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
			T lasttable = history.removeLast();
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

	public V tableview() {
		return table.getview();
	}

}
