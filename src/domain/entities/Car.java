package domain.entities;

import domain.events.OnCarParkingTimeout;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public class Car extends Thread {

    int id;
    Optional<OnCarParkingTimeout> onCarParkingTimeout = Optional.empty();

    float parkingWaitingSeconds;

    public Car(int id, float parkingWaitingSeconds) {
        this.id = id;
        this.parkingWaitingSeconds = parkingWaitingSeconds;
    }

    public void park(OnCarParkingTimeout event) {
        this.onCarParkingTimeout = Optional.of(event);
        if(!this.isAlive()) {
            this.start();
        }
    }

    public void run(){
        try {
            Thread.sleep(Math.round(Math.random() * this.parkingWaitingSeconds * 1000));
        } catch (Exception e){
            System.out.println("Erro na Espera do Carro Estacionado " + e.getMessage());
        }
        onCarParkingTimeout.ifPresent(event -> event.onCarParkingTimeout(this));
    }

    @Override
    public String toString() {
        return "Car Id: " + this.id;
    }

    public static class CarQueue {

        Semaphore semaphore = new Semaphore(1);
        LinkedList<Car> stack = new LinkedList<Car>();
        public CarQueue(int carQuantity, float carWaitingSeconds){
            for(int i = 0; i < carQuantity; i++) stack.add(new Car(i + 1, carWaitingSeconds));
        }

        public Car poll() {
            try {
                semaphore.acquire();
                return this.stack.poll();
            }catch (Exception e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
            return null;
        }

        public boolean isEmpty() {
            return this.stack.isEmpty();
        }
    }
}
