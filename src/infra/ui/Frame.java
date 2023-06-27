package infra.ui;

import domain.entities.Car;
import domain.entities.CarBarrier;
import domain.entities.Parking;
import domain.events.OnParkingChange;
import infra.ui.components.ImagePanel;
import infra.ui.components.Input;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Optional;

public class Frame extends JFrame implements OnParkingChange {

    static final int CAR_BARRIER_QQT = 20;
    static final int CAR_QQT = 10000;
    static final float CAR_WAITING_SECONDS = 2;
    private Parking parking = new Parking();

    private JPanel parkingPanel = new JPanel(new GridLayout(4, 4));
    private Input carArriveTimeInput;
    private Input carWaitingTimeInput;
    private Input carQuantityInput;
    private JButton buttonPlay;
    private JLabel labelTotalCars;
    private JLabel labelTotalWaitingCars;

    private ArrayList<JLabel> parkingLabels = new ArrayList<JLabel>();

    public Frame() {
        this.setTitle("SO Semaforos");
        this.setPreferredSize(new Dimension(1000, 1000));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        Optional<Car>[][] vacancies = parking.getVacancies();
        JPanel gridPanel = new JPanel(new GridLayout(10, 10));
        for (Optional<Car>[] vacanciesLine: vacancies) {
            for (Optional<Car> vacancy: vacanciesLine) {
                JLabel label = vacancy.isPresent() ?
                        new ImagePanel(new ImageIcon(getClass().getResource("car.png")).getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT)):
                        new JLabel("");
                label.setPreferredSize(new Dimension(30, 40));
                label.setBorder(BorderFactory.createLineBorder(Color.black));
                parkingLabels.add(label);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                gridPanel.add(label);
            }
        }
        gridPanel.setMaximumSize(new Dimension(500, 500));

        carQuantityInput = new Input("Quantidade de Carros");
        carQuantityInput.setText("10000");
        carArriveTimeInput = new Input("Tempo de Chegada de Carros");
        carArriveTimeInput.setText("2");
        carWaitingTimeInput = new Input("Tempo de Espera dos Carros");
        carWaitingTimeInput.setText("7");
        JPanel header = new JPanel(new GridLayout(1, 0));

        buttonPlay = new JButton("Play");
        buttonPlay.setSize(new Dimension(100, 100));
        buttonPlay.setSize(new Dimension(100, 100));
        buttonPlay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (parking.isRunning) {
                    buttonPlay.setText("Play");
                    parking.stopSimulation();
                } else {
                    buttonPlay.setText("Stop");
                    play();
                }
            }
        });
        header.add(carQuantityInput);
        header.add(carArriveTimeInput);
        header.add(carWaitingTimeInput);
        header.add(buttonPlay);
        header.setMaximumSize(new Dimension(900, 200));

        JPanel parkingContainer = new JPanel();
        parkingContainer.setLayout(new GridLayout());
        parkingContainer.add(gridPanel);

        JPanel statsContainer = new JPanel();
        statsContainer.setLayout(new BoxLayout(statsContainer, BoxLayout.Y_AXIS));
        Border border = BorderFactory.createTitledBorder("Status");
        statsContainer.setBorder(border);

        JLabel totalCarLabel = new JLabel("Total de Carros Estacionados");
        JLabel carWaitingLabel = new JLabel("Total de Carros Esperando para Estacionar");
        this.labelTotalCars = new JLabel("0");
        this.labelTotalWaitingCars = new JLabel("0");
        JPanel totalCarsPanel = new JPanel();
        JPanel totalCarsWaitingPanel = new JPanel();
        totalCarsPanel.add(totalCarLabel);
        totalCarsPanel.add(labelTotalCars);
        totalCarsWaitingPanel.add(carWaitingLabel);
        totalCarsWaitingPanel.add(labelTotalWaitingCars);

        statsContainer.add(totalCarsPanel);
        statsContainer.add(totalCarsWaitingPanel);
        statsContainer.setMaximumSize(new Dimension(400, 400));

        this.add(header, BorderLayout.WEST);
        this.add(gridPanel);
        this.add(parkingContainer);
        this.add(statsContainer);

        this.parking.setOnParkingChangeEventListener(this);
        this.pack();
        this.setVisible(true);
    }

    void play() {
        int carQuantity = Integer.parseInt(this.carQuantityInput.getText());
        float carWaitingSeconds = Float.parseFloat(this.carWaitingTimeInput.getText());
        float carArriveTime = Float.parseFloat(this.carArriveTimeInput.getText());
        this.parking.removeBarriers();

        Car.CarQueue carQueue = new Car.CarQueue(carQuantity, carWaitingSeconds);
        ArrayList<CarBarrier> carBarriers = new ArrayList();
        for(int i = 0; i < CAR_BARRIER_QQT; i++) carBarriers.add(new CarBarrier(carQueue, this.parking, i + 1, carArriveTime));

        for(CarBarrier carBarrier: carBarriers) this.parking.addBarrier(carBarrier);

        this.parking.startWorking();
    }
    void stop() {
        this.parking.stopSimulation();
    }

    void updateParking() {
        Optional<Car>[][] vacancies = parking.getVacancies();
        int i = 0;
        for (Optional<Car>[] vacanciesRow: vacancies) {
            for (Optional<Car> vacancy: vacanciesRow) {
                this.parkingLabels.get(i).setIcon(
                    vacancy.isPresent()
                        ? new ImageIcon(new ImageIcon(getClass().getResource("car.png"))
                            .getImage()
                            .getScaledInstance(20, 20, Image.SCALE_DEFAULT))
                        : null
                    );
                i++;
            }
        }
        this.labelTotalWaitingCars.setText(String.valueOf(this.parking.getStats().awaitingCars));
        this.labelTotalCars.setText(String.valueOf(this.parking.getStats().totalCars));
    }

    @Override
    public void onParkingChange(Parking parking) {
        this.updateParking();
    }

    @Override
    public void onSimulationEnd(Parking parking) {
        this.buttonPlay.setEnabled(true);
    }
}
