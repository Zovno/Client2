import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;

public class AccountApp {
    private int accountBalance = 0;
    private JLabel balanceLabel;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public AccountApp() {
        JFrame frame = new JFrame("Клиент 2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 400);

        balanceLabel = new JLabel("Баланс: " + accountBalance, JLabel.CENTER);

        JButton updateButton = new JButton("Обновить счет");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //sendMessage("3"); // Отправляем "3" серверу
            }
        });

        JButton depositButton = new JButton("Пополнить");
        depositButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                accountBalance += 10;
                updateBalance();
                sendMessage("1"); // Отправляем "1" серверу
            }
        });

        JButton withdrawButton = new JButton("Снять");
        withdrawButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                accountBalance -= 10;
                updateBalance();
                sendMessage("2"); // Отправляем "2" серверу
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));
        panel.add(balanceLabel);
        panel.add(updateButton);
        panel.add(depositButton);
        panel.add(withdrawButton);

        frame.add(panel);
        frame.setVisible(true);

        try {
            socket = new Socket("127.0.0.1", 1234);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Обработчик события для кнопки "Обновить счет"
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage("3"); // Отправляем "3" серверу
                // Ждем ответ от сервера и обрабатываем его
                try {
                    String response = in.readLine();
                    int receivedNumber = Integer.parseInt(response);
                    accountBalance = receivedNumber;
                    updateBalance();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void sendMessage(String message) {
        out.println(message);
    }

    private void updateBalance() {
        balanceLabel.setText("Баланс: " + accountBalance);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new AccountApp();
            }
        });
    }
}