package org.openntf.domino.graph2.impl;

import java.util.Collection;

import javolution.lang.Immutable;
import javolution.util.FastTable;
import javolution.util.service.TableService;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
// FIXME: you should NOT access internal classes, as these classes are not exported
//import javolution.util.internal.table.AtomicTableImpl;
//import javolution.util.internal.table.UnmodifiableTableImpl;

// Comments from RPr to NTF:
// Inheriting from "FastTable" may be the wrong approach (don't know)
// 1) If you do it, you must override all DSL-specific methods (.atomic, mapped, .reversed, .shared, .subList, .unmodifiable)
// 2) accessing *.internal.* packages is a problem as these packages are not exported.
// So, if there is a way to avoid this, it should be done. 
// As a last resort, the negation of "*.internal.*" export in javolution.cores should be changed.
public class DEdgeList extends FastTable<Edge> {
	private static final long serialVersionUID = 1L;
	protected final DVertex sourceVertex_;

	public DEdgeList(final DVertex source) {
		sourceVertex_ = source;
	}

	public DEdgeList(final DVertex source, final TableService<Edge> service) {
		super(service);
		sourceVertex_ = source;
	}

	@Override
	public DEdgeList atomic() {
		System.err.println("FIXME: DEdgeList.atomic() simply returns 'this'. Read comments in DEdgeList.java");
		return this;
		//return new DEdgeList(sourceVertex_,  new AtomicTableImpl<Edge>(service()));
	}

	@Override
	public DEdgeList unmodifiable() {
		System.err.println("FIXME: DEdgeList.atomic() simply returns 'this'. Read comments in DEdgeList.java");
		return this;
		//return new DEdgeList(sourceVertex_, new UnmodifiableTableImpl<Edge>(service()));
	}

	@Override
	public <T extends Collection<Edge>> Immutable<T> immutable() {
		return super.immutable();
	}

	public Edge findEdge(final Vertex toVertex) {
		Edge result = null;
		Object toId = toVertex.getId();
		Object fromId = sourceVertex_.getId();
		if (this.size() > 0) {
			for (Edge edge : this) {
				if (edge instanceof DEdge) {
					DEdge dedge = (DEdge) edge;
					if (toId.equals(dedge.getOtherVertexId(sourceVertex_))) {
						result = dedge;
						break;
					}
				} else {
					//					System.out.println("DEBUG: Found an Edge that's not a DEdge. It's a " + edge.getClass().getName());
					Vertex inVertex = edge.getVertex(Direction.IN);
					if (fromId.equals(inVertex.getId())) {
						if (toId.equals(edge.getVertex(Direction.OUT))) {
							result = edge;
							break;
						}
					} else if (toId.equals(inVertex.getId())) {
						result = edge;
						break;
					}
				}
			}
		} else {
			//			System.out.println("DEBUG: No edges defined in EdgeList");
		}
		return result;
	}

}
