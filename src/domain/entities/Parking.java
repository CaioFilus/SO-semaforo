package domain.entities;

import domain.events.OnCarParkingTimeout;
import domain.events.OnParkingChange;
import domain.objectValues.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;

public class Parking implements OnCarParkingTimeout {

    private Optional<OnParkingChange> onParkingChange = Optional.empty();

    public boolean isRunning = false;
    private final Semaphore inSemaphore = new Semaphore(1);
    private final Semaphore outSemaphore = new Semaphore(1);
    private final Optional<Car>[][] vacancies = (Optional<Car>[][]) new Optional<?>[10][10];
    private ParkingStats parkingStats = new ParkingStats();
    private final List<CarBarrier> barriers;
    public Parking() {
        this.barriers = new ArrayList<>();
        for (int i = 0; i < this.vacancies.length; i++) {
            for (int j = 0; j < this.vacancies[i].length; j++) {
                this.vacancies[i][j] = Optional.empty();
            }
        }
    }

    public void addBarrier(CarBarrier carBarrier){
        this.barriers.add(carBarrier);
    }
    public void removeBarriers(){
        this.barriers.clear();
    }
    public ParkingStats getStats(){
        return this.parkingStats;
    }

    public void startWorking() {
        parkingStats.awaitingCars = 0;
        parkingStats.totalCars = 0;
        this.isRunning = true;
        this.barriers.forEach(CarBarrier::start);
    }

    public void stopSimulation() {
        this.isRunning = false;
        this.barriers.forEach(CarBarrier::stopWork);
    }

    void fillVacancy(Car car, CarBarrier carBarrier){
        try {
            if(inSemaphore.hasQueuedThreads()) parkingStats.awaitingCars ++;
            parkingStats.totalCars++;


            inSemaphore.acquire();
            Optional<Coordinate> freeVacancyCoordinate = this.findFreeVacancy();
            while(!freeVacancyCoordinate.isPresent()){
                Thread.sleep(500);
                freeVacancyCoordinate = this.findFreeVacancy();
            }
            Coordinate coordinate = freeVacancyCoordinate.get();
            this.vacancies[coordinate.getX()][coordinate.getY()] = Optional.of(car);
            car.park(this);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            inSemaphore.release();
        }

        this.onParkingChange.ifPresent(event -> event.onParkingChange(this));
    }
    void releaseVacancy(Car car){
        try {
            outSemaphore.acquire();
            Optional<Coordinate> vacancyCoordinate = this.findVacancy(car);
            if(vacancyCoordinate.isPresent()) {
                Coordinate coordinate = vacancyCoordinate.get();
                this.vacancies[coordinate.getX()][coordinate.getY()] = Optional.empty();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            outSemaphore.release();
        }
        this.onParkingChange.ifPresent(event -> event.onParkingChange(this));
        if(this.isSimulationEnd()){
            this.isRunning = false;
            this.onParkingChange.ifPresent(event -> event.onSimulationEnd(this));
        }
    }

    public void setOnParkingChangeEventListener(OnParkingChange listener){
        this.onParkingChange = Optional.of(listener);
    }
    public Optional<Car>[][] getVacancies() {
        return vacancies;
    }

    private Optional<Coordinate> findFreeVacancy(){
        for(int x = 0; x < this.vacancies.length; x++){
            Optional<Car>[] cars = this.vacancies[x];
            for(int y = 0; y < cars.length; y++) {
                Optional<Car> car = cars[y];
                if(!car.isPresent()) return Optional.of(new Coordinate(x, y));
            }
        }
        return Optional.empty();
    }
    private Optional<Coordinate> findVacancy(Car car){
        for(int x = 0; x < this.vacancies.length; x++){
            Optional<Car>[] cars = this.vacancies[x];
            for(int y = 0; y < cars.length; y++) {
                Optional<Car> carVacancy = cars[y];
                if(carVacancy.isPresent()) {
                    if(carVacancy.get() == car) return Optional.of(new Coordinate(x, y));
                }
            }
        }
        return Optional.empty();
    }

    private boolean isSimulationEnd() {
        for(Optional<Car>[] vacanciesRow: this.vacancies){
            for(Optional<Car> vacancy: vacanciesRow) {
                if(vacancy.isPresent()) return false;
            }
        }
        return true;
    }

    @Override
    public void onCarParkingTimeout(Car car) {
        this.releaseVacancy(car);
    }


    public static class ParkingStats {
        public int totalCars = 0;
        public int awaitingCars = 0;
    }

}
