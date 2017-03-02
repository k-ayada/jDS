package pub.ayada.dataStructures.queues;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class CQueue<E> implements Cloneable, Serializable {
	protected static final long serialVersionUID = 2311548119323648571L;

	protected 
	  final   int CQueue_Length;
	protected int PopFrom=0;
	protected int PushTo=0;
	protected AtomicInteger AddCount= new AtomicInteger(0);
	protected AtomicInteger GetCount= new AtomicInteger(0);
	protected E[] arr;
	protected boolean LoadEnded=false;
	protected boolean test=false;

	public CQueue(int CQueue_Length) {
		this(CQueue_Length,null, false);
	}

	public CQueue(int CQueue_Length, boolean test) {
		this(CQueue_Length,null, test);
	}	
	public CQueue(int CQueue_Length, CQueue<E> copyFrmQ) {
		this(CQueue_Length,copyFrmQ, false);
	}
	
	public CQueue(CQueue<E> copyFrmQ) {
		this(copyFrmQ.getCount(),copyFrmQ, false);
	}	
	
	@SuppressWarnings("unchecked")
	public CQueue(int CQueue_Length, CQueue<E> copyFrmQ, boolean test) {
		this.CQueue_Length = CQueue_Length;
		this.test = test;
		this.arr = (E[]) new Object[this.CQueue_Length+1];
		if (copyFrmQ != null) {
			this.PushTo = copyFrmQ.getNextPushPos();
			this.AddCount.set(this.PushTo-copyFrmQ.getNextPopPos());
			System.arraycopy(copyFrmQ.getQdata() 
					        ,copyFrmQ.getNextPopPos()
					        ,this.arr
					        ,0
					        ,this.PushTo-copyFrmQ.getNextPopPos()+1);
		}
	}
	/**
	 * Rebuilds the current using the input queue
	 * @param copyFrmQ (CQueue<E>)
	 */
	@SuppressWarnings("unchecked")
	public void rebuildFromQ(CQueue<E> copyFrmQ) {
		
		if (copyFrmQ == null) { 
			this.arr = (E[]) new Object[this.CQueue_Length+1];
			return;
		}
		int newSize = (  this.arr.length < copyFrmQ.getQsize() 
				       ? copyFrmQ.getQsize()+1
                       : this.arr.length); 
		this.PushTo = copyFrmQ.getCount();
		this.AddCount.set(this.AddCount.intValue() + this.PushTo);
		this.arr = (E[]) new Object[newSize];
		System.arraycopy(copyFrmQ.getQdata(), copyFrmQ.getNextPopPos(), this.arr, 0, this.PushTo+1);
	}
	
	protected int getQsize() {
		return this.arr.length;
	}

	public int getNextPopPos() {
		return this.PopFrom;
	}
	public int getNextPushPos() {
		return this.PushTo;
	}
	
	protected E[] getQdata() {
		return  this.arr;
	}
 
	public void addWhenPossible(E e, int sleepMilSec) throws InterruptedException {
		 while (true) {
			 if (canAdd())
				 break;
			 Thread.sleep(sleepMilSec);
		 }
		 add(e);
	}
	
	
	public boolean addWhenPossible(E e, int sleepMilSec, int loopLimit) throws InterruptedException {
		 int i=0;
		 
		 while (++i <= loopLimit) {
			 if (canAdd()) {
				 add(e);
				 return true;
			 }		 
			 Thread.sleep(sleepMilSec);
		 }
		 return false;
	}	
	
	public boolean tryAdd(E e) {
	       if (canAdd()) {
	    	   add(e);
	    	   return true;
	       }
	       else return false;
		}
		protected void add(E e) {
		    this.arr[this.PushTo] = e;
			this.PushTo = inc(this.PushTo);
			this.AddCount.incrementAndGet();
			log("Add");
		}

		protected int inc(int inx) {
			return (inx == this.CQueue_Length ? 0: (inx + 1));
		}

		public E getWhenCan(int sleepInterval) throws Exception {
			 while (true) {
				 if (canGet())
					 return getNremove();
				 Thread.sleep(sleepInterval);
			 }
		}
		public E getWhenCan(int sleepInterval, int looplimit) throws Exception {
			int lim=0;
			 while (true) {
				 if (canGet())
					 return getNremove();
				 Thread.sleep(sleepInterval);
				 lim++;
				 if (lim >= looplimit)
					return null;
			 }
		}

	    public E tryGet() throws Exception {
	    	if (canGet())
	    	   return getNremove();
	    	return (E) null;
	    }

	    @SuppressWarnings("unchecked")
		protected E getNremove() throws Exception {
	    	Object o = this.arr[this.PopFrom];
			this.arr[this.PopFrom]=null;
	    	this.PopFrom = inc(this.PopFrom);
	    	this.GetCount.incrementAndGet();
//		    logGet((Object[]) o);
	    	log("Get");
	    	return (E) o;
	    }

		public boolean canAdd() {
		 //   log("canAdd()  -" + (this.arr[this.PushTo] == null));
			if (this.arr[this.PushTo] == null)
			   return true;
			return false;
		}
		public boolean canGet() {
	   	 //  log ("canGet()  -" + !(this.arr[this.PopFrom] == null));
	       return !(this.arr[this.PopFrom] == null);
	 	}

		public boolean isLoadEnded() {
			return this.LoadEnded;
		}

		public int getCount() {
			return this.AddCount.get() - this.GetCount.get();
		}

		public void setLoadEnded(boolean loadEnded) {
			this.LoadEnded = loadEnded;
		}


		protected void log(String from) {

	      if (this.test) {
	    	  System.err.println(Thread.currentThread().getName() + String.format("%15s", from) 
	    			            +" Push Pos:" + String.format("%5d",this.PushTo)
	    						   +" Pop pos:" + String.format("%5d",this.PopFrom)
	    						   +" In Q:" + String.format("%5d",getCount())
	    						   +" Load_End:" + this.LoadEnded
	    						   +" Can_Add:" + (this.arr[this.PushTo] == null)
	    						   +" Can_Get:" + !(this.arr[this.PopFrom] == null || getCount() == 0));
	      //L.info(from +" Push Position:" + (this.PushTo)+" Pop position:" + this.PopFrom +" In Q :" + getCount());
	      }
		}


		protected void logGet(Object[] o) {

			if (this.test) {
				StringBuilder b = new StringBuilder();
				for (int i = 0; i < o.length; i++)
					if (o[0] == null)
						b.append("<null> , ");
					else
						b.append(o[i].toString()).append(" , ");
				b.setLength(b.length() - 3);
				System.out.println(Thread.currentThread().getName()
						           + " Get. Push Position:" + (this.PushTo)+" Pop position:" + this.PopFrom +" In Q :" + getCount() + "  " + b.toString());
			}
		}
		
		public Object clone() throws CloneNotSupportedException {
	        return super.clone();
	    }		
		public String getStats() {

			return  " Push Pos:" + String.format("%5d",this.PushTo)
				   +" Pop pos:" + String.format("%5d",this.PopFrom)
				   +" In Q:" + String.format("%5d",getCount())
				   +" Load_End:" + this.LoadEnded
				   +" Can_Add:" + (this.arr[this.PushTo] == null)
				   +" Can_Get:" + !(this.arr[this.PopFrom] == null || getCount() == 0)
				   ;

		}

		@SuppressWarnings("unchecked")
		public synchronized void flush() {
			this.arr = (E[]) new Object[this.CQueue_Length+1];
			this.PopFrom=0;
		    this.PushTo=0;
		    this.AddCount.set(0);
		    this.GetCount.set(0);
			this.LoadEnded=false;
		}
}
