package org.basex.query.iter;

import org.basex.query.QueryException;
import org.basex.query.item.FTNodeItem;

/**
 * Node iterator interface.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Sebastian Gath
 */
public abstract class FTNodeIter extends Iter {
  @Override
  public abstract FTNodeItem next() throws QueryException;
}
