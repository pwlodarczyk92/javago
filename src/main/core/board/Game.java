package core.board;

import core.MoveNotAllowed;
import core.table.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by maxus on 24.02.16.
 */
public class Game<F, G> implements IGame<F, G> {

	final protected Logger logger = LoggerFactory.getLogger(Game.class.getName());

	protected State<F, G> currentstate;
	protected final LinkedList<State<F, G>> states;
	protected final LinkedList<F> moves;
	protected final HashSet<TableView<F, G>> tables;

	public Game(TableView<F, G> table) {
		this.currentstate = new State<>(table);
		this.states = new LinkedList<>();
		this.moves = new LinkedList<>();
		this.tables = new HashSet<>();
		this.states.addLast(this.currentstate);
	}

	protected Game(State<F, G> currentstate, LinkedList<State<F, G>> states, LinkedList<F> moves, HashSet<TableView<F, G>> tables) {
		this.currentstate = currentstate;
		this.states = new LinkedList<>(states);
		this.moves = new LinkedList<>(moves);
		this.tables = new HashSet<>(tables);
	}

	//--accessors--
	@Override
	public boolean superkoviolation(IState<F, G> state) {
		return tables.contains(state.gettable());
	}
	@Override
	public List<F> moves() {
		return Collections.unmodifiableList(moves);
	}
	@Override
	public State<F, G> getstate() {
		return currentstate;
	}
	//--accessors--


	//--modifiers--
	@Override
	public Set<F> put(F field) {

		Map.Entry<Set<F>, State<F, G>> newdata = currentstate.put(field);
		if (newdata == null) return null;

		State<F, G> newstate = newdata.getValue();
		Set<F> points = newdata.getKey();

		if (field != null && superkoviolation(newstate)) return null; //positional super-ko rule

		if (field != null) this.tables.add(newstate.gettable());
		this.moves.addLast(field);
		this.states.addLast(newstate);
		this.currentstate = newstate;

		return points;

	}

	@Override
	public F undo() {

		if (moves.isEmpty()) throw new MoveNotAllowed();

		F lastmove = this.moves.removeLast();
		State<F, G> laststate = this.states.removeLast();
		this.currentstate = this.states.peekLast();
		if (lastmove != null) this.tables.remove(laststate.table);

		return lastmove;

	}
	//--modifiers--

	@Override
	public Game<F, G> fork() {
		return new Game<>(this.currentstate, this.states, this.moves, this.tables);
	}
}
