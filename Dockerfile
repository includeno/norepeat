# For more information, please refer to https://aka.ms/vscode-docker-python
#docker pull python:3.8.6-slim
#版本查询 https://hub.docker.com/_/python?tab=tags
FROM python:3.8-slim-buster

EXPOSE 5000

# Keeps Python from generating .pyc files in the container
ENV PYTHONDONTWRITEBYTECODE=1

# Turns off buffering for easier container logging
ENV PYTHONUNBUFFERED=1

#https://github.com/bndr/pipreqs  打包依赖的工具pip install pipreqs
# pipreqs /home/project/location本路径可以用.(英文句号)代替 --encoding=utf8
#pipreqs C:\DataPosition\GitHub_Projects\norepeat --encoding=utf8 --force
# Install pip requirements
ADD requirements.txt .
RUN python -m pip install -r requirements.txt

WORKDIR /app
ADD . /app

# Switching to a non-root user, please refer to https://aka.ms/vscode-docker-python-user-rights
RUN useradd appuser && chown -R appuser /app
USER appuser

# During debugging, this entry point will be overridden. For more information, please refer to https://aka.ms/vscode-docker-python-debug
CMD ["gunicorn", "--bind", "0.0.0.0:5000", "flaskmain:app"]
