package pub.ayada.dataStructures.queues;

import java.util.concurrent.locks.ReentrantLock;

public class CQueue1toN<E> extends CQueue<E>{
	private static final long serialVersionUID = -5869870347872720634L;
	final ReentrantLock lock= new ReentrantLock();


	public CQueue1toN(String queueID, int CQueue_Length) {
		super(queueID,CQueue_Length);
	}
	public CQueue1toN(String queueID,int CQueue_Length, boolean testing ) {
		super(queueID,CQueue_Length, testing);
	}
	public CQueue1toN(String queueID,int CQueue_Length, CQueue<E> clone) {
		super(queueID,CQueue_Length, clone);
	}	
	public CQueue1toN(String newQueueID,CQueue<E> clone) {
		super(newQueueID,clone);
	}	

	

    @SuppressWarnings("unchecked")
	protected synchronized E getNremove() throws Exception {
   // 	try {
   // 		 this.lock.tryLock();
    		 Object o = this.arr[this.PopFrom];
    		 this.arr[this.PopFrom]=null;
    		//log("Poped.    -");
    		 this.PopFrom = inc(this.PopFrom);
    		 this.ReadCount.incrementAndGet();
    		 return (E) o;
  //  	}
  //  	finally { this.lock.unlock();}
    }

}
