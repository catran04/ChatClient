import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * @author Alexey Androsov
 * created by 25.06.2017
 */
public class ChatClient extends JFrame implements ActionListener, Constants {

    final String TITLE_OF_PROGRAM = "Chat client";
    final String TITLE_BTN_ENTER = "Enter";
    final int START_LOCATION = 200;
    final int WINDOW_WIDTH = 350;
    final int WINDOW_HEIGHT = 450;

    JTextArea dialogue; // area for dialog
    JTextField command; // field for entering commands

    Socket socket;
    PrintWriter writer;
    BufferedReader reader;

    public static void main(String[] args) {
        new ChatClient();
    }

    /**
     * Constructor:
     * Creating a window and all the necessary elements on it
     */
    ChatClient() {
        setTitle(TITLE_OF_PROGRAM);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(START_LOCATION, START_LOCATION, WINDOW_WIDTH, WINDOW_HEIGHT);
        addWindowListener(new WindowListener() {
            public void windowClosing(WindowEvent event) {
                try {
                    writer.println(EXIT_COMMAND);
                    writer.flush();
                    socket.close();
                } catch (Exception ex) {}
            }
            public void windowDeactivated(WindowEvent event) {}
            public void windowActivated(WindowEvent event) {}
            public void windowDeiconified(WindowEvent event) {}
            public void windowIconified(WindowEvent event) {}
            public void windowClosed(WindowEvent event) {}
            public void windowOpened(WindowEvent event) {}
        });
        // area for dialog
        dialogue = new JTextArea();
        dialogue.setLineWrap(true);
        dialogue.setEditable(false);
        JScrollPane scrollBar = new JScrollPane(dialogue);
        // panel for connamd field and button
        JPanel bp = new JPanel();
        bp.setLayout(new BoxLayout(bp, BoxLayout.X_AXIS));
        command = new JTextField();
        command.addActionListener(this);
        JButton enter = new JButton(TITLE_BTN_ENTER);
        enter.addActionListener(this);
        // adding all elements to the window
        bp.add(command);
        bp.add(enter);
        add(BorderLayout.CENTER, scrollBar);
        add(BorderLayout.SOUTH, bp);
        setVisible(true);
        // connect to server
        Connect();
    }

    void Connect() {
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(new ServerListener()).start();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * ServerListener: get messages from Server
     */
    class ServerListener implements Runnable {
        String message;

        public void run() {
            try {
                while ((message = reader.readLine()) != null) {
                    if (!message.equals("\0"))
                        dialogue.append(message + "\n");
                    if (message.equals(AUTH_FAIL))
                        System.exit(-1); // terminate client
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    /**
     * Listener of events from menu, command field and enter button
     */

    public void actionPerformed(ActionEvent event) {
        if (command.getText().trim().length() > 0) {
            writer.println(command.getText());
            writer.flush();
            command.setText("");
        }
        command.requestFocusInWindow();
    }
}

