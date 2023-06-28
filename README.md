# Final Exam Software Engineering 2023: MyShelfie
![Display_1.jpg](src%2Fmain%2Fresources%2Fimages%2Fpublisher%2FDisplay_1.jpg)
## GC09
### Authors:
- Luca Pagano (<luca1.pagano@mail.polimi.it>)
- Fabio Sabbion (<fabio.sabbion@mail.polimi.it>)
- Andri Salillari (<andri.salillari@mail.polimi.it>)
- Lorenzo Torsani (<lorenzo.torsani@mail.polimi.it>)

# Project Description

The project consists of the development of a software version of the board game My Shelfie.

The final project shall include
- high-level UML diagrams of the application, showing, with all and only useful details, the overall design

- general design of the application;
- detailed UML diagrams showing all aspects of the application. These diagrams may be generated from the project source code using automatic tools;
- working implementation of the game in accordance with the rules of the game and the specifications in this
  document;
- documentation of the communication protocol between client and server;
  peer review documents (one for the first peer review and one for the second);
- source code of the implementation;
- Javadoc documentation of the implementation (generated from the code);
  unit test source code.


|  Requirements met   | Voto  |
|-----|---|
Simple Rules+ TUI + RMI o Socket | 18 |
Complete Rules + TUI + RMI o Socket |20|
Complete Rules + TUI + RMI o Socket + 1 FA |22|
Complete Rules + TUI + GUI + RMI o Socket + 1 AF |24|
Complete Rules + TUI + GUI + RMI + Socket + 1 AF |27|
Complete Rules + TUI + GUI + RMI + Socket + 2 AF |30|
Complete Rules + TUI + GUI + RMI + Socket + 3 AF |30L|

## Achieved Goals

|  Functionality   | Status  |
|-----|---|
| Basic rules | ✅ 
| Complete rules | ✅ | 
| Socket | ✅ | 
| RMI | ✅ | 
| CLI | ✅ | 
| GUI | ✅ | 
| Multiple games | ✅ 
| Persistence | ❌ |  
| Resilience to disconnections | ✅ |
| Chat | ✅ |

# Running the game
## Building the artifacts
```mvn clean install```

JAR files can be found in the ```target``` folder
## Server
It is sufficient to run the server through the command
```java -jar AppServer.jar``` which creates a RMI connection 
on port 1099, and a socket connection on port 4445.
## Client
You can play the game through a GUI or a CLI interface. To run the program
you ```java -jar AppClientCLI.jar``` or ```java -jar AppClientGUI.jar``` 
followed by the connection type (```rmi```/```socket```)
and the IP of the server (x.x.x.x) (in case of local game
is sufficient entering ```localhost``` 
instead of the full address)

# Test Coverage
![coverage.png](src%2Fmain%2Fresources%2Fimages%2Fscreenshots%2Fcoverage.png)
