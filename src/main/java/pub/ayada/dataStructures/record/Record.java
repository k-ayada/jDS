package pub.ayada.dataStructures.record;

import java.io.Serializable;
import java.util.ArrayList;

import pub.ayada.dataStructures.record.ColumnMeta;
public class Record implements Serializable, Cloneable{
	private static final long serialVersionUID = 7213802130508244520L;
	Object[] columns; 
	
    public Record(int columnsCount) {
    	this.columns = new Object[columnsCount];
    }
    
    public Record(Object[] columns) {
    	this.columns = columns;
    }    

	public Object[] getColumns() {
		return this.columns;
	}

	public void setColumns(Object[] columns) {
		this.columns = columns;
	}
	
	public void setColumn(int inx, Object newValue) {
		this.columns[inx] = newValue;
	}	
    
    public Object getColum(int inx) {
    	return this.columns[inx];
    }
    
    public String toString() {    	
    	StringBuilder b =   new StringBuilder();
  	    for (int i = 0; i < this.columns.length; i++)
  	    	if (this.columns[i] == null)
  	    		b.append("<null> , ");
    		else
    			b.append(this.columns[i].toString()).append(" , ");  
  	  b.setLength(b.length() - 3);
  	  return b.toString();
    }

	public int compareKeys(Record rec, ArrayList<ColumnMeta> sortKeys) throws Exception {
		int res = 0, i = 0, itrLen = sortKeys.size();

		Object[] leftKeys  = this.getKeyValues(sortKeys);
		Object[] rightKeys = rec.getKeyValues(sortKeys);
		
		while (true) {
			if (!(res == 0 && i < itrLen))
				break;
			res = compareObjects(leftKeys[i]
					            ,rightKeys[i] 
					            ,(sortKeys.get(i)
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
	
	public Object[] getKeyValues(ArrayList<ColumnMeta> keys) throws Exception {
		Object[] keyValue = new Object[keys.size()];
		int[] keyOrder= new int[keys.size()];
		int i=0,inx=0;
		try {
			for (;i<keys.size();i++) {
				keyValue[inx] = getColum(keys.get(i).getColumnNum());
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
/*	
	public void merageRecords(Record fromRec, SumDetails sumDetailsObj) {
		
		for (int i = 0; i < sumDetailsObj.getSumColumns().length; i++) {
			int inx = sumDetailsObj.getSumColumns()[i];
			if (this.getColum(inx).getClass()
					   .isAssignableFrom(Integer.class)) {
				int val = ((Integer) this.getColum(inx)).intValue()
						+ ((Integer) fromRec.getColum(inx)).intValue();
				this.setColumn(inx, new Integer(val));
			} else if (this.getColum(inx).getClass()
					          .isAssignableFrom(Long.class)) {
				long val = ((Long) this.getColum(inx)).intValue()
						+ ((Long) fromRec.getColum(inx)).intValue();
				this.setColumn(inx, new Long(val));
			} else if (this.getColum(inx).getClass()
					          .isAssignableFrom(Float.class)) {
				float val = ((Float) this.getColum(inx)).floatValue()
						+ ((Float) fromRec.getColum(inx)).floatValue();
				this.setColumn(inx, new Float(val));
			} else if (this.getColum(inx).getClass()
					          .isAssignableFrom(Double.class)) {
				double val = ((Double) this.getColum(inx)).doubleValue()
						+ ((Double) fromRec.getColum(inx)).doubleValue();
				this.setColumn(inx, new Double(val));
			} else if (this.getColum(inx).getClass()
					          .isAssignableFrom(BigDecimal.class)) {
				this.setColumn(inx, ((BigDecimal) this.getColum(inx))
						.add((BigDecimal) fromRec.getColum(inx)));
			} else if (this.getColum(inx).getClass()
				              .isAssignableFrom(String.class)) {
				this.setColumn(inx, 
					            (String)this.getColum(inx) 
					            +sumDetailsObj.getStringConcatSeparator()
					            +(String)fromRec.getColum(inx));
		   }				
		}			
		
	}
*/	
}
