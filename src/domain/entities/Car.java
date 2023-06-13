package domain.entities;

import domain.events.OnCarParkingTimeout;

import java.util.Optional;

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
        this.start();
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
}
