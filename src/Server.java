import java.awt.EventQueue;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.sun.prism.paint.Color;

import java.awt.BorderLayout;
import java.awt.Component;

public class Server {

	private JFrame frame;
	private JScrollPane pane;
	private Socket socket;
	private ServerSocket server_socket;
	private InputStreamReader stream = null;
	private BufferedReader bufor = null;
	private JTextArea textArea;
	private volatile boolean otrzymanie_wiadomosci = false;
	private BlockingQueue<Socket> kolejka;

	private Thread watek; // sprawdzenie polaczenia
	private Thread watek2; // analiza odpowiedzi
	
	private Socket temp; // do tej zmiennej przechowuje obecna odpowiedz z socketa
	private int test = 0; // liczba odpowiedzi
	
	private String[] odbior = null;
	static ArrayList<String> pytania = new ArrayList<String>();
	static ArrayList<String> odpowiedzi = new ArrayList<String>();
	static boolean running = true;
	private int zwieksz_pytanie = 0;
	private int licznik_pytania = 1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Quiz();
	
		} catch (IOException e1) {
			System.out.println("Koniec quizu");
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server window = new Server();
					window.frame.setVisible(true);
					window.kolejka = new ArrayBlockingQueue<Socket>(5);
					window.server_socket = new ServerSocket(14888);
					System.out.println(window.server_socket.getInetAddress());
					window.textArea.setText("::::::::::::::Witamy w Quiz:::::::::::::\n");
					window.textArea.append("Pytanie nr: " + window.licznik_pytania + " "
							+ (pytania.get(window.zwieksz_pytanie)) + "\n\n");
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							// window.wiadomosc = null;

							window.watek = new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										while (running) {

											window.socket = window.server_socket.accept();
											window.test++;
										
											window.kolejka.put(window.socket);
											window.otrzymanie_wiadomosci = true;
										}
									} catch (IOException | InterruptedException e) {
										
										System.out.println("Koniec quizu");
									}

								}
							});
							window.watek2 = new Thread(new Runnable() {

								@Override
								public void run() {
									try {
										while (running) {
											while (window.otrzymanie_wiadomosci == true) {

												window.temp = window.kolejka.take();
												window.stream = new InputStreamReader(window.temp.getInputStream());
												window.bufor = new BufferedReader(window.stream);
												window.odbior = window.bufor.readLine().split("\\s", 2);

												//System.out.println(window.odbior.length);
												// window.wiadomosc = window.bufor.readLine().split("\\s+");
												if (window.odbior[1].toLowerCase()
														.equals(odpowiedzi.get(window.zwieksz_pytanie))) {
													window.textArea.append("::::::::::::::U¿ytkownik: "
															+ window.odbior[0] + ": "
															+ "  udzieli³ poprawnej odpowiedzi: " + "[" + odpowiedzi.get(window.zwieksz_pytanie).toUpperCase()
															+ "]" + "\n");
													
													window.zwieksz_pytanie++;
													window.licznik_pytania++;
													if (window.zwieksz_pytanie == (pytania.size() - 1)) {
														window.zwieksz_pytanie = 0;

														window.textArea.append(
																"::::::::::::::Dziêkujêmy za rozgrywkê :)::::::::::::::\n Liczba ³¹cznie udzielonych odpowiedzi: "
																		+ window.test + "\n");
														///to usprawnic
														Float wynik = (float) ((pytania.size()-1)%(window.licznik_pytania)*100);
														String wynik1 = wynik.toString();
														window.textArea.append("Procentowe poprawne odpowiedzi: " + wynik1 + "%");
														running = false;
														window.server_socket.close();
														window.temp.close();
														window.stream.close();
														window.bufor.close();
														window.odbior.clone();

													}
													else {window.textArea.append("Pytanie nr: " + window.licznik_pytania + " "
															+ (pytania.get(window.zwieksz_pytanie)) + "\n\n");}

													

												} 
												
												else {
													window.textArea.append("U¿ytkownik: " + window.odbior[0]
															+ "  wpisa³ b³êdn¹ odpowiedz\n\n");

												}
												window.otrzymanie_wiadomosci = false;
											}

										}
									} catch (IOException | InterruptedException e) {
										// TODO Auto-generated catch block
										System.out.println("Koniec quizu");;
									}

								}
							});

							window.watek.start();
							window.watek2.start();

						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}

	public static void Quiz() throws IOException {
		file_stream_pytania();
		file_stream_odpowiedzi();

	}

	public Server() {
		initialize();
	}

	public static void file_stream_pytania() throws IOException {
		FileReader odczyt_pliku = new FileReader("C:\\Users\\oszpu\\OneDrive\\Pulpit\\LAPTOP_DZISIAJ\\Quiz_done\\Quiz\\Pytania.txt");
		String linijka;
		BufferedReader bufor_odczytu = new BufferedReader(odczyt_pliku);

		while ((linijka = bufor_odczytu.readLine()) != null) {
			pytania.add((linijka));
		}
		bufor_odczytu.close();
	}

	public static void file_stream_odpowiedzi() throws IOException {
		FileReader odczyt_pliku = new FileReader("C:\\Users\\oszpu\\OneDrive\\Pulpit\\LAPTOP_DZISIAJ\\Quiz_done\\Quiz\\Odpowiedzi.txt");
		String linijka;
		BufferedReader bufor_odczytu = new BufferedReader(odczyt_pliku);

		while ((linijka = bufor_odczytu.readLine()) != null) {
			odpowiedzi.add((linijka));
		}
		bufor_odczytu.close();

	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		textArea = new JTextArea();
		frame.getContentPane().add(textArea, BorderLayout.CENTER);
		pane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.add(pane);
	}

}
