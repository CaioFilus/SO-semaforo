package domain.entities;

import java.util.*;

public class CarBarrier extends Thread {

    int id;
    Parking parking;
    Queue<Car> awaitingCars = new LinkedList<>();
    Car.CarQueue availableCars;
    float newIncomeCarsSeconds;

    boolean isRunning;


    public CarBarrier(Car.CarQueue availableCars, Parking parking, int id, float newIncomeCarsSeconds){
        this.availableCars = availableCars;
        this.newIncomeCarsSeconds = newIncomeCarsSeconds;
        this.id = id;
        this.parking = parking;
    }


    public void stopWork() {
        this.isRunning = false;
    }
    @Override
    public void run() {
        super.run();
        this.isRunning = true;
        Optional<Car> maybeCar = Optional.ofNullable(this.availableCars.poll());
        while(maybeCar.isPresent() && this.isRunning) {
            Car car = maybeCar.get();
            try {
                Thread.sleep(Math.round(Math.random() * this.newIncomeCarsSeconds * 1000));
                //System.out.println("Novo carro chegou na cancela " +  this.id);
                this.awaitingCars.add(car);
                this.parking.fillVacancy(car, this);
                this.awaitingCars.remove();
            } catch (Exception e) {
                System.out.println("Erro na Thread de Barrier " + e);
                e.printStackTrace();
            }

            maybeCar = Optional.ofNullable(this.availableCars.poll());
        }
    }

    @Override
    public String toString() {
        return "Barrier Id: " + this.id;
    }
}
