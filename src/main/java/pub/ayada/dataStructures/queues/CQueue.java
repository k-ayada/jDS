package pub.ayada.dataStructures.queues;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class CQueue<E> implements Cloneable, Serializable {
	protected static final long serialVersionUID = 2311548119323648571L;

	private final int CQueue_Length;
	private final String qID;
	protected volatile int PopFrom = 0;
	protected volatile int PushTo = 0;
	protected AtomicInteger AddCount = new AtomicInteger(0);
	protected AtomicInteger ReadCount = new AtomicInteger(0);
	protected E[] arr;
	protected AtomicBoolean LoadEnded = new AtomicBoolean(false);
	protected boolean test = false;

	 /**
	  * <b>Constructor</b>
	  * @param queueID
	  * @param CQueue_Length
	  */
	public CQueue(String queueID, int CQueue_Length) {
		this(queueID, CQueue_Length, null, false);
	}

	/**
	 * <b>Constructor</b>
	 * @param queueID
	 * @param CQueue_Length
	 * @param test
	 */
	public CQueue(String queueID, int CQueue_Length, boolean test) {
		this(queueID, CQueue_Length, null, test);
	}
    /**
     * <b>Constructor</b>
     * @param queueID
     * @param CQueue_Length
     * @param copyFrmQ
     */
	public CQueue(String queueID, int CQueue_Length, CQueue<E> copyFrmQ) {
		this(queueID, CQueue_Length, copyFrmQ, false);
	}

	/**
	 * <b>Constructor</b>
	 * @param newQueueID
	 * @param copyFrmQ
	 */
	public CQueue(String newQueueID, CQueue<E> copyFrmQ) {
		this(newQueueID, copyFrmQ.getCount(), copyFrmQ, false);
	}

	/**
	 * <b>Constructor</b> 
	 * @param queueID
	 * @param CQueue_Length
	 * @param copyFrmQ
	 * @param test
	 */
	@SuppressWarnings("unchecked")
	public CQueue(String queueID, int CQueue_Length, CQueue<E> copyFrmQ, boolean test) {
		this.CQueue_Length = CQueue_Length;
		this.qID = queueID;
		this.test = test;
		this.arr = (E[]) new Object[this.CQueue_Length + 1];
		if (copyFrmQ != null) {
			this.PushTo = copyFrmQ.getNextPushPos();
			this.AddCount.set(this.PushTo - copyFrmQ.getNextPopPos());
			System.arraycopy(copyFrmQ.getQdata(), copyFrmQ.getNextPopPos(), this.arr, 0,
					this.PushTo - copyFrmQ.getNextPopPos() + 1);
		}
	}

	/**
	 * Rebuilds the current using the input queue
	 *
	 * @param copyFrmQ
	 *            (CQueue<E>)
	 */
	@SuppressWarnings("unchecked")
	public void rebuildFromQ(CQueue<E> copyFrmQ) {

		if (copyFrmQ == null) {
			this.arr = (E[]) new Object[this.CQueue_Length + 1];
			return;
		}
		int newSize = this.arr.length < copyFrmQ.getQsize() ? copyFrmQ.getQsize() + 1 : this.arr.length;
		this.PushTo = copyFrmQ.getCount();
		this.AddCount.set(this.AddCount.intValue() + this.PushTo);
		this.arr = (E[]) new Object[newSize];
		System.arraycopy(copyFrmQ.getQdata(), copyFrmQ.getNextPopPos(), this.arr, 0, this.PushTo + 1);
	}
    /**
     * Returns the ID assigned for the queue
     * @return queue id 
     */
 	public String getQueueID() {
		return this.qID;
	}

 	/**
 	 * Return the total capacity of the queue
 	 * @return
 	 */
	public int getQsize() {
		return this.arr.length;
	}
    /**
     * Returns the position of the next element that will be read
     * @return
     */
	public int getNextPopPos() {
		return this.PopFrom;
	}
    /**
     * Return the next position where the new element will be inserted  
     * @return
     */
	public int getNextPushPos() {
		return this.PushTo;
	}
    /**
     * Returns the current array backing the queue
     * @return
     */
	protected E[] getQdata() {
		return this.arr;
	}
   
	/**
	 * Adds the element to queue when the next push position gets freed.
	 * <br> 
	 * If the next push position is not free, the thread will sleep for sleepMilSec before re-trying
	 * <br>
	 * Control is returned back to the caller after the new element is added to the queue 
	 * @param 
	 * @param sleepMilSec
	 * @throws InterruptedException
	 */
	public void addWhenPossible(E element, int sleepMilSec) throws InterruptedException {
		while (true) {
			if (canAdd()) {
				break;
			}
			// log("AddWait");
			Thread.sleep(sleepMilSec);

		}
		add(element);
	}
    /**
	 * Adds the element to queue when the next push position gets freed.
	 * <br> 
	 * If the next push position is not free, the thread will sleep for sleepMilSec before re-trying
	 * <br>
	 * Control is returned back to the caller after the new element is added to the queue or the retry limit goes beyond the loopLimit
     * @param element
     * @param sleepMilSec
     * @param loopLimit
     * @return true if we managed to add the element to the queue. Else false
     * @throws InterruptedException
     */
	public boolean addWhenPossible(E element, int sleepMilSec, int loopLimit) throws InterruptedException {
		int i = 0;

		while (++i <= loopLimit) {
			if (canAdd()) {
				add(element);
				return true;
			}
			// log("AddWait");
			Thread.sleep(sleepMilSec);
		}
		return false;
	}
    /**
     * Try to add the element to the queue. 
     * @param element
     * @return true if we managed to add the element to the queue. Else false
     */
	public boolean tryAdd(E element) {
		if (canAdd()) {
			add(element);
			return true;
		} else {
			return false;
		}
	}
    /**
     * Add the element to the queue
     * @param e
     */
	protected void add(E e) {
		this.arr[this.PushTo] = e;
		this.PushTo = inc(this.PushTo);
		this.AddCount.incrementAndGet();
		// log("Adding");
	}
    /**
     * Move the index(push/pop) to the next value. 
     * If the next value is beyond the limit of the array, the value is set to0
     * @param inx
     * @return
     */
	protected int inc(int inx) {
		return inx == this.CQueue_Length ? 0 : inx + 1;
	}

	/**
	 * Get the next element when it is available. 
	 * Thread will sleep for sleepInterval (milliseconds)  before re-trying.
	 * <br>
	 * The method will keep trying until we get the data (could go for infinite loop... :( ) 
	 * @param sleepInterval
	 * @return
	 * @throws Exception
	 */
	public E getWhenCan(int sleepInterval) throws Exception {
		while (true) {
			if (canGet()) {
				return getNremove();
			}
			// log("GetWait");
			Thread.sleep(sleepInterval);
		}

	}
    /**
     * Get the next element when it is available. 
	 * Thread will sleep for sleepInterval (milliseconds) before re-trying.
	 * <br>
	 * The method will return the data if it managed to retrieve the data within retry limit of input reTryLimit.
	 * If it the re-try limit breach the max tries, it returns null  
     * @param sleepInterval
     * @param looplimit
     * @return
     * @throws Exception
     */
	public E getWhenCan(int sleepInterval, int reTryLimit) throws Exception {
		int lim = 0;
		while (true) {
			if (canGet()) {
				// log("Getting");
				return getNremove();
			}
			// log("GetWait");
			Thread.sleep(sleepInterval);
			lim++;
			if (lim >= reTryLimit) {
				return null;
			}
		}
	}
    /**
     * Try to get the next element, if available return the element else returns null.
     * @return the next element or null
     * @throws Exception
     */
	public E tryGet() throws Exception {
		if (canGet()) {
			return getNremove();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected E getNremove() throws Exception {
		Object o = this.arr[this.PopFrom];
		this.arr[this.PopFrom] = null;
		this.PopFrom = inc(this.PopFrom);
		this.ReadCount.incrementAndGet();
		// logGet((Object[]) o);
		// log("Get");
		return (E) o;
	}
    /**
     * Check if we can add next element to the queue
     * @return true if we can add, else false
     */
	public boolean canAdd() {
		// log("canAdd() -" + (this.arr[this.PushTo] == null));
		if (this.arr[this.PushTo] == null) {
			return true;
		}
		return false;
	}
    /**
     * Check if we can retrieve next element 
     * @return true if we can get , else false
     */
	public boolean canGet() {
		return !(this.arr[this.PopFrom] == null);
	}

	/**
	 * Check if the producer has flagged that it finished the load process.  
	 * <br> Or <br>
	 * Check if the 
	 * @return
	 */
	public boolean isLoadEnded() {
		return this.LoadEnded.get();
	}
	/**
	 * Check if the consumer has stopped reading from the queue.  
	 * @return
	 */
	public boolean isQReadEnded() {
		return this.LoadEnded.get();
	}

	/**
	 * Return the number of elements available in the queue to retrive 
	 * @return
	 */
	public int getCount() {
		return this.AddCount.get() - this.ReadCount.get();
	}

	/**
	 * Mark that the producer has finished loading 
	 * @param loadEnded
	 */
	public void setLoadEnded(boolean loadEnded) {
		this.LoadEnded.set(loadEnded);
	}
	/**
	 * Mark that consumer has stopped reading from the queue
	 * @param loadEnded
	 */
	public void requstLoadEnd(boolean loadEnded) {
		this.LoadEnded.set(loadEnded);
	}	

	
	protected void log(String from) {
		if (this.test) {
			StringBuilder sb = new StringBuilder(100);
			sb.append(String.format("%15s %15s %15s Push Pos:%5d  Pop pos:%5d In Q:%5d",Thread.currentThread().getName(),this.qID, from,this.PushTo, this.PopFrom, getCount()))
			  .append(" Load_End:").append(this.LoadEnded)
			  .append(" Available:").append(getCount())
			  .append(" Can_Add:").append(this.arr[this.PushTo] == null)
			  .append(" Can_Get:").append(this.arr[this.PopFrom] == null || getCount() == 0);
			System.out.println(sb.toString());
		}
	}

	protected void logGet(Object[] o) {
		StringBuilder b = new StringBuilder();
		b.append(Thread.currentThread().getName())
		 .append(" Get. Push Position: ")  
		 .append(this.PushTo)
		 .append(" Pop position: " )
		 .append(this.PopFrom)
		 .append(" In Q :")
		 .append(getCount())
		 .append(" ");
		 		
		if (this.test) {
			
			for (int i = 0; i < o.length; i++) {
				if (o[0] == null) {
					b.append("<null> , ");
				} else {
					b.append(o[i].toString()).append(" , ");
				}
			}
			b.setLength(b.length() - 3);			
			System.out.println(b.toString());
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getStats() {
		return String.format("%15d", qID) + " Push Pos:" + String.format("%5d", this.PushTo) + " Pop pos:" + String.format("%5d", this.PopFrom)
				+ " In Q:" + String.format("%5d", getCount()) + " Load_End:" + this.LoadEnded + " Can_Add:"
				+ (this.arr[this.PushTo] == null) + " Can_Get:" + !(this.arr[this.PopFrom] == null || getCount() == 0);

	}

	@SuppressWarnings("unchecked")
	public synchronized void flush() {
		this.arr = (E[]) new Object[this.CQueue_Length + 1];
		this.PopFrom = 0;
		this.PushTo = 0;
		this.AddCount.set(0);
		this.ReadCount.set(0);
		this.LoadEnded.set(false);
	}
}
