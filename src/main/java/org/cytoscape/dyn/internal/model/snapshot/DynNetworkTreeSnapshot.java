package org.cytoscape.dyn.internal.model.snapshot;

import java.util.Collection;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;

/**
 * <code> DynNetworkTree </code> is the interface to investigate a tree dynamic network in a specific
 * snapshot in time given a time interval.
 * 
 * @author Sabina Sara Pfister
 *
 * @param <T>
 */
public interface DynNetworkTreeSnapshot<T> extends DynNetworkSnapshot<T>
{
	   /**
     * Returns a view of this graph as a collection of <code>Tree</code> instances.
     * @return a view of this graph as a collection of <code>Tree</code>s
     */
	Collection<CyNode> getRoots();

    /**
     * Returns the parent of <code>vertex</code> in this tree.
     * (If <code>vertex</code> is the root, returns <code>null</code>.)
     * The parent of a vertex is defined as being its predecessor in the 
     * (unique) shortest path from the root to this vertex.
     * This is a convenience method which is equivalent to 
     * <code>Graph.getPredecessors(vertex).iterator().next()</code>.
     * @return the parent of <code>vertex</code> in this tree
     */
    public CyNode getParent(CyNode node);
    
    /**
     * Returns the edge connecting <code>vertex</code> to its parent in
     * this tree.
     * (If <code>vertex</code> is the root, returns <code>null</code>.)
     * The parent of a vertex is defined as being its predecessor in the 
     * (unique) shortest path from the root to this vertex.
     * This is a convenience method which is equivalent to 
     * <code>Graph.getInEdges(vertex).iterator().next()</code>,
     * and also to <code>Graph.findEdge(vertex, getParent(vertex))</code>.
     * @return the edge connecting <code>vertex</code> to its parent, or 
     * <code>null</code> if <code>vertex</code> is the root
     */
    public CyEdge getParentEdge(CyNode node);
    
    /**
     * Returns the children of <code>vertex</code> in this tree.
     * The children of a vertex are defined as being the successors of
     * that vertex on the respective (unique) shortest paths from the root to
     * those vertices.
     * This is syntactic (maple) sugar for <code>getSuccessors(vertex)</code>. 
     * @param vertex the vertex whose children are to be returned
     * @return the <code>Collection</code> of children of <code>vertex</code> 
     * in this tree
     */
    public Collection<CyNode> getChildren(CyNode node);
    
    /**
     * Returns the edges connecting <code>vertex</code> to its children 
     * in this tree.
     * The children of a vertex are defined as being the successors of
     * that vertex on the respective (unique) shortest paths from the root to
     * those vertices.
     * This is syntactic (maple) sugar for <code>getOutEdges(vertex)</code>. 
     * @param vertex the vertex whose child edges are to be returned
     * @return the <code>Collection</code> of edges connecting 
     * <code>vertex</code> to its children in this tree
     */
    public Collection<CyEdge> getChildEdges(CyNode node);
    
    /**
     * Returns the number of children that <code>vertex</code> has in this tree.
     * The children of a vertex are defined as being the successors of
     * that vertex on the respective (unique) shortest paths from the root to
     * those vertices.
     * This is syntactic (maple) sugar for <code>getSuccessorCount(vertex)</code>. 
     * @param vertex the vertex whose child edges are to be returned
     * @return the <code>Collection</code> of edges connecting 
     * <code>vertex</code> to its children in this tree
     */
    public int getChildCount(CyNode node); 
    
    /**
     * Returns the (unweighted) distance of <code>vertex</code> 
     * from the root of this tree.
     * @param vertex    the vertex whose depth is to be returned.
     * @return the length of the shortest unweighted path 
     * from <code>vertex</code> to the root of this tree
     */
    public int getDepth(CyNode node);
    
    /**
     * Returns the maximum depth in this tree.
     * @return the maximum depth in this tree
     */
    public int getHeight(CyNode node);
    
    /**
     * Returns the root of this tree.
     * The root is defined to be the vertex (designated either at the tree's
     * creation time, or as the first vertex to be added) with respect to which 
     * vertex depth is measured.
     * @return the root of this tree
     */
    public CyNode getRoot(CyNode node);
}