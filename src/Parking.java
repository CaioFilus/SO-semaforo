import java.util.ArrayList;
import java.util.List;

public class Parking {

    private Car[][] vacancies = new Car[10][10];

    private List<CarBarrier> barriers;
    public Parking() {
        this.barriers = new ArrayList<>();
    }

    public void addBarrier(CarBarrier carBarrier){
        this.barriers.add(carBarrier);
    }

}
