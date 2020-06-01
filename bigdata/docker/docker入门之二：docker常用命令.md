<!-- TOC -->

- [1. docker常用命令](#1-docker常用命令)
    - [1.1. 基本命令](#11-基本命令)
    - [1.2. docker管理容器](#12-docker管理容器)
        - [1.2.1. 启动容器](#121-启动容器)
        - [进入容器](#进入容器)
        - [删除容器](#删除容器)

<!-- /TOC -->
# 1. docker常用命令
## 1.1. 基本命令
1. 获取镜像
```sh
[hadoop@hadoop101 docker]$ docker pull ubuntu:16.04
```

2. 运行镜像
```sh
[hadoop@hadoop101 docker]$ docker run -it --rm ubuntu:16.04 bash

[hadoop@hadoop101 docker]$ exit
```

参数说明：
- -it -i：交互式操作；-t：表示终端
- -rm 退出容器后删除容器
- bash表示进入交互式shell

3. 列出镜像
```sh
# 查看镜像
[hadoop@hadoop101 docker]$ docker images

# 查看镜像、容器、数据卷所占的空间
[hadoop@hadoop101 docker]$ docker system df

# 查看悬镜镜像 dangling images
[hadoop@hadoop101 docker]$ docker images ls -f dangling=true

# 悬镜镜像失去价值，可以删除
[hadoop@hadoop101 docker]$ docker image prune
```

4. 删除镜像
```sh
[hadoop@hadoop101 docker]$ docker ps -a
1c42f7a46920 hello-world "/hello" 

# 删除容器
[hadoop@hadoop101 docker]$ docker rm -f 1c42f7a46920

# 删除镜像
[hadoop@hadoop101 docker]$ docker rmi hello-world
```

## 1.2. docker管理容器
### 1.2.1. 启动容器
1. 新建并启动
```sh
[hadoop@hadoop101 docker]$ docker run ubuntu:16.04 /bin/echo 'Hello World'
# 新建
[hadoop@hadoop101 docker]$ docker run -it ubuntu:16.04 /bin/bash

[hadoop@hadoop101 docker]$ docker run -d hello-world

# 启动

[hadoop@hadoop101 docker]$ docker container start abc2351e845b

[hadoop@hadoop101 docker]$ docker container stop abc2351e845b
```

### 进入容器
```sh
[hadoop@hadoop101 docker]$ docker container start abc2351e845b

[hadoop@hadoop101 docker]$ docker exec -it abc2351e845b /bin/bash
```

### 删除容器
前提：容器处于终止状态
```sh
# 查看容器
[hadoop@hadoop101 docker]$ docker container ls -a

[hadoop@hadoop101 docker]$ docker ps -a

# 删除容器
[hadoop@hadoop101 docker]$ docker container rm f9a5537e738d
# 删除所有终止状态的容器
[hadoop@hadoop101 docker]$ docker container prune

```