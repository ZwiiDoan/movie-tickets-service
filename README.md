# Movie Tickets Service

### Requirements

Implement a Java application which will expose an API call to take transaction as input and return below information:

* The ID of the transaction
* Each individual type of movie ticket present in that transaction, ordered alphabetically, and it's quantity and total
  cost
* The total cost of all movie tickets for that transaction

Sample Request:

```json
{
  "transactionId": 2,
  "customers": [
    {
      "name": "John Smith",
      "age": 36
    },
    {
      "name": "Jane Smith",
      "age": 3
    },
    {
      "name": "George Smith",
      "age": 8
    },
    {
      "name": "Brad Smith",
      "age": 9
    },
    {
      "name": "Adam Smith",
      "age": 17
    }
  ]
}
```

Sample Response:

```json
{
  "transactionId": 2,
  "tickets": [
    {
      "ageGroup": "Adult",
      "quantity": 1,
      "totalCost": 25.00
    },
    {
      "ageGroup": "Children",
      "quantity": 3,
      "totalCost": 11.25
    },
    {
      "ageGroup": "Teen",
      "quantity": 1,
      "totalCost": 12.00
    }
  ],
  "totalCost": 48.25
}
```

### Implementation Details

Different ticket prices and discounts can be configured in [application.yml](src/main/resources/application.yml)

* Ticket groups can be configured by AgeGroup (`Adult`, `Teen`, `Children`, `Senior`) with an absolute price such as

```yaml
  tickets:
    Adult:
      minAge: 18
      maxAge: 64
      price: 25
```

* Or with derived price from another group such as:

```yaml
  tickets:
    Senior:
      minAge: 65
      baseGroup: Adult
      adjustPercent: -30
```

* A missing `minAge` would be set to default value `0` whereas a missing `maxAge` would be set to default
  value `Integer.MAX_VALUE`.

* Discounts are also configured by AgeGroup and a minimum number of tickets to activate the discount, for instance:

```yaml
  discounts:
    Children:
      adjustPercent: -25
      minTickets: 3
```

The API Request body is validated by following rules:

* `transactionId` must be a positive number
* `customers` list must not be empty and must have unique customer names
* `customer.name` must not be empty or blank
* `customer.age` must be a positive number

`totalCost` in responses are rounded to nearest cent.

### Build

Execute from repo's root directory

```shell
/.gradlew clean build
```

The build pipeline integrates different tools to verify code quality and security such as:

* [Checkstyle](https://checkstyle.sourceforge.io/) to ensure coding standard using Google Checkstyle
  at [config/checkstyle/checkstyle.xml](config/checkstyle/checkstyle.xml)
* [Spotbugs](https://spotbugs.github.io/) with plugin [FindSecBugs](https://find-sec-bugs.github.io/) that do static
  analysis to look for common bugs and security bugs in Java code
* [Jacoco](https://github.com/jacoco/jacoco) to ensure code and test coverage. The minimum instruction
  and branch coverage of this repo is 95% which is satisfied by unit tests and integration tests.
* [OWASP dependency-check-gradle plugin](https://plugins.gradle.org/plugin/org.owasp.dependencycheck) to scan for CVEs
  in the project dependencies. This task is not a part of default build task as it takes a lot of time. However, it can
  be executed separately by command `./gradlew dependencyCheckAnalyze`.

### Local Run/Demo

Execute from repo's root directory

```shell
/.gradlew bootRun
```

User can submit a Ticket Transaction with this curl command:

```shell
curl --location 'http://localhost:8080/v1/ticket-transaction' \
--header 'Correlation-Id: 38198fa6-5c91-4808-8e39-96dc01212400' \
--header 'Content-Type: application/json' \
--data '{
    "transactionId": 1,
    "customers": [
        {
            "name": "John Smith",
            "age": 70
        },
                {
            "name": "Jane Smith",
            "age": 5
        },
        {
            "name": "George Smith",
            "age": 6
        }
    ]
}'
```

You can also import the Postman collection in [postman](postman) folder to interact with the demo application.

### Future Improvements & Considerations

1. Error response schema can be adjusted to include more fields and show/hide details if required by enterprise
   standards or business requirements.
2. More Observability tools can be integrated to provide distributed tracing, logging, metrics
   with [Spring Boot 3 Observability](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3) such as
   [Prometheus](https://prometheus.io/), [Grafana]https://grafana.com/, [Loki](https://github.com/loki4j/loki-logback-appender),
   etc.
