vscode 终端
打包镜像
docker build . -t includeno/norepeat_springboot:1.0.2

docker pull includeno/norepeat_springboot:1.0.2
docker run -itd -p 9999:8080 --name norepeat_springboot_container includeno/norepeat_springboot:1.0.2
docker logs -ft norepeat_springboot_container

docker exec -it norepeat_springboot_container /bin/bash

docker stop norepeat_springboot_container
docker rm norepeat_springboot_container

docker image rm includeno/norepeat_springboot:1.0.2
