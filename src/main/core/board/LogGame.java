package core.board;

import core.table.Table;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by maxus on 03.04.16.
 */
public class LogGame<F, G> extends Game<F, G> {

	private final Function<IState<F, G>, String> stateLogger;
	private final Supplier<Boolean> isLogging;

	public LogGame(Table<F, G> table, Function<IState<F, G>, String> stateLogger, Supplier<Boolean> isLogging) {
		super(table);
		this.stateLogger = stateLogger;
		this.isLogging = isLogging;
	}

	protected LogGame(LogGame<F, G> logBoard) {
		super(logBoard.currentState, logBoard.states, logBoard.moves, logBoard.tables);
		this.stateLogger = logBoard.stateLogger;
		this.isLogging = logBoard.isLogging;
	}

	@Override
	public Set<F> put(F field) {
		Set<F> result = super.put(field);
		if (this.isLogging.get() && result != null)
			logger.warn(stateLogger.apply(currentState));
		return result;
	}

	@Override
	public F undo() {
		F result = super.undo();
		if (this.isLogging.get())
			logger.warn(stateLogger.apply(currentState));
		return result;
	}

	@Override
	public LogGame<F, G> fork() {
		return new LogGame<>(this);
	}


}
