package core.board;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import core.primitives.MoveNotAllowed;
import core.primitives.Stone;
import core.table.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by maxus on 24.02.16.
 */
public class Board<F, G> implements Game<F, G> {

	protected static class BoardState<F, G> implements StateView<F, G> {

		private ITable<F, G> table;
		private Stone currentstone;
		private Integer whitepoints;
		private Integer blackpoints;
		private Integer passcounter;

		public BoardState(ITable<F, G> table) {
			this.table = table;
			this.currentstone = Stone.WHITE;
			this.whitepoints = 0;
			this.blackpoints = 0;
			this.passcounter = 0;
		}

		public BoardState(ITable<F, G> table, Integer whitepoints, Integer blackpoints, Integer passcounter, Stone currentstone) {
			this.table = table;
			this.whitepoints = whitepoints;
			this.blackpoints = blackpoints;
			this.passcounter = passcounter;
			this.currentstone = currentstone;
		}

		public BoardState<F,G> nullstate() {
			return new BoardState<>(table, whitepoints, blackpoints, passcounter+1, currentstone.opposite());
		}

		public BoardState<F,G> movestate(ITable<F, G> newtable, Set<F> points) {
			switch (currentstone) {
				case WHITE: return new BoardState<>(newtable, whitepoints+points.size(), blackpoints, 0, currentstone.opposite());
				case BLACK: return new BoardState<>(newtable, whitepoints, blackpoints+points.size(), 0, currentstone.opposite());
				default: throw new RuntimeException();
			}
		}

		@Override
		public Integer getpasscount() {
			return passcounter;
		}

		@Override
		public Integer getwhitepoints() {
			return whitepoints;
		}

		@Override
		public Integer getblackpoints() {
			return blackpoints;
		}

		@Override
		public Stone getcurrentstone() {
			return currentstone;
		}

		@Override
		public ITable<F, G> gettable() {
			return table;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			BoardState that = (BoardState) o;

			if (!table.equals(that.table)) return false;
			if (currentstone != that.currentstone) return false;
			if (!blackpoints.equals(that.blackpoints)) return false;
			if (!passcounter.equals(that.passcounter)) return false;
			if (!whitepoints.equals(that.whitepoints)) return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = table.hashCode();
			result = 31 * result + currentstone.hashCode();
			result = 31 * result + whitepoints.hashCode();
			result = 31 * result + blackpoints.hashCode();
			result = 31 * result + passcounter.hashCode();
			return result;
		}

	}

	final protected Logger logger = LoggerFactory.getLogger(Board.class.getName());

	protected BoardState<F, G> currentstate;
	private LinkedList<BoardState<F, G>> history = new LinkedList<>();
	private LinkedList<F> moves = new LinkedList<>();
	private HashSet<ITable<F, G>> snaps = new HashSet<>();
	private Table<BoardState<F, G>, F, Map.Entry<Set<F>, BoardState<F, G>>> cache = HashBasedTable.create();

	public Board(ITable<F, G> table) {
		this.currentstate = new BoardState<>(table);
		this.history.addLast(this.currentstate);
	}

	private Map.Entry<Set<F>, BoardState<F, G>> getstate(BoardState<F, G> state, F field) {
		Map.Entry<Set<F>, BoardState<F, G>> result = cache.get(state, field);
		if (result == null) {
			ITable<F, G> newtable = state.table.copy();
			Set<F> points = newtable.put(currentstate.currentstone, field);
			result = new AbstractMap.SimpleEntry<>(points, state.movestate(newtable, points));
			//cache.put(state, field, result);
		}
		return result;
	}

	//--game state modifiers--
	@Override
	public Set<F> put(F field) {

		if (currentstate.passcounter > 2)
			throw new MoveNotAllowed();

		BoardState<F, G> newstate;
		Set<F> points;

		if (field == null) {

			points = null;
			newstate = currentstate.nullstate();

		} else {

			Map.Entry<Set<F>, BoardState<F, G>> result = getstate(currentstate, field);
			newstate = result.getValue();
			ITable<F, G> newtable = newstate.gettable();
			points = result.getKey();

			if (snaps.contains(newtable))
				throw new MoveNotAllowed(); //positional super-ko rule
			snaps.add(newtable);

			newstate = currentstate.movestate(newtable, points);

		}

		this.moves.addLast(field);
		this.history.addLast(newstate);
		this.currentstate = newstate;
		return points;

	}
	@Override
	public F undo() {

		if (moves.isEmpty()) throw new RuntimeException();

		F lastmove = moves.removeLast();
		BoardState<F, G> laststate = history.removeLast();
		this.currentstate = history.peekLast();

		if (lastmove != null)
			snaps.remove(laststate.table);

		return lastmove;

	}
	//--game state modifiers--

	//--accessors--
	@Override
	public List<F> moves() {
		return Collections.unmodifiableList(moves);
	}
	@Override
	public StateView<F, G> getview() {
		return currentstate;
	}
	//--accessors--
}
