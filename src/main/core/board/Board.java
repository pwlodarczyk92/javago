package core.board;

import core.primitives.MoveNotAllowed;
import core.primitives.Stone;
import core.table.Table;
import core.table.TableView;

import java.util.*;

/**
 * Created by maxus on 24.02.16.
 */
public class Board<F, G> {

	private LinkedList<F> moves = new LinkedList<>();
	private HashSet<Table<F, G>> snaps = new HashSet<>();
	private Deque<Table<F, G>> history = new ArrayDeque<>();
	private Deque<Integer> blackpoints = new ArrayDeque<>();
	private Deque<Integer> whitepoints = new ArrayDeque<>();

	private Table<F, G> table;
	protected Stone currstone = Stone.WHITE;
	protected Integer passcounter = 0;

	public Board(Table<F, G> table) {
		this.table = table;
		this.history.addLast(table);
		this.blackpoints.addLast(0);
		this.whitepoints.addLast(0);
	}

	public void put(F field) {

		if (passcounter > 2) {
			throw new MoveNotAllowed();
		}

		if (field == null) {
			moves.addLast(null);
			currstone = currstone.opposite();
			passcounter += 1;
			return;
		}

		Table<F, G> newtable = this.table.copy();

		int points = newtable.put(currstone, field).size();
		if (snaps.contains(newtable)) {
			throw new MoveNotAllowed(); //positional super-ko rule
		}

		moves.addLast(field);
		snaps.add(newtable);
		history.addLast(newtable);
		switch (currstone) {
			case WHITE: whitepoints.addLast(whitepoints.peekLast()+points); break;
			case BLACK: blackpoints.addLast(blackpoints.peekLast()+points); break;
			default: throw new RuntimeException();
		}
		currstone = currstone.opposite();
		passcounter = 0;
		this.table = newtable;

	}

	public F undo() {

		F lastmove = moves.removeLast();
		currstone = currstone.opposite();

		if (lastmove != null) {
			Table<F, G> lasttable = history.removeLast();
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

	public Stone currentstone() {
		return currstone;
	}
	public Integer passcount() {
		return passcounter;
	}
	public List<F> moves() {
		return Collections.unmodifiableList(moves);
	}

	public TableView<F, G> tableview() {
		return table.getview();
	}
	public Table<F, G> tablecopy() { return table.copy(); }
}
