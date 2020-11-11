# For more information, please refer to https://aka.ms/vscode-docker-python
#docker pull python:3.8.6-slim
#版本查询 https://hub.docker.com/_/python?tab=tags
FROM python:3.8-slim-buster

EXPOSE 5000

# Keeps Python from generating .pyc files in the container
ENV PYTHONDONTWRITEBYTECODE=1
# Turns off buffering for easier container logging
ENV PYTHONUNBUFFERED=1

# install git
RUN /bin/sh -c 'apt-get update  && apt-get upgrade -y  && apt-get install git -y'

WORKDIR /app
ADD . /app

# Switching to a non-root user, please refer to https://aka.ms/vscode-docker-python-user-rights
#RUN useradd appuser && chown -R appuser /app
#USER appuser

RUN /bin/sh -c 'cd /app && git clone https://github.com/includeno/norepeat.git'

# Install pip requirements
RUN /bin/sh -c 'cd /app/norepeat && python -m pip install -r requirements.txt '

#https://github.com/bndr/pipreqs  打包依赖的工具pip install pipreqs
# pipreqs /home/project/location本路径可以用.(英文句号)代替 --encoding=utf8
#pipreqs C:\DataPosition\GitHub_Projects\norepeat --encoding=utf8 --force

# During debugging, this entry point will be overridden. For more information, please refer to https://aka.ms/vscode-docker-python-debug
#CMD ["/bin/bash", "python", "/app/norepeat/flaskmain.py"]
CMD ["python", "/app/norepeat/flaskmain.py"]

#vscode 终端
#打包
#docker build . -t norepeat

#ssh工具
#运行
#docker run -it -p 5000:5000 --name norepeat_container norepeat
#删除镜像
#docker image rm norepeat
#停止容器
#docker stop norepeat_container
#删除容器
#docker rm norepeat_container

#容器内查看
#sudo docker exec -it norepeat_container /bin/bash
