# Eryantis Board Game

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
![latest commit](https://img.shields.io/github/last-commit/DariodAbate/ingsw2022-AM57?color=red)
<img src="assets/Eriantys_scatolaFrontombra-600x600.png" width=300px height=300px align="right" />

The development of this project is part of the software engineering course at the Polytechnic University of Milan, and as a final examination, it is necessary for the bachelor's degree in computer engineering. The course was held by prof. Alessandro Margara, Gianpaolo Cugola and Pierluigi San Pietro in the academic year 2021-2022.<br>
***Final Score: 30/30 cum laude***

## Project specification

<p>The project consists of a distributed multi-user application with a graphical interface that emulates a board game.</p>
<p><em>The official game can be found <a href="https://craniointernational.com/products/eriantys/">here</a></em></p>

| Functionality                | State |
|:-----------------------------|:-----:|
| Basic rules                  |  🟢   |
| Complete rules               |  🟢   |
| Socket                       |  🟢   |
| GUI                          |  🟢   |
| CLI                          |  🟢   |
| Character cards              |  🟢   |
| Multiple games               |  🟢   |
| Persistence                  |  🟢   |
| 4 players game               |  🔴   |
| Resilience to disconnections |  🔴   |

#### Legend
  
🔴 Not implemented
🟡 Implementing
🟢 Implemented

## Test cases

**Coverage criteria: code lines.**

| Package | Tested Class   |   Coverage    |
|:--------|:---------------|:-------------:|
| Model   | Global Package | 823/963 (85%) |


## The Team
- ### [Dario d'Abate](https://github.com/DariodAbate)<br/>dario.dabate@mail.polimi.it
- ### [Lorenzo Corrado](https://github.com/Lerrylore)<br/>lorenzo.corrado@mail.polimi.it 
- ### [Luca Bresciani](https://github.com/BrescianiLuca)<br/>luca5.bresciani@mail.polimi.it


## Tools used

- **Intellij IDEA Ultimate** - Development environment
- **Maven** - Project management
- **Git** - Version control
- **Junit** - Testing
- **Sonarqube** - Code analysis
- **Astah UML** - UML diagram

## How to use
<p><em>The software can be run on WIndows, Linux and MacOS.</em></p>
<p><em>The software was developed using Java 17.0.0.2 and Java JDK 17.0.0.2, so be sure to use these versions or higher.</em></p>

<ol>
  <li>Be sure to have <a href="https://www.java.com/it/download/">Java</a> and <a 
  href="https://www.oracle.com/java/technologies/javase/jdk17-
  archive-downloads.html"> Java JDK</a> installed</li>
  <li>Download <a href="https://github.com/DariodAbate/ingsw2022-
  AM57/blob/master/deliveries/AM57-client.jar">AM57-client.jar</a> and <a 
  href="https://github.com/DariodAbate/ingsw2022-AM57/blob/master/deliveries/AM57-
  server.jar">AM57-server.jar</a> (at least one server should be up to play!)
  </li>
  <li>To open the server you need to type <code> -java -jar AM57-server.jar [port number] 
  </code> (this passage is optional if another server is already open)</li>
  <li>Open the cmd and then type <code>-java -jar AM57-client.jar [interface] </code>  where 
  instead of interface, type CLI or GUI depending on your preferences</li>
  <li>Have fun! </li>
</ol>
  
<p><em>When using the CLI mode, we suggest to utilize <a href="https://conemu.github.io">conEmu</a> on Windows, since some special characters are not correctly visualized in the default Windows shell.</em></p>

## Copyright and license

Eryantis Board Game is copyrighted 2021.

Licensed under the **[MIT License](https://github.com/DariodAbate/ingsw2022-AM57/blob/master/LICENSE)** ;
you may not use this software except in compliance with the License.

