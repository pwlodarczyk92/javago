package core.table;

import core.Stone;

import java.util.Set;


/**
 * Created by maxus on 28.03.16.
 */
public interface ITable<F, G> extends TableView<F, G> {

	public Set<F> put(Stone stone, F field);

}
