package infra.ui;

import domain.entities.Car;
import domain.entities.CarBarrier;
import domain.entities.Parking;
import domain.events.OnParkingChange;
import infra.ui.components.ImagePanel;
import infra.ui.components.Input;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class Frame extends JFrame implements OnParkingChange {

    static final int CAR_BARRIER_QQT = 20;
    static final int CAR_QQT = 200;

    static final float CAR_WAITING_SECONDS = 2;

    private Parking parking = new Parking();

    private JPanel parkingPanel = new JPanel(new GridLayout(4, 4));

    private ArrayList<JLabel> parkingLabels = new ArrayList<JLabel>();

    public Frame() {
        this.setTitle("SO Semaforos");
        this.setPreferredSize(new Dimension(1000, 1000));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);
        this.setLayout(new FlowLayout ());

        Optional<Car>[][] vacancies = parking.getVacancies();
        JPanel gridPanel = new JPanel(new GridLayout(4, 4));
        for (Optional<Car>[] vacanciesLine: vacancies) {
            for (Optional<Car> vacancy: vacanciesLine) {
                JLabel label = vacancy.isPresent() ?
                        new ImagePanel(new ImageIcon(getClass().getResource("car.png")).getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)):
                        new JLabel("");
                label.setSize(100, 100);
                label.setPreferredSize(new Dimension(50, 50));
                label.setMaximumSize(new Dimension(50, 50));
                parkingLabels.add(label);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                gridPanel.add(label);
            }
        }
        gridPanel.setMaximumSize(new Dimension(200, 200));
        gridPanel.setSize(new Dimension(100, 100));
        gridPanel.setPreferredSize(new Dimension(100, 100));




        Input carArriveTime = new Input("Tempo de Chegada de Carros");
        Input carWaitingTime = new Input("Tempo de Espera dos Carros");
        JPanel header = new JPanel(new GridLayout(1, 0));

        JButton playButton = new JButton("Play");
        playButton.setSize(new Dimension(100, 100));
        playButton.setSize(new Dimension(100, 100));
        playButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                play();
                playButton.setEnabled(false);
            }
        });
        header.add(carArriveTime);
        header.add(carWaitingTime);
        header.add(playButton);
        header.setMaximumSize(new Dimension(50, 500));

        this.add(header, BorderLayout.WEST);
        this.add(gridPanel, BorderLayout.CENTER);

        this.parking.setOnParkingChangeEventListener(this);
    }

    void play() {
        Queue<Car> availableCars = new LinkedList<Car>();
        for(int i = 0; i < CAR_QQT; i++) availableCars.add(new Car(i + 1, CAR_WAITING_SECONDS));

        ArrayList<CarBarrier> carBarriers = new ArrayList<CarBarrier>();
        for(int i = 0; i < CAR_BARRIER_QQT; i++) carBarriers.add(new CarBarrier(availableCars, this.parking, i + 1, 2));


        for(CarBarrier carBarrier: carBarriers) this.parking.addBarrier(carBarrier);

        this.parking.startWorking();
    }


    void updateParking() {
        Optional<Car>[][] vacancies = parking.getVacancies();
        int i = 0;
        for (Optional<Car>[] vacanciesRow: vacancies) {
            for (Optional<Car> vacancy: vacanciesRow) {
                this.parkingLabels.get(i).setIcon(
                    vacancy.isPresent()
                        ? new ImageIcon(new ImageIcon(getClass().getResource("car.png")).getImage().getScaledInstance(20, 20, Image.SCALE_DEFAULT))
                        : null
                    );
                i++;
            }
        }
    }

    @Override
    public void onParkingChange(Parking parking) {
        this.updateParking();
    }
}
