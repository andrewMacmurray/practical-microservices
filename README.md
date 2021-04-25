# Practical Microservices

implementation of CQRS app in [practical-microservices](https://pragprog.com/titles/egmicro/practical-microservices/)
book using `Spring Boot` and `Axon`

## Run the App

+ Start `Axon Server` with `./scripts/axon` (make sure you have Docker running / installed)
+ Start the `Spring Boot` server with `./gradlew bootRun`
+ Visit `http://localhost:8080` to see the app

## Run the tests

+ Run tests with `./gradlew test`

## Caveats

+ The UI looks dreadful
+ Auth only checks for correct email
+ Tests only cover `Aggregates` and `Sagas`
