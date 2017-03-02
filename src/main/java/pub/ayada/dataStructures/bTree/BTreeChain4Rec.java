package pub.ayada.dataStructures.bTree;

import java.util.ArrayList;


import pub.ayada.dataStructures.record.Record;
import pub.ayada.dataStructures.record.ColumnMeta;

public class BTreeChain4Rec<T extends BTreeNode4Rec<Record>> {

	private final int max = 2;
	private final int mid = 1;
	private final int min = 0;
	private int totNodesCnt;
	private int leftNodesCnt;
	private int rightNodescnt;
	private boolean sumNewNodeVal=false;

	private ArrayList<T> Nodes = new ArrayList<T>(3);
	private Object[][] Key = new Object[3][2];
	private ArrayList<ColumnMeta> keys = new ArrayList<ColumnMeta>();

	public BTreeChain4Rec() {
		this.totNodesCnt = 0;
		this.leftNodesCnt = 0;
		this.rightNodescnt = 0;
		this.Nodes.add((T) null);
		this.Nodes.add((T) null);
		this.Nodes.add((T) null);
	}

	public BTreeChain4Rec(ArrayList<ColumnMeta> sortKeys) {
		this.totNodesCnt = 0;
		this.leftNodesCnt = 0;
		this.rightNodescnt = 0;
		this.Nodes.add((T) null);
		this.Nodes.add((T) null);
		this.Nodes.add((T) null);
		this.keys.addAll(sortKeys);
	}

	public void addKeys(ColumnMeta SortKey) {
		this.keys.add(SortKey);
	}

	public void setKeys(ArrayList<ColumnMeta> SortKeys) {
		this.keys.clear();
		this.keys.addAll(SortKeys);
	}

	@SuppressWarnings("unchecked")
	public T reach_Mid_Of_Mid_Min(T trNode, int count) {
		if (count == 1 && trNode.getParent() == null)
			return trNode;

		while (count-- > 0) {
			if (trNode.getParent() == null)
				break;

			trNode = (T) trNode.getParent();
		}
		return trNode;
	}

	@SuppressWarnings("unchecked")
	public T reach_Mid_Of_Mid_Max(T trNode, int count) {
		if (count == 1 && trNode.getChild() == null)
			return trNode;

		while (count-- > 0) {
			if (trNode.getChild() == null)
				break;
			trNode = (T) trNode.getChild();
		}
		return trNode;
	}

