# ICT Technologies for the Web (Dynamic Version)

This repository contains the first part of the [ICT Technologies for the Web](https://onlineservices.polimi.it/manifesti/manifesti/controller/ManifestoPublic.do?EVN_DETTAGLIO_RIGA_MANIFESTO=evento&aa=2025&k_cf=225&k_corso_la=531&k_indir=I3I&codDescr=085879&lang=IT&semestre=2&idGruppo=5457&idRiga=335673)
course final project held at Politecnico di Milano

## OVERVIEW

The ICT Technologies for the Web course at Politecnico di Milano deals with technologies, architectures,
implementation details and programming of web based applications.
The final evaluation of the project does not consist of a written exam but rather in a final project 
where two small web applications are to be developed. Both application share the same specifications
however they differ in the technological and architectural aspects that must be used for the implementation.

In particular the second part of the final project (this repository) is to be developed as an dynamic (CSR and SPA)
three tier web application. More in detail the architecture comprises:
- A SQL database.
- Application server written in Java with no HTML templating exposing a REST API to the clients.
- Tomcat web server for static file delivery (including javascript) and request forwarding to the application server.
Client side rendering (CSR) is performed in a single page application (SPA) manner using pure vanilla javascript,
which is also in charge of requesting and downloading JSON data from the application server as required by
the user's interactions.
More details regarding the chosen architecture and techincal design can be found under `docs/`.

The project was developed in groups of two and the final score obtained was of **28 out of 30**.

