mkdir "./target/encrypted"
./encrypt -encrypt -in ./target/trans-extends-0.0.1-SNAPSHOT.jar -out ./target/encrypted/app.jar
docker build --network=host -t file:5.1.4 .
docker save -o file.tar file:5.1.4
docker rmi file:5.1.4