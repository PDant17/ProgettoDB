use campionato;
#NUMERO 1
insert into scuderia (nome, sede, finanziamenti_totali)
values
('scuderia longobardi', 'napoli, italy', 3);

#NUMERO 2
#richiesta nome scuderia in java
select nome
from scuderia;
#se ci sta allora va bene
insert into vettura (numero_di_gara, modello, nome_scuderia)
values (101, 'modello a', "VINCOLO INTEGRITA'");

#NUMERO 3 
#richiesta tipologia pilota in java
#se è gentleman driver, controllo che ci sia almeno un pro o am, chiedo la vettura
#controllo pro
select count(*) as pro
from vettura inner join pilota on vettura.numero_di_gara = pilota.numero_vettura
where vettura.numero_di_gara = 103 and pilota.licenze > 1;

#controllo am, non gentleman driver
select count(*) as am
from vettura as v inner join pilota as p on v.numero_di_gara = p.numero_vettura
where v.numero_di_gara = 103 and p.licenze = 1 and not exists (
	select pil.codice_pilota
    from vettura as ve inner join pilota as pil on ve.numero_di_gara = pil.numero_vettura inner join gentleman_driver on
    pil.codice_pilota = gentleman_driver.codice_pilota 
    where v.numero_di_gara = ve.numero_di_gara and p.codice_pilota = pil.codice_pilota);
    
/*Conteggio numero gentleman_driver
select count(*) 
from vettura inner join pilota on vettura.numero_di_gara = pilota.numero_vettura inner join gentleman_driver on pilota.codice_pilota = gentleman_driver.codice_pilota
where vettura.numero_di_gara = 101;
*/
insert into pilota (codice_pilota, nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, numero_vettura)
values
(1, 'mario', 'rossi', '1990-05-15', 'italiana', 1, '2010-03-20', "VINCOLO INTEGRITA'");
insert into gentleman_driver (codice_gentleman, quota, finanziamenti, codice_pilota)
values
(1, 1500.00, 1, 1);
select numero_gara
from vettura;

#NUMERO 4
#richiesta del gentleman driver, controllo esistenza
select count(*) as gentleman_driver from gentleman_driver where codice_gentleman = "CodiceGentleman";
#se va bene, allora
update gentleman_driver
set quota = quota + 1000.00, finanziamenti = finanziamenti + 1
where codice_gentleman = 2;
#aggiornamento attributo ridondante in scuderia
update gentleman_driver inner join pilota on gentleman_driver.codice_pilota = pilota.codice_pilota
inner join vettura on pilota.numero_vettura = vettura.numero_di_gara inner join scuderia on vettura.nome_scuderia = scuderia.nome
set scuderia.finanziamenti_totali = scuderia.finanziamenti_totali + 1
where codice_gentleman = 2 and gentleman_driver.codice_pilota = pilota.codice_pilota and
pilota.numero_vettura = vettura.numero_di_gara and vettura.nome_scuderia = scuderia.nome;

#NUMERO 5
#richiesta numero vettura di vettura, controllo esistenza
select count(*) as vetture from vettura where numero_di_gara = "NumeroGara";
#richiesta nome di gara, controllo esistenza
select count(*) as gare from gara where nome = "NomeGara";
#controllo della non iscrizione di questa vettura a questa gara
select count(*) as iscrizioni from iscrizione where numero_vettura = "NumeroGara" and nome_gara = "NomeGara";
#se va bene, allora, ricorda che il punteggio e lo stato_fine_gara sono inizializzati a -1 e NonArrivato
insert into iscrizione (numero_vettura, nome_gara, punteggio, stato_fine_gara)
values
(101, 'gran premio italia', -1, 'NonArrivato');

#NUMERO 6
#rihiesta nome_gara di gara, controllo esistenza, controllo in java correttezza punteggio (punteggio <= 25)
select count(*) as totale from iscrizione where nome_gara = "NomeGara";
select numero_vettura from iscrizione where nome_gara = "NomeGara";
#se va bene allora, ciclare per count(*) volte, settando ogni i numero_vettura
update iscrizione set punteggio = "punteggio", stato_fine_gara = "stato fine gara"
where numero_vettura = "NumeroVettura" and nome_gara = "NomeGara";

