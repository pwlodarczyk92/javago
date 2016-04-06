package core.board;

import core.table.Table;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by maxus on 03.04.16.
 */
public class LogBoard<F, G> extends Board<F, G> {

	private final Function<StateView<F, G>, String> statelogger;
	private final Supplier<Boolean> islogging;

	public LogBoard(Table<F, G> table, Function<StateView<F, G>, String> statelogger, Supplier<Boolean> islogging) {
		super(table);
		this.statelogger = statelogger;
		this.islogging = islogging;
	}

	@Override
	public Set<F> put(F field) {
		Set<F> result = super.put(field);
		if (this.islogging.get() && result != null)
			logger.warn(statelogger.apply(currentstate));
		return result;
	}

	@Override
	public F undo() {
		F result = super.undo();
		if (this.islogging.get())
			logger.warn(statelogger.apply(currentstate));
		return result;
	}


}
