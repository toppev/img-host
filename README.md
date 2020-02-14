# img-host
An image hosting server written in Kotlin using Ktor framework. Made for my screenshot software [screen-capture](https://github.com/toppev/screen-capture).

## Features
- Simple home page
- HTTP POST to upload images
- Saves upload date, last viewed and number of views in MongoDB
- Browse images by identifier/token (with paging)

## Installation
1. Clone this repository with `git clone https://github.com/toppev/img-host.git` or using SSH `git clone git@github.com:toppev/img-host.git`
2. Configure `database.properties`
3. Use `./gradlew build` to build the project. You can find the `image-host-all.jar` in `./build/libs` directory
4. Either run the jar with `java -jar <jar file>` (requires MongoDB instance!) or run with [Docker Compose](https://docs.docker.com/compose/): configure `docker-compose.yml` and run `docker-compose up` (will also start the MongoDB instance).