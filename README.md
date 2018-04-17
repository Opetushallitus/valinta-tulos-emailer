# valinta-tulos-emailer

Sovellus joka hakee valinta-tulos-service:stä avointen hakujen vastaanotettavat valinta-tulokset,
muodostaa lähettävät sähköpostit ja lähettää ne viestintapalvelulle. Lähetyksen jälkeen
valinta-tulos-emailer merkitsee valinta-tulos-servicen avulla ne valinta-tulokset joille lähetys onnistui.

#### Kehittäminen 

Jos projekti ei toimi sbt:ssä, varmista että se pyörii versiolla 0.13 eikä 1.0, ks. `./project/build.properties`.