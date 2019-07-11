package pub.ayada.commons.ds.ast.node;

import java.io.Serializable;
import java.util.ArrayList;

import pub.ayada.commons.ds.ast.tokens.Token;

public class Node<T extends Object> implements Cloneable, Serializable {
	
	private static final long serialVersionUID = 7925649524214521032L;
	private Node<Token<?>> lChild;
	private Node<Token<?>> rChild;
	private Node<Token<?>> parent;
	private ArrayList<Token<?>> multiparms = new ArrayList<>();
	private Token<?> value;
	private NodeType nodeType;
	
	
	public Node(T value, NodeType nodeType, Token<?> Value) {
		super();
		this.value = Value;
		this.nodeType = nodeType;
		this.lChild = null;
		this. rChild = null;
	}
	
	public Node(Token<?> Value,  NodeType nodeType) {
		this.value =  Value;
		this.nodeType = nodeType;
		this.lChild = null;
		this. rChild = null;
		this.parent = null;
	}

	public boolean isLeaf() {
		return (this.lChild == null && this.rChild == null);
	}
	
	public Token<?> getValue() {
		return this.value;
	}

	public Node<Token<?>> getlChild() {
		return lChild;
	}

	public void setlChild(Node<Token<?>> lChild) {
		this.lChild = lChild;
	}

	public Node<Token<?>> getrChild() {
		return rChild;
	}

	public void setrChild(Node<Token<?>> rChild) {
		this.rChild = rChild;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public void setValue(Token<?> value) {
		this.value= value;
	}

	public void setParent(Node<Token<?>> node) {
		this.parent = node;
	}

	public Node<Token<?>> getParent() {
		return this.parent;
	}

	public boolean isRoot() {
		return (this.parent == null) ;
	}
}
