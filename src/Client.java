import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class Client {

	private JFrame frame;
	private JTextField Nick;
	private JTextField Tekst;
	private JButton Przycisk;
	private Socket socket;
	private PrintStream wysylanie;
	private String wiadomosc;
	private String pseudonim;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client window = new Client();
					window.frame.setVisible(true);
					window.Przycisk.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							try {

								window.wiadomosc = window.Tekst.getText();
								window.pseudonim = window.Nick.getText();
								window.socket = new Socket("192.168.0.154", 14888);
								window.wysylanie = new PrintStream(window.socket.getOutputStream());
								window.wysylanie.println(window.pseudonim + " " + window.wiadomosc);
								window.Tekst.setText("");

							} catch (IOException e) {
								// TODO Auto-generated catch block
								window.Tekst.setText("Koniec Quizu");
								System.out.println("Socket zamkniety");
							}

						}
					});
				} catch (Exception e) {
					System.out.println("Error 2");
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Client() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 220, 191);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		Nick = new JTextField();
		Nick.setHorizontalAlignment(SwingConstants.LEFT);
		Nick.setBounds(10, 39, 116, 20);
		frame.getContentPane().add(Nick);
		Nick.setColumns(10);

		Tekst = new JTextField();
		Tekst.setBounds(10, 81, 116, 38);
		frame.getContentPane().add(Tekst);
		Tekst.setColumns(10);

		Przycisk = new JButton("Wy\u015Blij");
		Przycisk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		Przycisk.setBorder(null);
		Przycisk.setBounds(133, 81, 61, 38);
		frame.getContentPane().add(Przycisk);
		frame.getRootPane().setDefaultButton(Przycisk);

		JLabel lblWiadomo = new JLabel("Wiadomo\u015B\u0107");
		lblWiadomo.setBounds(10, 68, 82, 14);
		frame.getContentPane().add(lblWiadomo);

		JLabel lblPseudonim = new JLabel("Pseudonim");
		lblPseudonim.setBounds(10, 25, 70, 14);
		frame.getContentPane().add(lblPseudonim);
	}
}
