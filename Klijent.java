import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Klijent {
    private static final int PORT = 7777;                                                                                       // Port je konstanta, mora da bude isti za klijent i server
    public static void main(String[] args) throws IOException {
        PrintWriter out = null;                                                                                                 // PrintWriter je klasa koja nam omogucava da upisujemo podatke u socket za komunikaciju sa serverom
        BufferedReader in = null;                                                                                              // BufferedReader je klasa pomocu koje citamo podatke sa socketa
        if (args.length != 1)                                                                                                  // U slucaju da nemamo dovoljno argumenata, korisniku ispisuje gresku i format kako treba pozivati komandu
        {
           System.out.println("poziv: java Klijent [hostname udaljenog servera]");
           System.exit(0);
       }
        InetAddress address = InetAddress.getByName(args[0]);                                                                 // address je objekat u koji skladistimo IP adresu unetu kao argument (adresa servera)
        Socket socket = new Socket(address, PORT);                                                                           // Saljemo zahtev za konekciju serveru sa adresom i portom
        try
        {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);      // Definisemo objekat out kao citac iz soketa
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));                                        // Preko objekta in saljemo podatke u soket
            Scanner sc = new Scanner(System.in);                                                                            // Koristi se za citanje sa standardnog ulaza
            String fromServer, toServer;
            while (true) {
                toServer = sc.nextLine();                                                                                  // Blokirajuca funkcija koja ucitava citavu liniju (komandu) sa standardnog ulaza
                if (toServer != null)
                    out.println(toServer);                                                                                 // Saljemo komandu serveru
                fromServer = in.readLine();                                                                                // Prihvatamo odgovor servera u string
                if(fromServer == null) break;                                                                              // Ako je konekcija prekinuta, fromServer ce dobiti vrednost null i
                                                                                                                           // tada mozemo prekinuti beskonacnu petlju kako bi zatvorili sve veze sa serverom
                System.out.print(fromServer.replace('\t', '\n'));                                          // Citamo odgovor servera (na serveru je napravljeno da salje \t umesto \n kako bi odgovor stao u jednu liniju.
                                                                                                                          // Ovde to mozemo zameniti zbog boljeg ispisa
            }
        } finally {                                                                                                       // Nakon zavrsetka komunikacije, zatvaramo soket i strimove za prenos podataka u duplex rezimu
            socket.close();
            in.close();
            out.close();
        }
    }
}
