package core.board;

import core.Stone;
import core.table.ITable;
import core.table.TableView;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by maxus on 08.04.16.
 */
public class State<F, G> implements IState<F, G> {

	protected final TableView<F, G> table;
	protected final Stone currentStone;
	protected final int whitePoints;
	protected final int blackPoints;
	protected final int passCounter;
	protected final int hash;

	protected State(TableView<F, G> table) {
		this.table = table;
		this.currentStone = Stone.WHITE;
		this.whitePoints = 0;
		this.blackPoints = 0;
		this.passCounter = 0;
		this.hash = _hash();
	}

	protected State(TableView<F, G> table, Integer whitePoints, Integer blackPoints, Integer passCounter, Stone currentStone) {
		this.table = table;
		this.whitePoints = whitePoints;
		this.blackPoints = blackPoints;
		this.passCounter = passCounter;
		this.currentStone = currentStone;
		this.hash = _hash();
	}

	private State<F,G> _makePassState() {
		return new State<>(table, whitePoints, blackPoints, passCounter +1, currentStone.opposite());
	}

	private State<F,G> _makeMoveState(TableView<F, G> newtable, int points) {
		switch (currentStone) {
			case WHITE: return new State<>(newtable, whitePoints + points, blackPoints, 0, currentStone.opposite());
			case BLACK: return new State<>(newtable, whitePoints, blackPoints + points, 0, currentStone.opposite());
			default: throw new RuntimeException();
		}
	}

	private int _hash() {
		int result = table.hashCode();
		result = 31 * result + currentStone.hashCode();
		result = 31 * result + whitePoints;
		result = 31 * result + blackPoints;
		result = 31 * result + passCounter;
		return result;
	}

	// --accessors--
	@Override
	public Integer getPassCount() {
		return passCounter;
	}

	@Override
	public Integer getWhitePoints() {
		return whitePoints;
	}

	@Override
	public Integer getBlackPoints() {
		return blackPoints;
	}

	@Override
	public Stone getCurrentStone() {
		return currentStone;
	}

	@Override
	public TableView<F, G> getTable() {
		return table;
	}
	//--accessors--

	//--pseudomodifier--
	@Override
	public Map.Entry<Set<F>, State<F, G>> put(F field) {

		if (passCounter > 1) return null;
		if (field == null) return new AbstractMap.SimpleEntry<>(Collections.emptySet(), _makePassState());
		else {

			ITable<F, G> newTable = table.fork();
			Set<F> points = newTable.put(currentStone, field);

			if (points == null) return null;
			return new AbstractMap.SimpleEntry<>(points, _makeMoveState(newTable, points.size()));
		}

	}
	//--pseudomodifier--


	@Override
	public State<F, G> fork() {
		return new State<>(table.fork(), whitePoints, blackPoints, passCounter, currentStone);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		State that = (State) o;

		if (currentStone != that.currentStone) return false;
		if (blackPoints != that.blackPoints) return false;
		if (passCounter != that.passCounter) return false;
		if (whitePoints != that.whitePoints) return false;
		if (!table.equals(that.table)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return hash;
	}

}
