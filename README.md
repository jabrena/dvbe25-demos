# DVBE25 Demos

## Hello World

```bash
./mvnw clean install
./mvnw clean compile exec:java -pl churrera/churrera-demo \
-Dexec.mainClass="info.jab.churrera.HelloWorldAgent"
```

## Pi Calculus

```bash
./mvnw clean install
./mvnw clean compile exec:java -pl churrera/churrera-demo \
-Dexec.mainClass="info.jab.churrera.PiAgent"
```
