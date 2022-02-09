package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;

public class Controller{
    @FXML
    private Text timerText;
    @FXML
    private TextField clientsTextField;
    @FXML
    private TextField queuesTextField;
    @FXML
    private TextField simTimeTextField;
    @FXML
    private TextField ATMinTextField;
    @FXML
    private TextField ATMaxTextField;
    @FXML
    private TextField STMinTextField;
    @FXML
    private TextField STMaxTextField;
    @FXML
    private Button startButton;
    @FXML
    private Text errorText;
    @FXML
    private Text outputText;

    private SimulationManager sim;

    int simTime;

    @FXML
    // validate input and start simulation
    void startSimulation() {
        try {
            int n = Integer.parseInt(clientsTextField.getText());
            int q = Integer.parseInt(queuesTextField.getText());
            simTime = Integer.parseInt(simTimeTextField.getText());
            int ATMin = Integer.parseInt(ATMinTextField.getText());
            int ATMax = Integer.parseInt(ATMaxTextField.getText());
            if (ATMin >= ATMax) {
                errorText.setText("Please input the minimum arrival time to be smaller than the maximum arrival time");
                return;
            }
            int STMin = Integer.parseInt(STMinTextField.getText());
            int STMax = Integer.parseInt(STMaxTextField.getText());
            if (STMin >= STMax) {
                errorText.setText("Please input the minimum service time to be smaller than the maximum service time");
                return;
            }
            sim = new SimulationManager(n, q, simTime, ATMin, ATMax, STMin, STMax);
            sim.start();
            startTimer();
        } catch (Exception e) {
            errorText.setText("Please input numbers in all fields and try again!");
        }
    }

    Thread thrd;
    void startTimer(){
        thrd = new Thread(() -> {
            int time = 0;
            while (time<=simTime) {
                timerText.setText(String.valueOf(time));
                //timerText.setText(String.valueOf(sim.out.get(time)));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time++;
            }
        });
        thrd.start();
    }

}
