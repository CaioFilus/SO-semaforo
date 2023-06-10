import java.util.concurrent.Semaphore;

class MyThread extends Thread {
    private SharedResource resource;
    private String name;

    public MyThread(SharedResource resource, String name) {
        this.resource = resource;
        this.name = name;
    }

    public void run() {
        resource.useResource(name);
    }
}