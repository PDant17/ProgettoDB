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
                "select count(*) from scuderia where nome = ?;",
                "select count(*) from vettura where numero_di_gara = ?;",
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
            	ResultSet resultSet;
            	
            	String newValue;
            	String newValue2;
            	String col1Value;
            	String col2Value;
            	String col3Value;
            	String col4Value;
            	String col5Value;
            	String col6Value;
            	String col7Value;
            	String col8Value;
            	String query2;
            	

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
                        resultSet = statement.executeQuery(query);
                        displayResultSet(resultSet);
                        break;

                    case "insert into scuderia (nome, sede, finanziamenti_totali) values (?, ?, ?);":
                        col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il nome:");
                        col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la sede:");
                        col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per i finanziamenti totali:");

                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                            preparedStatement.setString(1, col1Value);
                            preparedStatement.setString(2, col2Value);
                            preparedStatement.setString(3, col3Value);
                            preparedStatement.executeUpdate();
                            resultArea.setText("Inserimento eseguito con successo!");
                        }
                        break;

                    case "select count(*) from scuderia where nome = ?;":
                        newValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il nome della scuderia:");
                        
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    		preparedStatement.setString(1, newValue);
                    		resultSet = preparedStatement.executeQuery();
                    		if(handleResultSet(resultSet, "count")) {
                            	query2 = "insert into vettura (numero_di_gara, modello, nome_scuderia) values (?, ?, ?);";
                            	
                            	col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il numero di gara:");
                                col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il modello:");
                            	
                            	try (PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                            		preparedStatement2.setString(1, col1Value);
                            		preparedStatement2.setString(2, col2Value);
                            		preparedStatement2.setString(3, newValue);
                            		preparedStatement2.executeUpdate();
                            		resultArea.setText("Inserimento eseguito con successo!");
                            	}
                            }
                            else {
                            	JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato", "Errore", JOptionPane.ERROR_MESSAGE);
                            }
                    	}
                        break;
                        
                    case "select count(*) from vettura where numero_di_gara = ?;":
                    	newValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il numero di gara della vettura:");
                    	
                    	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    		preparedStatement.setString(1, newValue);
                    		resultSet = preparedStatement.executeQuery();
                    		if(handleResultSet(resultSet, "count")) {
                    			newValue2 = JOptionPane.showInputDialog(WECApp.this, "Inserisci il tipo di pilota [pro, am, gd]");
                    			
                    			if(newValue2.equals("gd")) {
                    				query2 = "select count(*) from vettura as v inner join pilota as p on v.numero_di_gara = p.numero_vettura where v.numero_di_gara = " + newValue + " and not exists (select pil.codice_pilota from vettura as ve inner join pilota as pil on ve.numero_di_gara = pil.numero_vettura inner join gentleman_driver on pil.codice_pilota = gentleman_driver.codice_pilota  where v.numero_di_gara = ve.numero_di_gara and p.codice_pilota = pil.codice_pilota);";
                    				resultSet = statement.executeQuery(query2);
                    				if(!handleResultSet(resultSet, "count")) {
                    					JOptionPane.showMessageDialog(WECApp.this, "Vincolo violato [Almeno un pilota non gentleman driver nell'equipaggio]", "Errore", JOptionPane.ERROR_MESSAGE);
                    					break;
                    				}
                    			}
                    			query2 = "insert into pilota (codice_pilota, nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, numero_vettura) values (?, ?, ?, ?, ?, ?, ?, ?)";
                    				
                    			col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il codice pilota:");
                                col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il nome:");
                                col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il cognome:");
                                col4Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la data di nascita [yyyy-mm-dd]:");
                                col5Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la nazionalità [nazione]:");
                                col6Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il numero di licenze:");
                                if(Integer.parseInt(col6Value) > 1 && (newValue2.equals("am") || newValue2.equals("gd"))) {
                                	JOptionPane.showMessageDialog(WECApp.this, "Vincolo violato [Un am o un gentleman driver possono avere solo una licenza]", "Errore", JOptionPane.ERROR_MESSAGE);
                                	break;
                                }
                                else if(Integer.parseInt(col6Value) == 1 && newValue2.equals("pro")) {
                                	JOptionPane.showMessageDialog(WECApp.this, "Vincolo violato [Un pro deve avere più di una licenza]", "Errore", JOptionPane.ERROR_MESSAGE);
                                	break;
                                }
                                col7Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la data della prima licenza [yyyy-mm-dd]:");
                                    
                                try (PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                                	preparedStatement2.setString(1, col1Value);
                                	preparedStatement2.setString(2, col2Value);
                                	preparedStatement2.setString(3, col3Value);
                                	preparedStatement2.setString(4, col4Value);
                                	preparedStatement2.setString(5, col5Value);
                                	preparedStatement2.setString(6, col6Value);
                                	preparedStatement2.setString(7, col7Value);
                                	preparedStatement2.setString(8, newValue);
                                	preparedStatement2.executeUpdate();
                            		resultArea.setText("Inserimento eseguito con successo!");
                    			}
                                
                                if(newValue2.equals("gd")) {
                                	query2 = "insert into gentleman_driver (codice_gentleman, quota, finanziamenti, codice_pilota ) values (?, ?, ?, ?);";
                                	
                                	col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il codice gentleman:");
                                	col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la quota:");
                                	col4Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per i finanzimenti:");
                                	
                                	try (PreparedStatement preparedStatement2 = connection.prepareStatement(query2)) {
                                    	preparedStatement2.setString(1, col2Value);
                                    	preparedStatement2.setString(2, col3Value);
                                    	preparedStatement2.setString(3, col4Value);
                                    	preparedStatement2.setString(4, col1Value);
                                    	preparedStatement2.executeUpdate();
                        			}
                                	
                                	query2 = "update scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join pilota on vettura.numero_di_gara = pilota.numero_vettura inner join gentleman_driver on pilota.codice_pilota = gentleman_driver.codice_pilota set scuderia.finanziamenti_totali = finanziamenti_totali + " + col4Value + " where gentleman_driver.codice_gentleman = " + col2Value + ";";
                                	statement.executeUpdate(query2);
                                	resultArea.setText("Inserimento e aggiornamento eseguito con successo!");
                                }
                    		}
                    		else {
                    			JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato", "Errore", JOptionPane.ERROR_MESSAGE);
                    		}
                    	}

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
    
    private boolean handleResultSet(ResultSet resultSet, String op) throws SQLException {
    	int n = 0;
    	
    	switch(op) {
		case "count":
			while (resultSet.next()) {
				n = resultSet.getInt(1);
			}
			if(n >= 1)
				return true;
			else
				return false;
 
		default:
			break;
		}
    	return false;
    }

    private void displayResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        StringBuilder resultText = new StringBuilder();

        // Visualizza l'intestazione delle colonne
        for (int i = 1; i <= columnCount; i++) {
            resultText.append(metaData.getColumnName(i)).append("\t\t");
        }
        resultText.append("\n");

        // Visualizza i dati
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                resultText.append(resultSet.getString(i)).append("\t\t");
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