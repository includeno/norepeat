vscode 终端
打包镜像
docker build . -t norepeat:1.0


docker run -itd -p 9999:9999 --name norepeat_container norepeat:1.0
docker logs -ft norepeat_container
docker exec -it norepeat_container /bin/bash

docker stop norepeat_container
docker rm norepeat_container

docker image rm norepeat:1.0


requests_html
gunicorn