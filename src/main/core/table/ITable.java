package core.table;

import core.primitives.Stone;
import utils.Copyable;

import java.util.Set;


/**
 * Created by maxus on 28.03.16.
 */
public interface ITable<F, G> extends TableView<F, G>, Copyable<ITable<F, G>> {

	public Set<F> put(Stone stone, F field);
	public ITable<F, G> copy();
	public TableView<F, G> getview();

}
