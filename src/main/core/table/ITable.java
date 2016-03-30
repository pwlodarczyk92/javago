package core.table;

import core.primitives.Stone;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;


/**
 * Created by maxus on 28.03.16.
 */
public interface ITable<F, G, V extends TableView<F, G>> extends TableView<F, G> {


	public Set<F> put(Stone stone, F field);
	public V getview();

}
