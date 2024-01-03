import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.*;

public class WECApp extends JFrame {
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/campionato";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "3621123";

    private JTextArea resultArea;

    public WECApp() {
        super("WECApp Campionato");
        setLayout(new BorderLayout());

        resultArea = new JTextArea();
        resultArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 3));
        
        for (int i = 1; i <= 15; i++) {
            JButton button = new JButton("Numero " + i);
            button.addActionListener(new QueryButtonListener(i));
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class QueryButtonListener implements ActionListener {
        private int i;

        public QueryButtonListener(int i) {
            this.i = i;
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
            	String query;
            	

                switch (i) {
                	case 8: query = "select nome, finanziamenti_totali from scuderia;";
                		resultSet = statement.executeQuery(query);
                		displayResultSet(resultSet);
                		break;
                	
                	case 9: query = "select nome, finanziamenti_totali from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura group by nome, finanziamenti_totali;";
                		resultSet = statement.executeQuery(query);
                		displayResultSet(resultSet);
                		break;
                	
                	case 10: query = "select p.codice_pilota, p.nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, p.numero_vettura, codice_gentleman, quota, finanziamenti from pilota p left join gentleman_driver on p.codice_pilota = gentleman_driver.codice_pilota inner join vettura on p.numero_vettura = vettura.numero_di_gara inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura inner join gara g on iscrizione.nome_gara = g.nome inner join circuito c on g.nome_circuito = c.nome where iscrizione.punteggio = 25 and p.nazionalità = c.paese group by p.codice_pilota, p.nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, p.numero_vettura, codice_gentleman, quota, finanziamenti;";
                		resultSet = statement.executeQuery(query);
                		displayResultSet(resultSet);
                		break;
                	
                	case 11: query = "select scuderia.nome, vettura.numero_di_gara, 100/count(pilota.codice_pilota)*count(gentleman_driver.codice_pilota) as percentuale from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join pilota on vettura.numero_di_gara =  pilota.numero_vettura left join gentleman_driver on pilota.codice_pilota = gentleman_driver.codice_pilota group by vettura.numero_di_gara;";
                		resultSet = statement.executeQuery(query);
                		displayResultSet(resultSet);
                		break;
                	
                	case 12: query = "select * from costruttore;";
                		resultSet = statement.executeQuery(query);
                		displayResultSet(resultSet);
                		break;
                		
                	case 13: query = "select vettura.numero_di_gara, sum(iscrizione.punteggio) as punteggio_finale from vettura inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura group by vettura.numero_di_gara;";
                		resultSet = statement.executeQuery(query);
                		displayResultSet(resultSet);
                		break;
                		
                	case 14: query = "select motore.tipo, sum(iscrizione.punteggio) as punteggio_finale from motore inner join componente on motore.codice_componente = componente.codice_componente inner join composizione on componente.codice_componente = composizione.codice_componente inner join vettura on composizione.numero_vettura = vettura.numero_di_gara inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura group by motore.codice_motore;";
                		resultSet = statement.executeQuery(query);
                		displayResultSet(resultSet);
                    	break;
                    	
                	case 15: query = "select nome_scuderia, left(avg(media_auto), 10) as media_tra_auto from (select scuderia.nome as nome_scuderia, vettura.numero_di_gara, sum(iscrizione.punteggio) / sum((round(time_to_sec(gara.durata) / 60))) as media_auto from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura inner join gara on iscrizione.nome_gara = gara.nome where not iscrizione.stato_fine_gara = \"nonterminato\" group by scuderia.nome, vettura.numero_di_gara) as medie_per_auto group by nome_scuderia;";
                        resultSet = statement.executeQuery(query);
                        displayResultSet(resultSet);
                        break;

                    case 1:
                    	query = "insert into scuderia (nome, sede, finanziamenti_totali) values (?, ?, ?);";
                        col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il nome:");
                        col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la sede:");
                        col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per i finanziamenti totali:");

                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                            preparedStatement.setString(1, col1Value);
                            preparedStatement.setString(2, col2Value);
                            preparedStatement.setString(3, col3Value);
                            preparedStatement.executeUpdate();
                            resultArea.setText("Inserimento scuderia eseguito con successo!");
                        }
                        break;

                    case 2:
                    	query = "select count(*) from scuderia where nome = ?;";
                    	java.util.List<String> nameValue = new ArrayList<>();
                        newValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il nome della scuderia:");
                        
                        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    		preparedStatement.setString(1, newValue);
                    		resultSet = preparedStatement.executeQuery();
                    		if(handleResultSet(resultSet, "count") == 1) {
                            	query = "insert into vettura (numero_di_gara, modello, nome_scuderia) values (?, ?, ?);";
                            	
                            	col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il numero di gara:");
                                col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il modello:");
                            	
                            	try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                            		preparedStatement2.setString(1, col1Value);
                            		preparedStatement2.setString(2, col2Value);
                            		preparedStatement2.setString(3, newValue);
                            		preparedStatement2.executeUpdate();
                            		resultArea.setText("Inserimento vettura eseguito con successo!");
                            	}
                            	
                            	java.util.List<String> type = new ArrayList<>();
                            	String typeBuffer = "";
                            	java.util.List<String> typeCode = new ArrayList<>();
                            	
                            	col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per nuovi componenti [0, 1, 2, 3]:");
                            	int count = 0;
                            	
                            	int count2 = 0;
                            	java.util.List<String> typeCodeExist = new ArrayList<>();
                            	
                            	for(int k = 0; k < Integer.parseInt(col2Value); k++) {
                            		query = "select count(*) from costruttore where nome = ?;";
                            		nameValue.add(JOptionPane.showInputDialog(WECApp.this, "Inserisci il nome del costruttore:"));
                            		
                            		try (PreparedStatement preparedStatement3 = connection.prepareStatement(query)) {
                                		preparedStatement3.setString(1, nameValue.get(k));
                                		resultSet = preparedStatement3.executeQuery();
                                		if(handleResultSet(resultSet, "count") == 1) {
                                			query = "insert into componente (codice_componente, costo, nome_costruttore) values (?, ?, ?);";
                                			
                                			typeBuffer = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il tipo componente [motore, telaio, cambio]:");
                                			if(type.contains(typeBuffer)) {
                                				while(count != 0) {
                                               		query = "delete from composizione where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                                	statement.executeUpdate(query);
                                                	query = "update costruttore set componenti = componenti - 1 where costruttore.nome = \"" + nameValue.get(count - 1) + "\";";
                                                	statement.executeUpdate(query);
                                                	
                                                	query = "delete from componente where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                                	statement.executeUpdate(query);
                                                	count--;
                                                }
                                                query = "delete from vettura where numero_di_gara = \"" + col1Value + "\";";
                                                statement.executeUpdate(query);
                                                JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Tipo componente già in uso]", "Errore", JOptionPane.ERROR_MESSAGE);
                                                k = 3;
                                                break;
                                			}
                                			
                                			type.add(typeBuffer);
                                			typeCode.add(JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il codice componente nuovo:"));
                                			col4Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il costo:");
                                			
                                			try (PreparedStatement preparedStatement4 = connection.prepareStatement(query)) {
                                        		preparedStatement4.setString(1, typeCode.get(k));
                                        		preparedStatement4.setString(2, col4Value);
                                        		preparedStatement4.setString(3, nameValue.get(k));
                                        		preparedStatement4.executeUpdate();
                                        		
                                        		resultArea.setText("Inserimento componente eseguito con successo!");
                                        	} catch (SQLException ex) {
                                                
                                                while(count != 0) {
                                                	query = "delete from composizione where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                                	statement.executeUpdate(query);
                                                	query = "delete from componente where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                                	statement.executeUpdate(query);
                                                	query = "update costruttore set componenti = componenti - 1 where costruttore.nome = \"" + nameValue.get(count) + "\";";
                                            		statement.executeUpdate(query);
                                                	count--;
                                                }
                                                query = "delete from vettura where numero_di_gara = \"" + col1Value + "\";";
                                                statement.executeUpdate(query);
                                                JOptionPane.showMessageDialog(WECApp.this, "Errore nell'esecuzione della query", "Errore", JOptionPane.ERROR_MESSAGE);
                                            }
                                			query = "insert into composizione (numero_vettura, data_installazione, codice_componente) values (?, ?, ?);";
                                			
                                			col4Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la data di installazione [yyyy-mm-dd]:");
                                			try (PreparedStatement preparedStatement5 = connection.prepareStatement(query)) {
                                				preparedStatement5.setString(1, col1Value);
                                				preparedStatement5.setString(2, col4Value);
                                				preparedStatement5.setString(3, typeCode.get(k));
                                				preparedStatement5.executeUpdate();
                                				query = "update costruttore set componenti = componenti + 1 where costruttore.nome = \"" + nameValue.get(k) + "\";";
                                        		statement.executeUpdate(query);
                                        		resultArea.setText("Inserimento composizione del componente nuovo e aggiornamento costruttore eseguito con successo!");
                                				count++;
                                				typeBuffer = "";
                                			} catch (SQLException ex) {
                                                
                                                while(count != 0) {
                                               		query = "delete from composizione where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                                	statement.executeUpdate(query);
                                                	query = "update costruttore set componenti = componenti - 1 where costruttore.nome = \"" + nameValue.get(count - 1) + "\";";
                                                	statement.executeUpdate(query);
                                                		
                                                	query = "delete from componente where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                                	statement.executeUpdate(query);
                                                	count--;
                                                }
                                                query = "delete from vettura where numero_di_gara = \"" + col1Value + "\";";
                                                statement.executeUpdate(query);
                                                JOptionPane.showMessageDialog(WECApp.this, "Errore nell'esecuzione della query", "Errore", JOptionPane.ERROR_MESSAGE);
                                			}
                                		}
                                		else {
                                        	JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Non esiste il costruttore]", "Errore", JOptionPane.ERROR_MESSAGE);
                                        	break;
                                        }	
                            		}
                            	}
                            	if(type.contains(typeBuffer)) {
                            		break;
                            	}
                            
                            	if(type.contains("motore")) {
                            		query = "insert into motore (codice_motore, tipo, cilindrata, cilindri, codice_componente) values (?, ?, ?, ?, ?);";
                            		
                            		col5Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il codice motore:");
                            		col6Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il tipo:");
                            		col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la cilindrata:");
                            		col4Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per i cilindri:");
                            		
                            		try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                        				preparedStatement2.setString(1, col5Value);
                        				preparedStatement2.setString(2, col6Value);
                        				preparedStatement2.setString(3, col3Value);
                        				preparedStatement2.setString(4, col4Value);
                        				preparedStatement2.setString(5, typeCode.get(type.indexOf("motore")));
                        				preparedStatement2.executeUpdate();
                        				resultArea.setText("Inserimento motore eseguito con successo!");
                        			} catch (SQLException ex) {
                                        
                        				if(type.contains("motore")) {
                                        	query = "delete from motore where codice_componente = \"" + typeCode.get(type.indexOf("motore")) + "\";";
                                        	statement.executeUpdate(query);
                                        }
                                        if(type.contains("telaio")) {
                                        	query = "delete from telaio where codice_componente = \"" + typeCode.get(type.indexOf("telaio")) + "\";";
                                        	statement.executeUpdate(query);
                                        }
                                        if(type.contains("cambio")) {
                                        	query = "delete from cambio where codice_componente = \"" + typeCode.get(type.indexOf("cambio")) + "\";";
                                        	statement.executeUpdate(query);
                                        }
                                        
                                        while(count != 0) {
                                        	query = "delete from composizione where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                        	statement.executeUpdate(query);
                                        	query = "delete from componente where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                        	statement.executeUpdate(query);
                                        	query = "update costruttore set componenti = componenti - 1 where costruttore.nome = \"" + nameValue.get(count - 1) + "\";";
                                    		statement.executeUpdate(query);
                                        	count--;
                                        }
                                        
                                        query = "delete from vettura where numero_di_gara = \"" + col1Value + "\";";
                                        statement.executeUpdate(query);
                                        
                                        JOptionPane.showMessageDialog(WECApp.this, "Errore nell'esecuzione della query", "Errore", JOptionPane.ERROR_MESSAGE);
                        			}
                            	}
                            	if(type.contains("telaio")) {
                            		query = "insert into telaio (codice_telaio, peso, materiale, codice_componente) values (?, ?, ?, ?);";
                            		
                            		col5Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il codice telaio:");
                            		col6Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il peso:");
                            		col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il materiale:");
                            		
                            		try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                        				preparedStatement2.setString(1, col5Value);
                        				preparedStatement2.setString(2, col6Value);
                        				preparedStatement2.setString(3, col3Value);
                        				preparedStatement2.setString(4, typeCode.get(type.indexOf("telaio")));
                        				preparedStatement2.executeUpdate();
                        				resultArea.setText("Inserimento telaio eseguito con successo!");
                        			} catch (SQLException ex) {
                                        
                        				if(type.contains("motore")) {
                                        	query = "delete from motore where codice_componente = \"" + typeCode.get(type.indexOf("motore")) + "\";";
                                        	statement.executeUpdate(query);
                                        }
                                        if(type.contains("telaio")) {
                                        	query = "delete from telaio where codice_componente = \"" + typeCode.get(type.indexOf("telaio")) + "\";";
                                        	statement.executeUpdate(query);
                                        }
                                        if(type.contains("cambio")) {
                                        	query = "delete from cambio where codice_componente = \"" + typeCode.get(type.indexOf("cambio")) + "\";";
                                        	statement.executeUpdate(query);
                                        }
                                        
                                        while(count != 0) {
                                        	query = "delete from composizione where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                        	statement.executeUpdate(query);
                                        	query = "delete from componente where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                        	statement.executeUpdate(query);
                                        	query = "update costruttore set componenti = componenti - 1 where costruttore.nome = \"" + nameValue.get(count) + "\";";
                                    		statement.executeUpdate(query);
                                        	count--;
                                        }
                                        
                                        query = "delete from vettura where numero_di_gara = \"" + col1Value + "\";";
                                        statement.executeUpdate(query);
                                        
                                        JOptionPane.showMessageDialog(WECApp.this, "Errore nell'esecuzione della query", "Errore", JOptionPane.ERROR_MESSAGE);
                        			}
                            	}
                            	if(type.contains("cambio")) {
                            		query = "insert into cambio (codice_cambio, marcia, codice_componente) values (?, ?, ?);";
                            		
                            		col5Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il codice cambio:");
                            		col6Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la marcia:");
                            		
                            		try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                        				preparedStatement2.setString(1, col5Value);
                        				preparedStatement2.setString(2, col6Value);
                        				preparedStatement2.setString(3, typeCode.get(type.indexOf("cambio")));
                        				preparedStatement2.executeUpdate();
                        				resultArea.setText("Inserimento cambio eseguito con successo!");
                        			} catch (SQLException ex) {
                        				
                        				if(type.contains("motore")) {
                                        	query = "delete from motore where codice_componente = \"" + typeCode.get(type.indexOf("motore")) + "\";";
                                        	statement.executeUpdate(query);
                                        }
                                        if(type.contains("telaio")) {
                                        	query = "delete from telaio where codice_componente = \"" + typeCode.get(type.indexOf("telaio")) + "\";";
                                        	statement.executeUpdate(query);
                                        }
                                        if(type.contains("cambio")) {
                                        	query = "delete from cambio where codice_componente = \"" + typeCode.get(type.indexOf("cambio")) + "\";";
                                        	statement.executeUpdate(query);
                                        }
                                        
                                        while(count != 0) {
                                        	query = "delete from composizione where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                        	statement.executeUpdate(query);
                                        	query = "delete from componente where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                        	statement.executeUpdate(query);
                                        	query = "update costruttore set componenti = componenti - 1 where costruttore.nome = \"" + nameValue.get(count - 1) + "\";";
                                    		statement.executeUpdate(query);
                                        	count--;
                                        }
                                        
                                        query = "delete from vettura where numero_di_gara = \"" + col1Value + "\";";
                                        statement.executeUpdate(query);
                                        
                                        JOptionPane.showMessageDialog(WECApp.this, "Errore nell'esecuzione della query", "Errore", JOptionPane.ERROR_MESSAGE);
                        			}
                            	}
                            	
                            	try {
                            		int j = 0;
                            		String queryType;
                            		String typeFound = "";
                            		String query2 = "";
                            		String query3 = "";
                            		String query4 = "";
                            		
                            		for(int k = 3; k > Integer.parseInt(col2Value); k--) {
                                		query = "insert into composizione (numero_vettura, data_installazione, codice_componente) values (\"" + col1Value + "\", ?, ?);";
                                		col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la data di installazione [yyyy-mm-dd]:");
                                		typeCodeExist.add(JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il codice componente esistente:"));
                                		
                                		queryType = "select count(*) from componente inner join motore on componente.codice_componente = motore.codice_componente where componente.codice_componente = \"" + typeCodeExist.get(j) + "\";";
                                		if(handleResultSet(statement.executeQuery(queryType), "count") == 1) {
                                			typeFound = "motore";
                                		}
                                		
                                		queryType = "select count(*) from componente inner join telaio on componente.codice_componente = telaio.codice_componente where componente.codice_componente = \"" + typeCodeExist.get(j) + "\";";
                                		if(handleResultSet(statement.executeQuery(queryType), "count") == 1) {
                                			typeFound = "telaio";
                                		}
                                		
                                		queryType = "select count(*) from componente inner join cambio on componente.codice_componente = cambio.codice_componente where componente.codice_componente = \"" + typeCodeExist.get(j) + "\";";
                                		if(handleResultSet(statement.executeQuery(queryType), "count") == 1) {
                                			typeFound = "cambio";
                                		}
                                		
                                		query2 = "select count(*) from motore inner join componente on motore.codice_componente = componente.codice_componente left join composizione on componente.codice_componente = composizione.codice_componente where composizione.numero_vettura = \"" + col1Value + "\";";
                                		query3 = "select count(*) from telaio inner join componente on telaio.codice_componente = componente.codice_componente left join composizione on componente.codice_componente = composizione.codice_componente where composizione.numero_vettura = \"" + col1Value + "\";";
                                		query4 = "select count(*) from cambio inner join componente on cambio.codice_componente = componente.codice_componente left join composizione on componente.codice_componente = composizione.codice_componente where composizione.numero_vettura = \"" + col1Value + "\";";
                                		
                                		if((typeFound.equals("motore") && handleResultSet(statement.executeQuery(query2), "count") >= 1) || (typeFound.equals("telaio") && handleResultSet(statement.executeQuery(query3), "count") >= 1) || (typeFound.equals("cambio") && handleResultSet(statement.executeQuery(query4), "count") >= 1)) {
                                			
                                			if(type.contains("motore")) {
                                            	query = "delete from motore where codice_componente = \"" + typeCode.get(type.indexOf("motore")) + "\";";
                                            	statement.executeUpdate(query);
                                            }
                                            if(type.contains("telaio")) {
                                            	query = "delete from telaio where codice_componente = \"" + typeCode.get(type.indexOf("telaio")) + "\";";
                                            	statement.executeUpdate(query);
                                            }
                                            if(type.contains("cambio")) {
                                            	query = "delete from cambio where codice_componente = \"" + typeCode.get(type.indexOf("cambio")) + "\";";
                                            	statement.executeUpdate(query);
                                            }
                                			
                                			while(count2 != 0) {
                                            	query = "delete from composizione where codice_componente = \"" + typeCodeExist.get(count2 - 1) + "\";";
                                            	statement.executeUpdate(query);
                                            	query = "update costruttore inner join componente on costruttore.nome = componente.nome_costruttore set componenti = componenti - 1 where componente.codice_componente = \"" + typeCodeExist.get(count2 - 1) + "\";";
                                            	statement.executeUpdate(query);
                                            	count2--;
                                            }
                                            
                                            while(count != 0) {
                                            	query = "delete from composizione where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                            	statement.executeUpdate(query);
                                            	query = "delete from componente where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                            	statement.executeUpdate(query);
                                            	query = "update costruttore set componenti = componenti - 1 where costruttore.nome = \"" + nameValue.get(count - 1) + "\";";
                                        		statement.executeUpdate(query);
                                            	count--;
                                            }
                                            
                                            query = "delete from vettura where numero_di_gara = \"" + col1Value + "\";";
                                            statement.executeUpdate(query);
                                            
                                			JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Tipo di componente già in uso]", "Errore", JOptionPane.ERROR_MESSAGE);
                                			k = 0;
                                			break;
                                		}
                                		typeFound = "";
                                		PreparedStatement preparedStatement2 = connection.prepareStatement(query);
                                    	preparedStatement2.setString(1, col3Value);
                                   		preparedStatement2.setString(2, typeCodeExist.get(j));
                                   		preparedStatement2.executeUpdate();
                                   		query = "update costruttore inner join componente on costruttore.nome = componente.nome_costruttore set componenti = componenti + 1 where componente.codice_componente = \"" + typeCodeExist.get(j) + "\";";
                                		statement.executeUpdate(query);
                                   		resultArea.setText("Inserimento composizione del componente esistente e aggiornamento costruttore eseguito con successo!");
                                   		count2++;
                                   		j++;
                            		}
                            		if((typeFound.equals("motore") && handleResultSet(statement.executeQuery(query2), "count") >= 1) || (typeFound.equals("telaio") && handleResultSet(statement.executeQuery(query3), "count") >= 1) || (typeFound.equals("cambio") && handleResultSet(statement.executeQuery(query4), "count") >= 1)) {
                            			break;
                            		}
                            		
                            	} catch (SQLException ex) {
                            		
                            		if(type.contains("motore")) {
                                    	query = "delete from motore where codice_componente = \"" + typeCode.get(type.indexOf("motore")) + "\";";
                                    	statement.executeUpdate(query);
                                    }
                                    if(type.contains("telaio")) {
                                    	query = "delete from telaio where codice_componente = \"" + typeCode.get(type.indexOf("telaio")) + "\";";
                                    	statement.executeUpdate(query);
                                    }
                                    if(type.contains("cambio")) {
                                    	query = "delete from cambio where codice_componente = \"" + typeCode.get(type.indexOf("cambio")) + "\";";
                                    	statement.executeUpdate(query);
                                    }
                            		
                            		while(count2 != 0) {
                                    	query = "delete from composizione where codice_componente = \"" + typeCodeExist.get(count2 - 1) + "\";";
                                    	statement.executeUpdate(query);
                                    	query = "update costruttore inner join componente on costruttore.nome = componente.nome_costruttore set componenti = componenti - 1 where componente.codice_componente = \"" + typeCodeExist.get(count2 - 1) + "\";";
                                    	statement.executeUpdate(query);
                                    	count2--;
                                    }
                                    
                                    while(count != 0) {
                                    	query = "delete from componente where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                    	statement.executeUpdate(query);
                                    	query = "delete from composizione where codice_componente = \"" + typeCode.get(count - 1) + "\";";
                                    	statement.executeUpdate(query);
                                    	query = "update costruttore set componenti = componenti - 1 where costruttore.nome = \"" + nameValue.get(count - 1) + "\";";
                                		statement.executeUpdate(query);
                                    	count--;
                                    }
                                    
                                    query = "delete from vettura where numero_di_gara = \"" + col1Value + "\";";
                                    statement.executeUpdate(query);
                                    
                        			JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Tipo di componente già in uso]", "Errore", JOptionPane.ERROR_MESSAGE);
                    			}
                            }
                            else {
                            	JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Non esiste quella scuderia]", "Errore", JOptionPane.ERROR_MESSAGE);
                            	break;
                            }
                    	}
                        break;
                        
                    case 3:
                    	query = "select count(*) from vettura where numero_di_gara = ?;";
                    	newValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il numero di gara della vettura:");
                    	
                    	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    		preparedStatement.setString(1, newValue);
                    		resultSet = preparedStatement.executeQuery();
                    		if(handleResultSet(resultSet, "count") == 1) {
                    			newValue2 = JOptionPane.showInputDialog(WECApp.this, "Inserisci il tipo di pilota [pro, am, gd]");
                    			
                    			if(newValue2.equals("gd")) {
                    				query = "select count(*) from vettura as v inner join pilota as p on v.numero_di_gara = p.numero_vettura where v.numero_di_gara = \"" + newValue + "\" and not exists (select pil.codice_pilota from vettura as ve inner join pilota as pil on ve.numero_di_gara = pil.numero_vettura inner join gentleman_driver on pil.codice_pilota = gentleman_driver.codice_pilota  where v.numero_di_gara = ve.numero_di_gara and p.codice_pilota = pil.codice_pilota);";
                    				resultSet = statement.executeQuery(query);
                    				if(handleResultSet(resultSet, "count") == 0) {
                    					JOptionPane.showMessageDialog(WECApp.this, "Vincolo violato [Almeno un pilota non gentleman driver nell'equipaggio]", "Errore", JOptionPane.ERROR_MESSAGE);
                    					break;
                    				}
                    			}
                    			query = "insert into pilota (codice_pilota, nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, numero_vettura) values (?, ?, ?, ?, ?, ?, ?, ?)";
                    				
                    			col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il codice pilota:");
                                col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il nome:");
                                col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il cognome:");
                                col4Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la data di nascita [yyyy-mm-dd]:");
                                col5Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la nazionalità [nazione]:");
                                col6Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il numero di licenze:");
                                if(Integer.parseInt(col6Value) > 1 && (newValue2.equals("am") || newValue2.equals("gd"))) {
                                	JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Un am o un gentleman driver possono avere solo una licenza]", "Errore", JOptionPane.ERROR_MESSAGE);
                                	break;
                                }
                                else if(Integer.parseInt(col6Value) == 1 && newValue2.equals("pro")) {
                                	JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Un pro deve avere più di una licenza]", "Errore", JOptionPane.ERROR_MESSAGE);
                                	break;
                                }
                                col7Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la data della prima licenza [yyyy-mm-dd]:");
                                    
                                try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                                	preparedStatement2.setString(1, col1Value);
                                	preparedStatement2.setString(2, col2Value);
                                	preparedStatement2.setString(3, col3Value);
                                	preparedStatement2.setString(4, col4Value);
                                	preparedStatement2.setString(5, col5Value);
                                	preparedStatement2.setString(6, col6Value);
                                	preparedStatement2.setString(7, col7Value);
                                	preparedStatement2.setString(8, newValue);
                                	preparedStatement2.executeUpdate();
                                	resultArea.setText("Inserimento pilota eseguito con successo!");
                    			}
                                
                                if(newValue2.equals("gd")) {
                                	query = "insert into gentleman_driver (codice_gentleman, quota, finanziamenti, codice_pilota ) values (?, ?, ?, ?);";
                                	
                                	col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il codice gentleman:");
                                	col3Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la quota:");
                                	col4Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per i finanzimenti:");
                                	
                                	try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                                    	preparedStatement2.setString(1, col2Value);
                                    	preparedStatement2.setString(2, col3Value);
                                    	preparedStatement2.setString(3, col4Value);
                                    	preparedStatement2.setString(4, col1Value);
                                    	preparedStatement2.executeUpdate();
                                    	query = "update scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join pilota on vettura.numero_di_gara = pilota.numero_vettura inner join gentleman_driver on pilota.codice_pilota = gentleman_driver.codice_pilota set scuderia.finanziamenti_totali = finanziamenti_totali + \"" + col4Value + "\" where gentleman_driver.codice_gentleman = \"" + col2Value + "\";";
                                    	statement.executeUpdate(query);
                                    	resultArea.setText("Inserimento gentleman driver e aggiornamento scuderia eseguito con successo!");
                        			}
                                }
                    		}
                    		else {
                    			JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Non esiste quella vettura]", "Errore", JOptionPane.ERROR_MESSAGE);
                    		}
                    	}
                    	break;
                    	
                    case 4:
                    	query = "select count(*) from gentleman_driver where codice_gentleman = ?;";
                    	newValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il codice gentleman del gentleman driver:");
                    	
                    	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    		preparedStatement.setString(1, newValue);
                    		resultSet = preparedStatement.executeQuery();
                    		if(handleResultSet(resultSet, "count") == 1) {
                    			query = "update gentleman_driver set quota = quota + ?, finanziamenti = finanziamenti + 1 where codice_gentleman = ?;";
                    			
                    			col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per la quota da aggiungere:");
                    			
                    			try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                                	preparedStatement2.setString(1, col1Value);
                                	preparedStatement2.setString(2, newValue);
                                	preparedStatement2.executeUpdate();
                                	query = "update gentleman_driver inner join pilota on gentleman_driver.codice_pilota = pilota.codice_pilota inner join vettura on pilota.numero_vettura = vettura.numero_di_gara inner join scuderia on vettura.nome_scuderia = scuderia.nome set scuderia.finanziamenti_totali = scuderia.finanziamenti_totali + 1 where codice_gentleman = \"" + newValue + "\" and gentleman_driver.codice_pilota = pilota.codice_pilota and pilota.numero_vettura = vettura.numero_di_gara and vettura.nome_scuderia = scuderia.nome;";
                                	statement.executeQuery(query);
                                	resultArea.setText("Aggiornamento gentleman driver e scuderia eseguito con successo!");
                    			}
                    		}
                    		else {
                    			JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Gentleman driver non trovato]", "Errore", JOptionPane.ERROR_MESSAGE);
                    		}
                    	}
                    	break;
                    	
                    case 5:
                    	query = "select count(*) from vettura where numero_di_gara = ?;";
                    	newValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il numero di gara della vettura:");
                    	
                    	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    		preparedStatement.setString(1, newValue);
                    		resultSet = preparedStatement.executeQuery();
                    		if(handleResultSet(resultSet, "count") == 1) {
                    			query = "select count(*) from gara where nome = ?;";
                    			
                    			col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci il nome della gara:");
                    			
                    			try (PreparedStatement preparedStatement2 = connection.prepareStatement(query)) {
                            		preparedStatement2.setString(1, col1Value);
                            		resultSet = preparedStatement2.executeQuery();
                            		if(handleResultSet(resultSet, "count") == 1) {
                            			query = "select count(*) from iscrizione where numero_vettura = \"" + newValue + "\" and nome_gara = \"" + col1Value + "\";";
                            			
                            			try (PreparedStatement preparedStatement3 = connection.prepareStatement(query)) {
                                    		resultSet = preparedStatement3.executeQuery();
                                    		if(handleResultSet(resultSet, "count") == 0) {
                                    			query = "insert into iscrizione (numero_vettura, nome_gara, punteggio, stato_fine_gara) values (\"" + newValue + "\", \"" + col1Value + "\", \"0\", \"nonterminato\");";
                                    			
                                    			statement.executeUpdate(query);
                                    			resultArea.setText("Inserimento iscrizione eseguito con successo!");
                                    		}
                                    		else {
                                    			JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Iscrizione già effettuata]", "Errore", JOptionPane.ERROR_MESSAGE);
                                    		}
                            			}
                            		}
                            		else {
                            			JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Gara non trovata]", "Errore", JOptionPane.ERROR_MESSAGE);
                            		}
                    			}
                    		}
                    		else {
                    			JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Vettura non trovata]", "Errore", JOptionPane.ERROR_MESSAGE);
                    		}
                    	}
                    	break;
                    	
                    case 6:
                    	query = "select count(*) from iscrizione where nome_gara = ?;";
                    	newValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il nome di gara:");
                    	
                    	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    		preparedStatement.setString(1, newValue);
                    		resultSet = preparedStatement.executeQuery();
                    		int n = handleResultSet(resultSet, "cycle");
                    		if(n >= 1) {
                    			String query2 = "select numero_vettura from iscrizione where nome_gara = \"" + newValue + "\";";
                            	ResultSet resultSet2 = statement.executeQuery(query2);
                            	String[] numeroVettura = extractStringResultSet(resultSet2).split(" ");
                        			
                            	for(int i = 0; i < n; i++) {
                        			query = "update iscrizione set punteggio = ?, stato_fine_gara = ? where nome_gara = \"" + newValue + "\" and numero_vettura = \"" + numeroVettura[i] + "\";";
                        				
                        			col1Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per il punteggio [0 - 25] [" + numeroVettura[i] + "]:");
                        			col2Value = JOptionPane.showInputDialog(WECApp.this, "Inserisci valore per lo stato fine gara [terminato, incidente, guasto meccanico, squalifica] [" + numeroVettura[i] + "]:");
                        			
                        			try (PreparedStatement preparedStatement3 = connection.prepareStatement(query)) {
                        				preparedStatement3.setString(1, col1Value);
                        				preparedStatement3.setString(2, col2Value);
                        				preparedStatement3.executeUpdate();
                        				resultArea.setText("Aggiornamento iscrizione [" + numeroVettura[i] + "] eseguito con successo!");
                        			}
                        		}
                    		}
                        	else {
                        		JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Gara non trovata]", "Errore", JOptionPane.ERROR_MESSAGE);
                        	}
                    	}
                    	break;
                    	
                    case 7:
                    	query = "select count(*) from composizione where numero_vettura = ?;";
                    	newValue = JOptionPane.showInputDialog(WECApp.this, "Inserisci il numero di gara della vettura:");
                    	
                    	try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    		preparedStatement.setString(1, newValue);
                    		resultSet = preparedStatement.executeQuery();
                    		if(handleResultSet(resultSet, "count") == 1) {
                    			query = "select count(*) from composizione inner join componente on composizione.codice_componente = componente.codice_componente left join motore on motore.codice_componente = componente.codice_componente left join cambio on cambio.codice_componente = componente.codice_componente left join telaio on telaio.codice_componente = componente.codice_componente where numero_vettura = \"" + newValue + "\";";
                            	resultSet = statement.executeQuery(query);
                        		resultArea.setText("Ci sono attualmente: " + handleResultSet(resultSet, "cycle") + " componenti nella vettura: " + newValue);
                    		}
                    		else {
                        		JOptionPane.showMessageDialog(WECApp.this, "Vincolo d'integrità violato [Vettura non trovata]", "Errore", JOptionPane.ERROR_MESSAGE);
                        	}
                    	}
                    default:
                        break;
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(WECApp.this, "Errore nell'esecuzione della query", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private int handleResultSet(ResultSet resultSet, String op) throws SQLException {
    	int n = 0;
    	
    	switch(op) {
		case "count":
			while (resultSet.next()) {
				n = resultSet.getInt(1);
			}
			if(n >= 1)
				return 1;
			else
				return 0;
 
		case "cycle":
			while(resultSet.next()) {
				n = resultSet.getInt(1);
			}
			
			return n;
			
		default:
			break;
		}
    	return 0;
    }
    
    private String extractStringResultSet(ResultSet resultSet) throws SQLException {
    	StringBuilder resultText = new StringBuilder();
    	while (resultSet.next()) {
            resultText.append(resultSet.getString(1)).append(" ");
        }
    	
    	return resultText.toString().trim();
    }

    private void displayResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        StringBuilder resultText = new StringBuilder();

        for (int i = 1; i <= columnCount; i++) {
            resultText.append(String.format("%-25s", metaData.getColumnName(i))).append("\t");
        }
        resultText.append("\n");

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                resultText.append(String.format("%-25s", resultSet.getString(i))).append("\t");
            }
            resultText.append("\n");
        }

        resultArea.setText(resultText.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WECApp());
    }
}