create schema Campionato;
use campionato;

create table costruttore(
nome varchar(50) primary key,
ragione_sociale varchar(20) not null,
sede varchar(50) not null,
componenti numeric(2) not null
);

create table componente(
codice_componente int primary key auto_increment,
costo numeric(10,2) not null,
nome_costruttore varchar(50),
foreign key(nome_costruttore) references costruttore(nome)
on update cascade
on delete cascade
);

create table motore(
codice_motore int primary key auto_increment,
tipo varchar(30) not null,
cilindrata numeric(5) not null,
cilindri numeric(2) not null,
codice_componente int,
foreign key(codice_componente) references componente(codice_componente)
on update cascade
on delete cascade
);

create table telaio(
codice_telaio int primary key auto_increment,
peso numeric(5,2) not null,
materiale varchar(30) not null,
codice_componente int,
foreign key(codice_componente) references componente(codice_componente)
on update cascade
on delete cascade
);

create table cambio(
codice_cambio int primary key auto_increment,
marcia numeric(1),
codice_componente int,
foreign key(codice_componente) references componente(codice_componente)
on update cascade
on delete cascade
);

create table scuderia(
nome varchar(50) primary key,
sede varchar(50) not null,
finanziamenti_totali numeric(3) not null
);

create table vettura(
numero_di_gara numeric(4) primary key,
modello varchar(20) not null,
nome_scuderia varchar(50),
foreign key(nome_scuderia) references scuderia(nome)
on update cascade
on delete cascade
);

create table pilota(
codice_pilota int primary key auto_increment,
nome varchar(30) not null,
cognome varchar(30) not null,
data_di_nascita date not null,
nazionalità varchar(20) not null,
licenze numeric(3) not null,
data_prima_licenza date not null,
numero_vettura numeric(4),
foreign key(numero_vettura) references vettura(numero_di_gara)
on update cascade
on delete cascade
);

create table gentleman_driver(
codice_gentleman int primary key auto_increment,
quota numeric(15,2) not null,
finanziamenti numeric(2) not null,
codice_pilota int,
foreign key(codice_pilota) references pilota(codice_pilota)
on update cascade
on delete cascade
);

create table circuito(
nome varchar(50) primary key,
paese varchar(50) not null,
lunghezza numeric(3,2) not null,
curve numeric(2) not null
);

create table composizione(
numero_vettura numeric(4),
data_installazione date not null,
codice_componente int,
foreign key(numero_vettura) references vettura(numero_di_gara)
on update cascade
on delete cascade,
foreign key(codice_componente) references componente(codice_componente)
on update cascade
on delete cascade
);

create table gara(
nome varchar(50) primary key,
tipo varchar(1) not null,
`data` date not null,
durata time not null,
nome_circuito varchar(50) not null,
foreign key(nome_circuito) references circuito(nome)
on update cascade
on delete cascade
);

create table iscrizione(
numero_vettura numeric(4),
nome_gara varchar(50),
punteggio numeric (3) not null,
stato_fine_gara varchar(16) not null,
foreign key(numero_vettura) references vettura(numero_di_gara)
on update cascade
on delete cascade,
foreign key(nome_gara) references gara(nome)
on update cascade
on delete cascade
);

