## Setup
- Need local databases to be similarly configured for pulling, pushing, and working with each other code
- Need to create a new docker postgres container for our project
- Make sure to stop any old postgres docker container to free up port 5432 on your machine, and then you can build and add docker container connection with that port
- application.properties file made with connection configuration to application
- to create new docker container, in terminal, run the command:
$ >docker run --name java-cafe-db -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres
- after docker connection created and added on vs code via database client extention, need to create database: cafe_db