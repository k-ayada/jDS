package pub.ayada.dataStructures.bTree;

import pub.ayada.dataStructures.record.Record;

public class BTreeNode4Rec<T extends Record> extends Object {

	private BTreeNode4Rec<T> parent;
	private BTreeNode4Rec<T> child;
	private T curNode;
	private boolean sameAsChild=false;

	public BTreeNode4Rec( T CurrentNode) {
		this.curNode = CurrentNode;
	}

	public BTreeNode4Rec(BTreeNode4Rec<T> Parent, BTreeNode4Rec<T> Child,  T CurrentNode) {
		this.parent = Parent;
		this.child = Child;
		this.curNode = CurrentNode;
	}
	public BTreeNode4Rec<T> getParent() {
		return this.parent;
	}
	public void setParent(BTreeNode4Rec<T>  NewNode) {
		this.parent =  NewNode;
	}
	public BTreeNode4Rec<T> getChild() {
		return this.child;
	}
	public void setChild(BTreeNode4Rec<T> Child) {
		this.child = Child;
	}

	public T getCurNode() {
		return this.curNode;
	}
	public void setCurNode(T CurNode) {
		this.curNode = CurNode;
	}

	public boolean isSameAsChild() {
		return this.sameAsChild;
	}
	public void setSameAsChild(boolean SameAsChild) {
		this.sameAsChild = SameAsChild;
	}
/*
	public Object[] getKeyValues(ArrayList<KeyData> keys) throws Exception {
		Object[] keyValue = new Object[keys.size()];
		int[] keyOrder= new int[keys.size()];
		int i=0,inx=0;
		try {
			for (;i<keys.size();i++) {
				keyValue[inx] = this.rec.getColum(keys.get(i).getColumnNum());
			    keyOrder[inx] = (keys.get(i).getOrder() == 'A' ? 1 : -1) ;
			    inx++;
			}    
		}
		catch(Exception e) {
			throw  new Exception ("Failed to get keys from :" + keys.toArray()
					+ "i=" + i + "  inx=" + inx + " res=" + keyValue.toString()
					);
		}
		return keyValue;
	}
*/
}
