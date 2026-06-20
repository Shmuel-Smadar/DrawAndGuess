# DrawAndGuess

## Prerequisites

To run this project, install:

- **Java 11** or newer
- **Node.js**: https://nodejs.org/en
- **Maven**: https://maven.apache.org/

On Windows, either add Maven to your environment variables or use `mvn.cmd` to execute Maven commands.

## Setup

Clone the repository, or download it as a ZIP file:

```bash
git clone https://github.com/Shmuel-Smadar/DrawAndGuess.git
cd DrawAndGuess
```

## Client

Install the client dependencies:

```bash
cd client
npm install
```

Run the client in development mode:

```bash
npm start
```

## Server

Compile the server:

```bash
cd server
mvn clean compile
```

Run the server:

```bash
mvn spring-boot:run
```

## Database Configuration

By default, the app does not use a database and stores data in memory.

To configure a database, update the `application.properties` file with your own database settings. The file should be located at:

```text
DrawAndGuess/server/src/main/resources/application.properties
```

If you are using the example config, remove the `.example` extension so Spring can load it.

## Enjoy

Have fun drawing and guessing.
