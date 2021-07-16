#环境配置
docker run -itd --name redis_container -p 6379:6379 --restart=always redis:6.0.13 redis-server --appendonly yes --requirepass "redis123456"
docker stop redis_container
docker rm redis_container

1.拉取线上版本
docker pull includeno/norepeat_springboot:1.0.3
vscode 终端

2.打包本地镜像  -e --dockerconfig=mac 表示docker是部署在Mac OS上的，-e --dockerconfig=Linux表示docker是部署在Linux服务器上的，
mvn clean package -Dmaven.test.skip=true
docker build . -t includeno/norepeat_springboot:1.0.3

docker buildx create --use --name mybuilder
docker buildx build --platform linux/amd64,linux/arm64 -t includeno/norepeat_springboot:1.0.3 --push .


docker run -itd -p 9999:8080 -e --dockerconfig=mac --restart=always --name norepeat_springboot_container includeno/norepeat_springboot:1.0.3
docker logs -ft norepeat_springboot_container


docker stop norepeat_springboot_container
docker rm norepeat_springboot_container
docker image rm includeno/norepeat_springboot:1.0.3


docker exec -it norepeat_springboot_container /bin/bash