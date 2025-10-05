# DVBE25 Demos

```bash
./mvnw clean install \
-Dmaven.repo.local=./local-m2
```

## Hello World

```bash
./mvnw clean compile exec:java -pl churrera/churrera-demo \
-Dexec.mainClass="info.jab.churrera.HelloWorldAgent" \
-Dmaven.repo.local=./local-m2
```

## Pi Calculus

```bash
./mvnw clean compile exec:java -pl churrera/churrera-demo \
-Dexec.mainClass="info.jab.churrera.PiAgent" \
-Dmaven.repo.local=./local-m2
```
