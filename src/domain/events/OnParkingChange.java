package domain.events;

import domain.entities.Parking;

public interface OnParkingChange {
    void onParkingChange(Parking parking);
}
