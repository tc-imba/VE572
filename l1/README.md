## Lab 1

### Install

```bash
mvn install
```

### Run the server

```bash
mvn exec:java -Dexec.mainClass="TCPServer"
```

### Run the client

```bash
mvn exec:java -Dexec.mainClass="TCPClient" <host>
```