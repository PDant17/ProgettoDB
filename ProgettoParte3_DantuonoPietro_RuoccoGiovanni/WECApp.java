/* import java.sql.*;
import java.util.*;

public class WECApp {
    public static void main(String[] arg) {
        Scanner input = new Scanner(System.in);
        System.out.println("Inserisci la spesa totale attesa");
        int x = Integer.parseInt(input.nextLine());
        executeEsercizio(x);
    }

    public static void executeEsercizio(int x) {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/condominio"
                    + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String username = "<username>";
            String pwd = "<pwd>";
            con = DriverManager.getConnection(url, username, pwd);
        } catch (Exception e) {
            System.out.println("Connessione fallita!!!");
        }
        try {
            String query = "SELECT condominio, scala, interno "
                    + "FROM rif_appartamento JOIN spesa ON rif_appartamento.spesa = spesa.codice "
                    + "GROUP BY condominio, scala, interno "
                    + "HAVING SUM(importo) > " + x;

            Statement pquery = con.createStatement();
            ResultSet result = pquery.executeQuery(query);

            System.out.println("Ecco i condomini che hanno pagato più di " + x + " euro per spese");
            while (result.next()) {
                String condominio = result.getString("condominio");
                String scala = result.getString("scala");
                int interno = result.getInt("interno");
                System.out.println(condominio + "\t" + scala + "\t" + interno);
            }
        } catch (Exception e) {
            System.out.println("Errore nell'interrogazione");
        }
    }
}
*/

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class WECApp extends JFrame {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/campionato";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "3621123";

    private JTextArea resultArea;

    public WECApp() {
        super("Database Query GUI");
        setLayout(new BorderLayout());

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 3));

        // Definisci le tue query qui
        String[] queries = {
                "insert into scuderia (nome, sede, finanziamenti_totali) values (?, ?, ?);",
                "select nome from scuderia;",
                "select count(*) as pro from vettura inner join pilota on vettura.numero_di_gara = pilota.numero_vettura where vettura.numero_di_gara = 103 and pilota.licenze > 1;",
                "select count(*) as gentleman_driver from gentleman_driver where codice_gentleman = ?;",
                "select count(*) as vetture from vettura where numero_di_gara = ?;",
                "select count(*) as totale from iscrizione where nome_gara = ?;",
                "select count(*) from composizione inner join componente on composizione.codice_componente = componente.codice_componente left join motore on motore.codice_componente = componente.codice_componente left join cambio on cambio.codice_componente = componente.codice_componente left join telaio on telaio.codice_componente = componente.codice_componente where numero_vettura = ? and composizione.codice_componente = ?;",
                "select nome, finanziamenti_totali from scuderia;",
                "select nome, finanziamenti_totali from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura group by nome, finanziamenti_totali;",
                "select p.codice_pilota, p.nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, p.numero_vettura, codice_gentleman, quota, finanziamenti from pilota p left join gentleman_driver on p.codice_pilota = gentleman_driver.codice_pilota inner join vettura on p.numero_vettura = vettura.numero_di_gara inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura inner join gara g on iscrizione.nome_gara = g.nome inner join circuito c on g.nome_circuito = c.nome where iscrizione.punteggio = 25 and p.nazionalità = c.paese group by p.codice_pilota, p.nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, p.numero_vettura, codice_gentleman, quota, finanziamenti;",
                "select scuderia.nome, vettura.numero_di_gara, 100/count(pilota.codice_pilota)*count(gentleman_driver.codice_pilota) as percentuale from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join pilota on vettura.numero_di_gara =  pilota.numero_vettura left join gentleman_driver on pilota.codice_pilota = gentleman_driver.codice_pilota group by vettura.numero_di_gara;",
                "select * from costruttore;",
                "select vettura.numero_di_gara, sum(iscrizione.punteggio) as punteggio_finale from vettura inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura group by vettura.numero_di_gara;",
                "select motore.tipo, sum(iscrizione.punteggio) as punteggio_finale from motore inner join componente on motore.codice_componente = componente.codice_componente inner join composizione on componente.codice_componente = composizione.codice_componente inner join vettura on composizione.numero_vettura = vettura.numero_di_gara inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura group by motore.codice_motore;",
                "select scuderia.nome, vettura.numero_di_gara, sum(iscrizione.punteggio)/sum(round(time_to_sec(gara.durata)/60)) as rapporto from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura inner join gara on iscrizione.nome_gara = gara.nome group by scuderia.nome, vettura.numero_di_gara;"               
                // "INSERT INTO tua_tabella1 (col1, col2) VALUES (?, ?)",
                // "UPDATE tua_tabella2 SET col1 = ? WHERE col2 = ?"
                // Aggiungi le tue query personalizzate qui
        };
        
        int i = 0;
        for (String query : queries) {
        	i++;
            JButton button = new JButton("Numero " + i);
            button.addActionListener(new QueryButtonListener(query));
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class QueryButtonListener implements ActionListener {
        private String query;

        public QueryButtonListener(String query) {
            this.query = query;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try (Connection connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
                 Statement statement = connection.createStatement()) {


                switch (query) {
                	//case "insert into scuderia (nome, sede, finanziamenti_totali) values (?, ?, ?);":
                	//case "select nome from scuderia;":
                	//case "select count(*) as pro from vettura inner join pilota on vettura.numero_di_gara = pilota.numero_vettura where vettura.numero_di_gara = 103 and pilota.licenze > 1;":
                	//case "select count(*) as gentleman_driver from gentleman_driver where codice_gentleman = ?;":
                	//case "select count(*) as vetture from vettura where numero_di_gara = ?;":
                	//case "select count(*) as totale from iscrizione where nome_gara = ?;":
                	//case "select count(*) from composizione inner join componente on composizione.codice_componente = componente.codice_componente left join motore on motore.codice_componente = componente.codice_componente left join cambio on cambio.codice_componente = componente.codice_componente left join telaio on telaio.codice_componente = componente.codice_componente where numero_vettura = ? and composizione.codice_componente = ?;":
                	//case "select nome, finanziamenti_totali from scuderia;":
                	case "select nome, finanziamenti_totali from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura group by nome, finanziamenti_totali;":
                	case "select p.codice_pilota, p.nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, p.numero_vettura, codice_gentleman, quota, finanziamenti from pilota p left join gentleman_driver on p.codice_pilota = gentleman_driver.codice_pilota inner join vettura on p.numero_vettura = vettura.numero_di_gara inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura inner join gara g on iscrizione.nome_gara = g.nome inner join circuito c on g.nome_circuito = c.nome where iscrizione.punteggio = 25 and p.nazionalità = c.paese group by p.codice_pilota, p.nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, p.numero_vettura, codice_gentleman, quota, finanziamenti;":
                	case "select scuderia.nome, vettura.numero_di_gara, 100/count(pilota.codice_pilota)*count(gentleman_driver.codice_pilota) as percentuale from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join pilota on vettura.numero_di_gara =  pilota.numero_vettura left join gentleman_driver on pilota.codice_pilota = gentleman_driver.codice_pilota group by vettura.numero_di_gara;":
                	case "select * from costruttore;":
                	case "select vettura.numero_di_gara, sum(iscrizione.punteggio) as punteggio_finale from vettura inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura group by vettura.numero_di_gara;":
                	case "select motore.tipo, sum(iscrizione.punteggio) as punteggio_finale from motore inner join componente on motore.codice_componente = componente.codice_componente inner join composizione on componente.codice_componente = composizione.codice_componente inner join vettura on composizione.numero_vettura = vettura.numero_di_gara inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura group by motore.codice_motore;":
                	case "select scuderia.nome, vettura.numero_di_gara, sum(iscrizione.punteggio)/sum(round(time_to_sec(gara.durata)/60)) as rapporto from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura inner join gara on iscrizione.nome_gara = gara.nome group by scuderia.nome, vettura.numero_di_gara;":               
                    //Logica per le query di selezione
                        ResultSet resultSet = statement.executeQuery(query);
                        displayResultSet(resultSet);
                        break;

                    case "insert into scuderia (nome, sede, finanziamenti_totali) values (?, ?, ?);":
                        // Logica per l'inserimento
                        String col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il nome:");
                        String col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la sede:");
                        String col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per i finanziamenti totali:");

                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                            preparedStatement.setString(1, col1Value);
                            preparedStatement.setString(2, col2Value);
                            preparedStatement.setString(3, col3Value);
                            preparedStatement.executeUpdate();
                            resultArea.setText("Inserimento eseguito con successo!");
                        }
                        break;

                    case "UPDATE tua_tabella2 SET col1 = ? WHERE col2 = ?":
                        // Logica per l'aggiornamento
                        String newValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il nuovo valore per col1:");
                        String conditionValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il valore per la condizione (col2):");

                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                            preparedStatement.setString(1, newValue);
                            preparedStatement.setString(2, conditionValue);
                            preparedStatement.executeUpdate();
                            resultArea.setText("Aggiornamento eseguito con successo!");
                        }
                        break;

                    default:
                        // Gestione per altre query personalizzate
                        break;
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(WECApp.this, "Errore nell'esecuzione della query", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void displayResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        StringBuilder resultText = new StringBuilder();

        // Visualizza l'intestazione delle colonne
        for (int i = 1; i <= columnCount; i++) {
            resultText.append(metaData.getColumnName(i)).append("\t");
        }
        resultText.append("\n");

        // Visualizza i dati
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                resultText.append(resultSet.getString(i)).append("\t");
            }
            resultText.append("\n");
        }

        resultArea.setText(resultText.toString());
    }

    public static void main(String[] args) {
        // Esegui l'applicazione su un thread separato per evitare blocchi dell'interfaccia utente
        SwingUtilities.invokeLater(() -> new WECApp());
    }
}