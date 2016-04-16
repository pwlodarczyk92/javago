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
	protected final Stone currentstone;
	protected final int whitepoints;
	protected final int blackpoints;
	protected final int passcounter;
	protected final int hash;

	protected State(TableView<F, G> table) {
		this.table = table;
		this.currentstone = Stone.WHITE;
		this.whitepoints = 0;
		this.blackpoints = 0;
		this.passcounter = 0;
		this.hash = _hash();
	}

	protected State(TableView<F, G> table, Integer whitepoints, Integer blackpoints, Integer passcounter, Stone currentstone) {
		this.table = table;
		this.whitepoints = whitepoints;
		this.blackpoints = blackpoints;
		this.passcounter = passcounter;
		this.currentstone = currentstone;
		this.hash = _hash();
	}

	private State<F,G> _passstate() {
		return new State<>(table, whitepoints, blackpoints, passcounter+1, currentstone.opposite());
	}

	private State<F,G> _movestate(TableView<F, G> newtable, int points) {
		switch (currentstone) {
			case WHITE: return new State<>(newtable, whitepoints + points, blackpoints, 0, currentstone.opposite());
			case BLACK: return new State<>(newtable, whitepoints, blackpoints + points, 0, currentstone.opposite());
			default: throw new RuntimeException();
		}
	}

	private int _hash() {
		int result = table.hashCode();
		result = 31 * result + currentstone.hashCode();
		result = 31 * result + whitepoints;
		result = 31 * result + blackpoints;
		result = 31 * result + passcounter;
		return result;
	}

	// --accessors--
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
	public TableView<F, G> gettable() {
		return table;
	}
	//--accessors--

	//--pseudomodifier--
	@Override
	public Map.Entry<Set<F>, State<F, G>> put(F field) {

		if (passcounter > 1) return null;
		if (field == null) return new AbstractMap.SimpleEntry<>(Collections.emptySet(), _passstate());
		else {

			ITable<F, G> newtable = table.fork();
			Set<F> points = newtable.put(currentstone, field);

			if (points == null) return null;
			return new AbstractMap.SimpleEntry<>(points, _movestate(newtable, points.size()));
		}

	}
	//--pseudomodifier--


	@Override
	public State<F, G> fork() {
		return new State<>(table.fork(), whitepoints, blackpoints, passcounter, currentstone);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		State that = (State) o;

		if (currentstone != that.currentstone) return false;
		if (blackpoints != that.blackpoints) return false;
		if (passcounter != that.passcounter) return false;
		if (whitepoints != that.whitepoints) return false;
		if (!table.equals(that.table)) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return hash;
	}

}
