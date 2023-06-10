import java.util.*;

public class CarBarrier extends Thread {

    Queue<Car> awaitingCars = new PriorityQueue<>();
    Queue<Car> availableCars = new PriorityQueue<>();



    public CarBarrier(Queue<Car> availableCars){
        this.availableCars = availableCars;
    }

    @Override
    public void run() {
        super.run();
        while(true) {

        }

    }
}
