#版本查询 https://hub.docker.com/_/python?tab=tags
FROM python:3.9.4

EXPOSE 9999

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

#RUN /bin/sh -c 'cd /app && git clone https://github.com/includeno/norepeat.git'


# Install pip requirements
RUN /bin/sh -c ' cd /app && python -m pip install -r /app/requirements.txt '

# During debugging, this entry point will be overridden. For more information, please refer to https://aka.ms/vscode-docker-python-debug
CMD ["python", "/app/flaskmain.py"]