	public boolean addNode(T newNode) throws Exception {

		updateMidIndex();
		Object[] newKey = newNode.getCurNode()
				                 .getKeyValues(this.keys);
		
		newNode.setSameAsChild(false);

	   /*
	    * System.err.println(String.format("%3d", this.tNodes) + " for " +
		* newNode.getCurNode().toString() + ". Hash :" + newKeyHash );
		*/
		if (this.totNodesCnt > 2) {
			boolean addAsParent;
			boolean smallFound = false, largeFound = false;

			int midComp = compareKeys(this.Key[this.mid], newKey);
			T trNode = (T) this.Nodes.get(this.mid);

			int newMid = 0;
			if (midComp > 0) {
				this.leftNodesCnt++;
				if (compareKeys(this.Key[this.min], newKey) > 0) {
					addAsParent(newNode, this.Nodes.get(this.min));
					this.totNodesCnt++;
					return true;
				}
				addAsParent = true;
				newMid = this.leftNodesCnt;
				while (true) {
					newMid = newMid - (int) (newMid / 2);
					if (midComp < 0)
						trNode = reach_Mid_Of_Mid_Max(trNode, newMid);
					else
						trNode = reach_Mid_Of_Mid_Min(trNode, newMid);

					midComp = compareKeys(trNode.getCurNode()
							                    .getKeyValues(this.keys)
							              ,newKey);
					if (midComp == 0 && this.sumNewNodeVal) 
						break;
					
					if (smallFound && midComp > 0) {
						addAsParent = true;
						if (newMid == 1)
							break;
					}
					if (!smallFound && midComp <= 0) {
						smallFound = true;
						addAsParent = false;
						if (newMid == 1)
							break;
					}
				}
			} else {
				this.rightNodescnt++;
				if (compareKeys(this.Key[this.max], newKey) <= 0) {
					addAsChild(newNode, this.Nodes.get(this.max));
					this.totNodesCnt++;
					return true;
				}
				addAsParent = false;
				newMid = this.rightNodescnt;
				while (true) {
					newMid = newMid - (int) (newMid / 2);
					if (midComp <= 0)
						trNode = reach_Mid_Of_Mid_Max(trNode, newMid);
					else
						trNode = reach_Mid_Of_Mid_Min(trNode, newMid);

					midComp = compareKeys(trNode.getCurNode()
							                    .getKeyValues(this.keys)
							             ,newKey);

					if (midComp == 0 && this.sumNewNodeVal) 
						break;
					
					if (largeFound && midComp <= 0) {
						addAsParent = false;
						if (newMid == 1)
							break;
					}
					if (!largeFound && midComp > 0) {
						largeFound = true;
						addAsParent = true;
						if (newMid == 1)
							break;
					}
				}
			}
			if (addAsParent)
				 addAsParent(newNode, trNode);
			else addAsChild(newNode, trNode);

			// dispInAscendingOrder();
			this.totNodesCnt++;
			return true;
		}

		/*
		 * This is the third node being added. The Decision Logic Table to
		 * insert b/w 2 & 4 ------------ --- --- --- --------- ---------
		 * ---------- new min mid max minComp maxComp sum mid = 1* > 1* 2 4
		 * 1=(2>=1) 1=(4>=1) 2=(1+1) --> newAsMin_MinAsMid mid = 2* > 2 2* 4
		 * 0=(2>=1) 1=(4>=2) 1=(0+1) --> newAsMid mid = 3* > 2 3* 4 -1=(2>=3)
		 * 1=(4>=3) 0=(-1+1) --> newAsMid mid = 4* > 2 4 4* -1=(2>=4) 0=(4>=4)
		 * -1=(-1+0) --> newAsMax_MaxAsMid mid = 5* > 2 4 5* -1=(2>=5) -1=(4>=5)
		 * -2=(-1+-1) --> newAsMax_MaxAsMid
		 */
		if (this.totNodesCnt == 2) {
			int minComp = compareKeys(this.Key[this.min], newKey);
			
			int maxComp = compareKeys(this.Key[this.max], newKey);
			this.leftNodesCnt++;
			this.rightNodescnt++;
			if ((minComp + maxComp) < 0)
				swap(newNode, newKey, this.max);
			else if ((minComp + maxComp) > 1)
				swap(newNode, newKey, this.min);
			else {
				this.Nodes.set(this.mid, newNode);
				this.Key[this.mid] = newKey;
			}
			((T) this.Nodes.get(this.min)).setParent((T) null);
			((T) this.Nodes.get(this.min)).setChild((T) this.Nodes
					.get(this.mid));

			((T) this.Nodes.get(this.mid)).setParent((T) this.Nodes
					.get(this.min));
			((T) this.Nodes.get(this.mid)).setChild((T) this.Nodes
					.get(this.max));

			((T) this.Nodes.get(this.max)).setParent((T) this.Nodes
					.get(this.mid));
			((T) this.Nodes.get(this.max)).setChild((T) null);

			// If both Parent child's key are same,
			// Set the Node's SameAsChild to true. Else false.
			if (minComp == 0)
				((T) this.Nodes.get(this.min)).setSameAsChild(true);
			else
				((T) this.Nodes.get(this.min)).setSameAsChild(false);

			if (maxComp == 0)
				((T) this.Nodes.get(this.mid)).setSameAsChild(true);
			else
				((T) this.Nodes.get(this.mid)).setSameAsChild(false);
			this.totNodesCnt++;
			return true;
		}

		// This is the second node being added.
		if (this.totNodesCnt == 1) {
			// min <= new. set the new node as Max
			int res = compareKeys(this.Key[this.min], newKey);
			if (res <= 0) {
				this.Nodes.set(this.max, newNode);
				this.Key[this.max] = newKey;
			}
			// min > new. Set new as the min and min as max
			else {
				this.Nodes.set(this.max, this.Nodes.get(this.min));
				this.Key[this.max] = this.Key[this.min];
				this.Nodes.set(this.min, newNode);
				this.Key[this.min] = newKey;
			}
			if (res == 0)
				this.Nodes.get(this.min).setSameAsChild(false);

			((T) this.Nodes.get(this.min))
			         .setChild((T) this.Nodes.get(this.max));
			((T) this.Nodes.get(this.max))
			         .setParent((T) this.Nodes.get(this.min));
			this.totNodesCnt++;
			return true;
		}

		// This is the first node being added.
		if (this.totNodesCnt == 0) {
			this.Nodes.set(this.min, newNode);
			this.Key[this.min] = newKey;
			this.totNodesCnt++;
			return true;
		}
		
		throw new Exception("Failed to add the new record."
				           +"\nRecord :" + newNode.getCurNode().toString());
	}

