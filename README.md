# DrawAndGuess
## Prerequisites
You need at least **Java 11** or above to run the code.  
You need to use nodejs to compile the code. https://nodejs.org/en
You need to use maven to compile the code. https://maven.apache.org/ 
(either add it to the environment variables, or use mvn.cmd to excecute mvn commands on windows)
## Building Client
```
git clone https://github.com/Shmuel-Smadar/DrawAndGuess.git (or use zip file)
cd DrawAndGuess-main
cd client
npm install
```
## Building Server
```
git clone https://github.com/Shmuel-Smadar/DrawAndGuess.git (or use zip file)
cd DrawAndGuess-main
cd server
mvn clean compile
```

To get the client running in development environment, excecute inside the client folder: 
```
npm start
```
To get the server running, execute inside the server folder 
```
mvn spring-boot:run 
```

You can configure the server to use your database by changing application.properties variables to your own db set up.
the file needs to be in "DrawAndGuess/server/src/main/resources" (delete the '.example' extention for it to be used)

by default the app will not use db and will save data in ram.

enjoy :)
