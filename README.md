# E-wallet-Application
Developed using Java Spring Boot framework, Apache Kafka, Hibernate, Spring security, Redis(Cache), SQL(database)

This E-wallet App is built following microservice architecture model which includes four services 
1. User Service -- Responsible for new user singup, account creation with new wallet and handles authentication and authorization requests.
2. Transaction Service -- Handles transactions and communicates with all three other services through both pub-sub model(using Kafka) and direct access based on the criticality of process.
3. Wallet Service -- Stores updated balances as data is pushed to respective kafka topics by Transaction service.
4. Notification Service -- Sends Email notifications when transactions are succesful or failed.
