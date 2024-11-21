# CBD Lab 1

Sample workspace for completing the CBD Lab 1.

This workspace provides a docker-compose file to run the Redis server, and it's companions, in a dockerized enviromnment.

The [resources folder](resources) is automatically mounted to `/resources` in the MongoDB container.
It contains some assets required to complete the Lab.

## Docker

Start the redis server
`docker-compose up -d`

Open `redis-cli` on the container:
`docker-compose exec -it redis redis-cli`

Copy `.rediscli_history` from the redis container
`docker-compose cp redis:/root/.rediscli_history CBD-11-NMEC.txt`

Destroy the redis server
`docker-compose down -v`

## Java

This project uses Maven Wrapper.

Compile
`./mvnw package`

Clean: **Important** Execute the clean-up before 
`./mvnw clean`

## Delivery

Follow the instructions provided in the Lab guide.

**Important** Make sure you clean your working copy with `./mvnw clean` before generating the delivery `zip` package.

## Additional Notes

* Make sure you have previously installed Docker Desktop, or at least Docker Engine.
// TODO: Add Links
