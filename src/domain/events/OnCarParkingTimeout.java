package domain.events;

import domain.entities.Car;
import domain.entities.Parking;

public interface OnCarParkingTimeout {
    void onCarParkingTimeout(Car car);
}