#NUMERO 7
#richiesta vettura, tipo da montare e data di montaggio
#conteggio 
select count(*)
from composizione inner join componente on composizione.codice_componente = componente.codice_componente
left join motore on motore.codice_componente = componente.codice_componente
left join cambio on cambio.codice_componente = componente.codice_componente
left join telaio on telaio.codice_componente = componente.codice_componente
where numero_vettura = "NumeroVettura" and composizione.codice_componente = "TipoMontare";
#se count(*) è 0, allora va bene
/*
#se count(*) > 0, allora verifica data, la condizione è che sia count(*) = 0, cioè non ci sono componenti più nuovi
select count(*)
from composizione inner join componente on composizione.codice_componente = componente.codice_componente
left join motore on motore.codice_componente = componente.codice_componente
left join cambio on cambio.codice_componente = componente.codice_componente
left join telaio on telaio.codice_componente = componente.codice_componente
where numero_vettura = "NumeroVettura" and composizione.codice_componente = "TipoMontare" and data_installazione > "DataInstallazioneNuovoComponente";
*/

#NUMERO 8
select nome, finanziamenti_totali from scuderia;

#NUMERO 9
#una scuderia ha partecipato, se almeno una sua vettura ha una iscrizione ad una gara
select nome, finanziamenti_totali from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia 
inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura
group by nome, finanziamenti_totali;

#NUMERO 10
select p.codice_pilota, p.nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, p.numero_vettura,
codice_gentleman, quota, finanziamenti
from pilota p left join gentleman_driver on p.codice_pilota = gentleman_driver.codice_pilota inner join vettura
on p.numero_vettura = vettura.numero_di_gara inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura
inner join gara g on iscrizione.nome_gara = g.nome inner join circuito c on g.nome_circuito = c.nome
where iscrizione.punteggio = 25 and p.nazionalità = c.paese
group by p.codice_pilota, p.nome, cognome, data_di_nascita, nazionalità, licenze, data_prima_licenza, p.numero_vettura,
codice_gentleman, quota, finanziamenti;

#NUMERO 11
select scuderia.nome, vettura.numero_di_gara, 100/count(pilota.codice_pilota)*count(gentleman_driver.codice_pilota) as percentuale
from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join pilota on vettura.numero_di_gara = 
pilota.numero_vettura left join gentleman_driver on pilota.codice_pilota = gentleman_driver.codice_pilota
group by vettura.numero_di_gara;
/*(select count(*)
from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join pilota on vettura.numero_di_gara = 
pilota.numero_vettura left join gentleman_driver on pilota.codice_pilota = gentleman_driver.codice_pilota
group by pilota.codice_pilota)*/

#NUMERO 12
select * from costruttore;

#NUMERO 13
select vettura.numero_di_gara, sum(iscrizione.punteggio) as punteggio_finale
from vettura inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura
group by vettura.numero_di_gara;

#NUMERO 14
select motore.tipo, sum(iscrizione.punteggio) as punteggio_finale
from motore inner join componente on motore.codice_componente = componente.codice_componente inner join composizione on
componente.codice_componente = composizione.codice_componente inner join vettura on composizione.numero_vettura = vettura.numero_di_gara
inner join iscrizione on vettura.numero_di_gara = iscrizione.numero_vettura
group by motore.codice_motore;

#NUMERO 15
select scuderia.nome, vettura.numero_di_gara, sum(iscrizione.punteggio)/sum(round(time_to_sec(gara.durata)/60)) as rapporto
from scuderia inner join vettura on scuderia.nome = vettura.nome_scuderia inner join iscrizione on vettura.numero_di_gara = 
iscrizione.numero_vettura inner join gara on iscrizione.nome_gara = gara.nome
group by scuderia.nome, vettura.numero_di_gara;