docker build --network=host -t file:5.1.2 .
docker save -o file.tar file:5.1.2
docker rmi file:5.1.2