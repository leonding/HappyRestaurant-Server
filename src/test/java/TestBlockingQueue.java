import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TestBlockingQueue {
    static public void main(String[] args){
        (new TestBlockingQueue()).test1();
    }

    private void test1(){
        BlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>();

        Thread thread1 = new Thread(()->{
            for(int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                blockingQueue.offer(i);
            }
        });

        Thread thread2 = new Thread(() ->{
           for(int i = 10; i < 20; i++) {
               try {
                 Thread.sleep(1000);
               }  catch ( Exception e) {
                   e.printStackTrace();
               }
               blockingQueue.offer(i);
           }
        });

        Thread thread3 = new Thread(()->{
            while (true) {
                try {
                    Integer val = blockingQueue.take();
                    System.out.println("获取数值 " + val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void test2(){
        MyExcutorService es = new MyExcutorService();

        Thread thread1 = new Thread(()->{
            for(int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                final int val = i;
                es.submit(()->{
                    System.out.println("取出数值 " + val);
                });
            }
        });

        Thread thread2 = new Thread(() ->{
            for(int i = 10; i < 20; i++) {
                try {
                    Thread.sleep(1000);
                }  catch ( Exception e) {
                    e.printStackTrace();
                }
                final int val = i;
                es.submit(()->{
                    System.out.println("取出数值 " + val);
                });
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MyExcutorService {
        private final BlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<>();
        private final Thread _thread;

        MyExcutorService(){
            this._thread = new Thread(()->{
                while (true) {
                    try {
                        Runnable r = blockingQueue.take();
                        if(null != r) {
                            r.run();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            this._thread.start();
        }

        public void submit(Runnable val){
            if(null != val){
                this.blockingQueue.offer(val);
            }
        }

    }

}
