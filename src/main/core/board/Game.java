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

	protected State<F, G> currentState;
	protected final LinkedList<State<F, G>> states;
	protected final LinkedList<F> moves;
	protected final HashSet<TableView<F, G>> tables;

	public Game(TableView<F, G> table) {
		this.currentState = new State<>(table);
		this.states = new LinkedList<>();
		this.moves = new LinkedList<>();
		this.tables = new HashSet<>();
		this.states.addLast(this.currentState);
	}

	protected Game(State<F, G> currentState, LinkedList<State<F, G>> states, LinkedList<F> moves, HashSet<TableView<F, G>> tables) {
		this.currentState = currentState;
		this.states = new LinkedList<>(states);
		this.moves = new LinkedList<>(moves);
		this.tables = new HashSet<>(tables);
	}

	//--accessors--
	@Override
	public boolean isKoViolatedBy(IState<F, G> state) {
		return tables.contains(state.getTable());
	}
	@Override
	public List<F> getMoves() {
		return Collections.unmodifiableList(moves);
	}
	@Override
	public State<F, G> getState() {
		return currentState;
	}
	//--accessors--


	//--modifiers--
	@Override
	public Set<F> put(F field) {

		Map.Entry<Set<F>, State<F, G>> newData = currentState.put(field);
		if (newData == null) return null;

		State<F, G> newState = newData.getValue();
		Set<F> points = newData.getKey();

		if (field != null && isKoViolatedBy(newState)) return null; //positional super-ko rule

		if (field != null) this.tables.add(newState.getTable());
		this.moves.addLast(field);
		this.states.addLast(newState);
		this.currentState = newState;

		return points;

	}

	@Override
	public F undo() {

		if (moves.isEmpty()) throw new MoveNotAllowed();

		F lastmove = this.moves.removeLast();
		State<F, G> laststate = this.states.removeLast();
		this.currentState = this.states.peekLast();
		if (lastmove != null) this.tables.remove(laststate.table);

		return lastmove;

	}
	//--modifiers--

	@Override
	public Game<F, G> fork() {
		return new Game<>(this.currentState, this.states, this.moves, this.tables);
	}
}
