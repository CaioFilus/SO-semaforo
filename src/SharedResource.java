import java.util.concurrent.Semaphore;

class SharedResource {
    private Semaphore semaphore;

    public SharedResource(int permits) {
        semaphore = new Semaphore(permits);
    }

    public SharedResource() {
        semaphore = new Semaphore(1);
    }


    public void useResource(String threadName) {
        try {
            System.out.println(threadName + " is waiting to access the shared resource.");
            semaphore.acquire();
            System.out.println(threadName + " has acquired the shared resource.");
            Thread.sleep(2000); // Simulating some work
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println(threadName + " has released the shared resource.");
            semaphore.release();
        }
    }
}