	@SuppressWarnings("unchecked")
	private void addAsParent(T newNode, T trNode) throws Exception {
		newNode.setParent((T) trNode.getParent());
		newNode.setChild(trNode);
		if (trNode.getParent() != null)
			((T) trNode.getParent()).setChild(newNode);
		else {
			this.Key[this.min] = newNode.getCurNode()
					                    .getKeyValues(this.keys);
			this.Nodes.set(this.min, newNode);
		}
		trNode.setParent((T) newNode);
	}

	@SuppressWarnings("unchecked")
	private void addAsChild(T newNode, T trNode) throws Exception {
		newNode.setChild((T) trNode.getChild());
		newNode.setParent(trNode);
		if (trNode.getChild() != null)
			((T) trNode.getChild()).setParent(newNode);
		else {
			this.Key[this.max] = newNode.getCurNode()
					                    .getKeyValues(this.keys);
			this.Nodes.set(this.max, newNode);
		}
		trNode.setChild((T) newNode);
	}

	private void swap(T newNode, Object[] newKey, int newKeyInx) {
		this.Nodes.set(this.mid, this.Nodes.get(newKeyInx));
		this.Key[this.mid] = this.Key[newKeyInx];
		this.Nodes.set(newKeyInx, newNode);
		this.Key[newKeyInx] = newKey;
	}

	@SuppressWarnings("unchecked")
	private void updateMidIndex() throws Exception {
		if (Math.abs(this.leftNodesCnt - this.rightNodescnt) >= 100) {
			T newMid = ((T) this.Nodes.get(this.mid));

			if (this.leftNodesCnt > this.rightNodescnt) {
				while (this.leftNodesCnt > this.rightNodescnt) {
					newMid = (T) newMid.getParent();
					this.leftNodesCnt--;
					this.rightNodescnt++;
				}
			} else {
				while (this.rightNodescnt > this.leftNodesCnt) {
					newMid = (T) newMid.getChild();
					this.rightNodescnt--;
					this.leftNodesCnt++;
				}
			}
			this.Nodes.set(this.mid, newMid);
			this.Key[this.mid] = newMid.getCurNode()
					                   .getKeyValues(this.keys);
		}
	}
 
	private int compareKeys(Object[] object, Object[] object2) throws Exception {
		int res = 0, i = 0, itrLen = object.length;

		while (true) {
			if (!(res == 0 && i < itrLen))
				break;
			res = compareObjects(object[i]
					            ,object2[i]
					            ,(this.keys.get(i)
					                  .getOrder() == 'A' ? 1 : -1));
			i++;
		}
		return res;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private int compareObjects(Object oLeft, Object oRight, int order)
			throws Exception {

		if (oLeft == null || oRight == null) {
			boolean l = (oLeft == null ? true : false);
			boolean r = (oRight == null ? true : false);
			if (l && !r)
				return -1;
			if (!l && r)
				return 1;
			if (l && r)
				return 0;
		}
		return order * ((Comparable) oLeft).compareTo((Comparable) oRight);
	}
 
	@SuppressWarnings("unchecked")
	public void dispInAscendingOrder() {

		int count = 0;
		T trNode = this.Nodes.get(this.min);

		while (true) {
			if (++count > this.totNodesCnt)
				break;
			Record rec = trNode.getCurNode();
			if (rec == null)
				continue;
			System.err.println(String.format("%10d", (count)) + "->"
					+ rec.toString());
			trNode = (T) trNode.getChild();
			if (trNode == null)
				break;
		}

	}

	public String getStats() {

		return new StringBuilder("Nodes")
				.append(String.format(". Total: %10d", this.totNodesCnt))
				.append(String.format(". Left: %10d", this.leftNodesCnt))
				.append(String.format(". Right: %10d", this.rightNodescnt)).toString();

	}

	public T getMinNode() {
		return this.Nodes.get(this.min);
	}
}
