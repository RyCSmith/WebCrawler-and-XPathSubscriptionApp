package edu.upenn.cis455.xpathengine;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Tree API
 * @param <V> The type of value that can be held in each Tree node.
 */
public class Tree<V> implements Iterable<Tree<V>> {

    private V value;
    private ArrayList<Tree<V>> children;
    
    /**
     * Constructs a Tree with the given value in the root node,
     * having the given children.
     * @param value The value to be put in the root.
     * @param children The immediate children of the root.
     */
    public Tree(V value, Tree<V>... children) {
        this.value = value;
        this.children = new ArrayList<Tree<V>>();
        for(Tree<V> child : children){
        	this.children.add(child);
        }
    }
    
    /**
     * Sets the value in this node.
     * @param value The value to be stored in this node.
     */
    public void setValue(V value) {
    	this.value = value;
    }
    
    /**
     * Returns the value in this node.
     * @return The value in this node.
     */
    public V getValue() {
        return value;
    }
    
    /**
     * Adds the child as the new <code>index</code>'th child of this Tree;
     * subsequent nodes are "moved over" as necessary to make room for the
     * new child.
     * 
     * @param index The position in which to put the new child.
     * @param child The child to be added to this node.
     * @throws IllegalArgumentException
     *         If the operation would create a circular Tree.
     */
    public void addChild(int index, Tree<V> child) {
    	if (child.contains(this))
    		throw new IllegalArgumentException("This would create a cycle");
    	else
    		children.add(index, child);
    }
    
    /**
     * Adds the child as the new last child of this node.
     * @param child The child to be added to this node.
     * @throws IllegalArgumentException
     *         If the operation would create a circular Tree.
     */
    public void addChild(Tree<V> child) {
    	//check if tree contains this node, if so there will be a cycle
    	if (child.contains(this))
    		throw new IllegalArgumentException("This would create a cycle");
    	else
    		children.add(child);
    }
    
    /**
     * Adds the children to this node, after the current children.
     * 
     * @param children The nodes to be added as children of this node.
     * @throws IllegalArgumentException
     *         If the operation would create a circular Tree.
     */
    public void addChildren(Tree<V>... children) {
        for (Tree<V> child : children){
        	if (child.contains(this))
        		throw new IllegalArgumentException("This would create a cycle");
        	else
        		this.children.add(child);
        }
    }
    
    /**
     * Returns the number of children that this node has.
     * 
     * @return A count of this node's immediate children.
     */
    public int getNumberOfChildren() {
        return children.size();
    }
    
    public ArrayList<Tree<V>> getChildren() {
    	return children;
    }
    
    /**
     * Returns the <code>index</code>'th child of this node.
     *  
     * @param index The position of the child that is to be returned.
     * @return The child at that position.
     * @throws IndexOutOfBoundsException If <code>index</code> is negative or
     *     is greater than or equal to the current number of children of this node.
     */
    public Tree<V> getChild(int index) {
        return children.get(index);
    }
    
    /**
     * Returns an iterator for the children of this node. 
     * 
     * @return An iterator for this node's immediate children.
     */
    @Override
    public Iterator<Tree<V>> iterator() {
        return children.iterator();
    }
    
    /**
     * Searchs this Tree for a node that is == to <code>node</code>,
     * and returns <code>true</code> if found, <code>false</code> otherwise.
     * 
     * @param node The node to be searched for.
     * @return <code>true</code> if the node is found.
     */
    boolean contains(Tree<V> node) {
        if (this == node)
        	return true;
        for (Tree<V> child : this.children){
        	if (child.contains(node) == true)
        		return true;
        }
        return false;
    }
    
    /**
     * Returns a one-line string representing this tree.
     * The form of the output is:<br>
     * <code>value(child, child, ..., child)</code>.
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toStringHelper(this).toString();
    }
    
    private StringBuilder toStringHelper(Tree<V> tree){
    	StringBuilder sb = new StringBuilder();
    	//if node is a leaf return its value
    	if (tree.getNumberOfChildren() == 0){
    		sb.append(tree.getValue());
    		return sb;
    	}
    	else{
    		//append the value of the current node, then recurse in order (left to right) to append value of its children
    		sb.append(tree.getValue() + " (");
    		for (int i = 0; i < tree.getNumberOfChildren(); i++){
    			sb.append(toStringHelper(tree.getChild(i)));
    			sb.append(" ");
    		}
    		//just removes unwanted space before )
    		sb.deleteCharAt(sb.length() -1);
    		sb.append(")");
    		return sb;
    	}
    }
    
    /**
     * Prints this tree as an indented structure.
     */
    public void print() {
        print(this, "");
    }   
    
    /**
     * Prints the given tree as an indented structure, with the
     * given node indented by the given amount.
     * @param tree The root of the tree or subtree to be printed.
     * @param indent The amount to indent the root.
     */
    public static void print(Tree<?> tree, String indent) {
    	if (tree != null){
    		System.out.printf(indent + tree.value + "\n");
    		String newIndent = indent + "   ";
    		for (int i = 0; i < tree.getNumberOfChildren(); i++) {
    			print(tree.getChild(i), newIndent);
    		}
    	}
    }
    
    /**
     * Tests whether the input argument is a Tree having the same shape
     * and containing the same values as this Tree.
     * 
     * @param obj The object to be compared to this Tree.
     * @return <code>true</code> if the object is equals to this Tree,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object obj) {
    	//node has different number fo children - false
    	if (this.getNumberOfChildren() != ((Tree)obj).getNumberOfChildren())
    		return false;
    	//last node in the line and they are not equal - false
    	if (this.getNumberOfChildren() == 0 && !this.equalsHelper(obj))
        	return false;
        for (int i = 0; i < this.getNumberOfChildren(); i++){
        	if (this.getChild(i).equals(((Tree) obj).getChild(i)) == false)
        		return false;
        }
        return true;
    }
    
    private boolean equalsHelper(Object obj){
    	if (!(obj instanceof Tree)) 
    		return false;
   	 	Tree<V> argTree = (Tree) obj;
   	 	if (this.value.equals(argTree.value) && this.children.equals(argTree.children))
   	 		return true;
   	 	else
   	 		return false;
    }
        
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
    	//uses String representation of the Tree to produce a unique and consistent hashCode
        int total = hashCodeHelper(this);
        return total;
        
    }

    private int hashCodeHelper(Tree<V> tree){
    	int total = 0;
    	if (tree.getNumberOfChildren() == 0){
    		char[] stringRep = this.toString().toCharArray();
            for (Character c : stringRep){
            	total += c.hashCode();
            }
            return total;
    	}
    	else{	
	        for (int i = 0; i < tree.getNumberOfChildren(); i++){
	        	total += hashCodeHelper(tree.getChild(i));
	        }
    	}
        return total;
    }
    

}

