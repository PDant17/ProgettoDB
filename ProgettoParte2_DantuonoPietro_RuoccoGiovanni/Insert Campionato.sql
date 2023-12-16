use campionato;

insert into scuderia (nome, sede, finanziamenti_totali)
values
('scuderia ferrari', 'maranello, italy', 1),
('mercedes amg petronas', 'stuttgart, germany', 1),
('red bull racing', 'milton keynes, uk', 0);

insert into vettura (numero_di_gara, modello, nome_scuderia)
values
(101, 'modello a', 'scuderia ferrari'),
(102, 'modello b', 'mercedes amg petronas'),
(103, 'modello c', 'red bull racing');

insert into pilota (codice_pilota, nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, numero_vettura)
values
(1, 'mario', 'rossi', '1990-05-15', 'italiana', 1, '2010-03-20', 101),
(2, 'luca', 'bianchi', '1985-12-10', 'italiana', 1, '2008-06-12', 102),
(3, 'anna', 'verdi', '1995-08-25', 'italiana', 4, '2015-02-28', 103);

insert into gentleman_driver (codice_gentleman, quota, finanziamenti, codice_pilota)
values
(1, 1500.00, 1, 1),
(2, 1200.00, 1, 2);

insert into circuito (nome, paese, lunghezza, curve)
values
('monza', 'italy', 5.79, 11),
('hockenheimring', 'germany', 4.57, 17),
('silverstone', 'uk', 5.89, 18);

insert into gara (nome, tipo, data, durata, nome_circuito)
values
('gran premio italia', 'a', '2023-07-01', '02:30:00', 'monza'),
('gran premio germania', 'a', '2023-07-15', '02:15:00', 'hockenheimring'),
('gran premio inghilterra', 'a', '2023-07-29', '02:20:00', 'silverstone');

insert into iscrizione (numero_vettura, nome_gara, punteggio, stato_fine_gara)
values
(101, 'gran premio italia', 25, 'terminato'),
(102, 'gran premio germania', 18, 'terminato'),
(103, 'gran premio inghilterra', 15, 'terminato');

insert into costruttore (nome, ragione_sociale, sede, componenti)
values
('ferrari', 'ferrari s.p.a.', 'maranello, italy', 3),
('mercedes', 'mercedes-benz', 'stuttgart, germany', 3),
('red bull racing', 'red bull racing', 'milton keynes, uk', 3);

insert into componente (codice_componente, costo, nome_costruttore)
values
(1, 5000.00, 'ferrari'),
(2, 4800.00, 'mercedes'),
(3, 4500.00, 'red bull racing');

insert into motore (codice_motore, tipo, cilindrata, cilindri, codice_componente)
values
(10, 'v6 turbo', 1600, 6, 1);

insert into telaio (codice_telaio, peso, materiale, codice_componente)
values
(20, 50, 'fibra di vetro', 2);

insert into cambio (codice_cambio, marcia, codice_componente)
values
(30, 8, 3);

insert into composizione (numero_vettura, data_installazione, codice_componente)
values
(101, '2023-06-15', 1),
(101, '2023-06-15', 2),
(101, '2023-06-15', 3),
(102, '2023-06-15', 1),
(102, '2023-06-15', 2),
(102, '2023-06-15', 3),
(103, '2023-06-15', 1),
(103, '2023-06-15', 2),
(103, '2023-06-15', 3);


