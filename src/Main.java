public class Main {

    public static void main(String args[]){
        SharedResource resource = new SharedResource(); // Number of permits

        Thread thread1 = new MyThread(resource, "Thread 1");
        Thread thread2 = new MyThread(resource, "Thread 2");
        Thread thread3 = new MyThread(resource, "Thread 3");

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
