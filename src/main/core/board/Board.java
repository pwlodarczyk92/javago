package core.board;

import core.primitives.MoveNotAllowed;
import core.primitives.Stone;
import core.table.ITable;
import core.table.TableView;
import utils.Copyable;

import java.util.*;

/**
 * Created by maxus on 24.02.16.
 */
public class Board<F, G, T extends ITable<F, G, V> & Copyable<T>, V extends TableView<F, G>> {

	private LinkedList<F> moves = new LinkedList<>();
	private HashSet<T> snaps = new HashSet<>();
	private Deque<T> history = new ArrayDeque<>();
	private Deque<Integer> blackpoints = new ArrayDeque<>();
	private Deque<Integer> whitepoints = new ArrayDeque<>();

	private T table;
	protected Stone currstone = Stone.WHITE;
	protected Integer passcounter = 0;

	public Board(T table) {
		this.table = table;
		this.history.add(table);
		this.blackpoints.add(0);
		this.whitepoints.add(0);
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

		int points = newtable.put(currstone, field).size();
		if (snaps.contains(newtable)) {
			throw new MoveNotAllowed(); //positional super-ko rule
		}

		moves.add(field);
		snaps.add(newtable);
		history.add(newtable);
		switch (currstone) {
			case WHITE: whitepoints.add(whitepoints.peek()+points); break;
			case BLACK: blackpoints.add(blackpoints.peek()+points); break;
			default: throw new RuntimeException();
		}
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
			switch (currstone) {
				case WHITE: whitepoints.removeLast(); break;
				case BLACK: blackpoints.removeLast(); break;
				default: throw new RuntimeException();
			}
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

	public int points(Stone stone) {
		switch (stone) {
			case WHITE: return whitepoints.peekLast();
			case BLACK: return blackpoints.peekLast();
			default: throw new RuntimeException();
		}
	}

	@SuppressWarnings("unchecked")
	public List<F> moves() {
		return Collections.unmodifiableList(moves);
	}

	public V tableview() {
		return table.getview();
	}
	public T tablecopy() { return table.copy(); }
}
