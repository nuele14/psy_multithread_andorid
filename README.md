# Progetto per applicazione mobile Android

## Titolo: Gestione di due thread concorrenti

## Descrizione:

L'applicazione permette di gestire due thread concorrenti. Il primo thread interroga l'API di ZenQuotes per ottenere una citazione casuale. Nel caso in cui la citazione includa il nome di una persona, il thread esegue anche una ricerca di immagini su Google per trovare una foto associata al nome. Il secondo thread calcola e appende ad una lista una serie di numeri primi. La lista viene continuamente aggiornata con i nuovi numeri primi calcolati.

## Interazione con l'utente:

L'interfaccia utente è composta da tre bottoni:

Start all: avvia i due thread
Start numeri primi: avvia il thread dei numeri primi
Start quotes: avvia il thread delle citazioni

Inoltre, è presente un toggle che permette di cambiare il font dell'interfaccia utente o il tema (da chiaro a scuro).

## Interazione dei bottoni:

Start all: disabilita gli altri due bottoni e cambia il proprio testo in "Stop all".
Start numeri primi: disabilita "Start all" e cambia il proprio testo in "Stop numeri primi".
Start quotes: disabilita "Start all" e cambia il proprio testo in "Stop quotes".

Il toggle ha associata una label sulla quale viene visualizzata la famiglia di font o il colore del tema attualmente selezionato.

## Struttura dell'applicazione:

L'applicazione è composta dalle seguenti classi:

MainActivity: classe principale dell'applicazione. Gestisce l'interazione con l'utente e i thread.
QuotesThread: classe che implementa il thread per l'interrogazione dell'API di ZenQuotes.
PrimeNumbersThread: classe che implementa il thread per il calcolo dei numeri primi.

## Implementazione:

L'applicazione viene implementata utilizzando i seguenti componenti:

Intent: per avviare l'attività di ricerca di immagini su Google.
AsyncTask: per eseguire le operazioni in background in modo asincrono.
Handler: per gestire la comunicazione tra i thread e l'interfaccia utente.

## Test:

L'applicazione viene testata manualmente utilizzando i seguenti casi di test:

Verifica dell'interazione con l'utente: vengono verificati il comportamento dei bottoni e del toggle.
Verifica del funzionamento dei thread: vengono verificati i risultati ottenuti dai thread
