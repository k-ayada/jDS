package pub.ayada.dataStructures.queues;

import java.util.concurrent.locks.ReentrantLock;

public class CQueue1toN<E> extends CQueue<E>{
	private static final long serialVersionUID = -5869870347872720634L;
	final ReentrantLock lock= new ReentrantLock();

	public CQueue1toN(int CQueue_Length) {
		super(CQueue_Length);
	}

	public CQueue1toN(int CQueue_Length, boolean test) {
		super(CQueue_Length, test);
	}
	
	public CQueue1toN(int CQueue_Length, CQueue<E> clone) {
		super(CQueue_Length, clone);
	}		

    @SuppressWarnings("unchecked")
	protected synchronized E getNremove() throws Exception {
   // 	try {
   // 		 this.lock.tryLock();
    		 Object o = this.arr[this.PopFrom];
    		 this.arr[this.PopFrom]=null;
    		//log("Poped.    -");
    		 this.PopFrom = inc(this.PopFrom);
    		 this.GetCount.incrementAndGet();
    		 return (E) o;
  //  	}
  //  	finally { this.lock.unlock();}
    }

}
