package domain.entities;

import domain.entities.Car;

import java.util.*;

public class CarBarrier extends Thread {

    int id;
    Parking parking;
    Queue<Car> awaitingCars = new LinkedList<>();
    Queue<Car> availableCars = new LinkedList<>();
    float newIncomeCarsSeconds;


    public CarBarrier(Queue<Car> availableCars, Parking parking, int id,float newIncomeCarsSeconds){
        this.availableCars = availableCars;
        this.newIncomeCarsSeconds = newIncomeCarsSeconds;
        this.id = id;
        this.parking = parking;
    }

    @Override
    public void run() {
        super.run();
        Optional<Car> maybeCar = Optional.ofNullable(this.availableCars.poll());
        while(maybeCar.isPresent()) {
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
