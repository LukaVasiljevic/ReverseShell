import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    public static final int PORT = 7777;                                                                            // Port je konstanta, mora da bude isti za klijent i server
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);                                                         // Otvara socket
        BufferedReader in = null;
        PrintWriter out = null;
        Process p = null;                                                                                           // Proces koji zapravo izvrsava shell komande
        String izlaz = null, s = null;                                                                              // Stringovi za uzimanje proces outputa i formatiranje u oblik kakav nama treba za klijenta
        System.out.println("slusam na portu " + PORT);
        while (true) {
            Socket socket = serverSocket.accept();                                                                  // Blokira se do klijentskog zahteva za konekcijom
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                Scanner sc = new Scanner(System.in);
                String fromClient;
                System.out.println("zakacio se klijent " + socket.getInetAddress().toString());

                while (true) {
                    fromClient = in.readLine();                                                                     // Uzima komandu od klijenta
                    if (fromClient == null) break;
                    System.out.println("izvrsavam: " + fromClient);

                    ProcessBuilder processBuilder = new ProcessBuilder();
                    processBuilder.command("/bin/sh", "-c", fromClient);                                            // Prvi argument je link do shella, argument -c znaci da se komanda prihvata u vidu stringa
                                                                                                                    // Treci argument je naredba koja se izvrsava u shellu
                    p = processBuilder.start();                                                                     // Pokreni komandu
                    p.waitFor();                                                                                    // Zaustavlja nit dok se naredba ne zavrsi. Daje povratne vrednosti shodno tome koji je rezultat naredbe
                                                                                                                    // 0 - izvrsena komanda bez problema, != 0 greska
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));        // Kanal za output shell naredbe
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));        // Kanal za error shell naredbe

                    izlaz = "";                                                                                     // Formatiranje stdout i stderr u jedan string i prosledjivanje klijentu
                    s = null;
                    while ((s = stdInput.readLine()) != null) izlaz += s + "\t";
                    while ((s = stdError.readLine()) != null) izlaz += s + "\t";
                    out.println(izlaz);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("zavrsavam konekciju");                                                        // Konekcija s trenutnim klijentom se prekida, i server se vraca u blokirano stanje
                                                                                                                 // gde ceka novog klijenta
                socket.close();
                out.close();
                in.close();
            }
        }
    }
}