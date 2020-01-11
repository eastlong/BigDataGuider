<!-- TOC -->

- [1. git教程](#1-git教程)
    - [1.1. git简介](#11-git简介)
        - [1.1.1. 基本介绍](#111-基本介绍)
        - [1.1.2. 安装git](#112-安装git)
        - [1.1.3. 创建版本库](#113-创建版本库)
- [2. 版本管理进阶](#2-版本管理进阶)
    - [2.1. 时光穿梭机](#21-时光穿梭机)
    - [2.2. 版本退回](#22-版本退回)
    - [2.3. 工作区和暂存区](#23-工作区和暂存区)
        - [2.3.1. 工作区（working Directory）](#231-工作区working-directory)
        - [2.3.2. 版本库（Repository）](#232-版本库repository)
    - [2.4. 管理修改](#24-管理修改)
    - [2.5. 撤销修改](#25-撤销修改)
    - [2.6. 删除文件](#26-删除文件)
- [3. 远程仓库](#3-远程仓库)
    - [3.1. 添加远程仓库](#31-添加远程仓库)
    - [从远程库克隆](#从远程库克隆)
- [分支管理](#分支管理)
    - [创建与合并分支](#创建与合并分支)

<!-- /TOC -->
 **参考文档**  
[廖雪峰Git教程](https://www.liaoxuefeng.com/wiki/896043488029600)

# 1. git教程
## 1.1. git简介
### 1.1.1. 基本介绍
&emsp;&emsp;Git是目前世界上最先进的**分布式版本控制系统**。  
&emsp;&emsp;SVN是集中式版本控制系统。  
* 集中式版本控制系统

<div align="center"><a><img width="300" heigth="300" src="imgs/git/1/01.jpg"></a></div>  
SVN、CVS

* 分布式版本控制系统
<div align="center"><a><img width="300" heigth="300" src="imgs/git/1/02.jpg"></a></div>  

（1）分布式版本控制系统根本没有“中央服务器”  
（2）分布式版本控制系统的安全性要高很多  
（3）分布式版本控制系统通常也有一台充当“中央服务器”的电脑，但这个服务器的作用仅仅是用来**方便“交换”大家的修改**，没有它大家也一样干活，只是交换修改不方便而已。

### 1.1.2. 安装git
* 环境变量配置  
~/git/bin 添加进环境变量PATH即可。  

### 1.1.3. 创建版本库
&emsp;&emsp;版本库又名**仓库**，英文名**repository**，你可以简单理解成一个目录，这个目录里面的所有文件都可以被Git管理起来，每个文件的修改、删除，Git都能跟踪，以便任何时刻都可以追踪历史，或者在将来某个时刻可以“还原”。  
* step1:创建空目录：
```shell
$ mkdir learngit
$ cd learngit
$ pwd
/Users/michael/learngit
```
* step2:初始化
```shell
git init
```
* step3：把文件添加到版本库
```shell
## 告诉Git，把文件添加到仓库
git add readme.txt
## 告诉Git，把文件提交到仓库：
git commit -m "write a file"
```

**注意：指定目录打开git bash**
```shell
start ‪E:\soft2\Git\app\git-bash.exe
```

# 2. 版本管理进阶
## 2.1. 时光穿梭机
当文件修改后但未提交
```shell
## 查看git状态
$ git status
On branch master
Changes not staged for commit:
  (use "git add <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)

        modified:   readme.txt

no changes added to commit (use "git add" and/or "git commit -a")

## 查看不同
$ git diff readme.txt
diff --git a/readme.txt b/readme.txt
index 4c9d7c6..20cde07 100644
--- a/readme.txt
+++ b/readme.txt
@@ -3,4 +3,6 @@ spark
-my boss is very nice
\ No newline at end of file
+hell chenying
\ No newline at end of file
## 提交
$ git add readme.txt
$ git commit -m "add text"
```

## 2.2. 版本退回

上一个版本就是**HEAD^**，上上一个版本就是**HEAD^^**，当然往上100个版本写100个^比较容易数不过来，所以写成**HEAD~100**
```shell
## 1 查看log
$ git log

$ git log --pretty=oneline
f2dcc37be6bc22ecf199605f37c5deb4dfdaa780 (HEAD -> master) add hive
fb6e6cf6bebe76fab720a35b45862727c90f2c72 add text
809d37333217d505798d9b105cc2306a96ffb8d0 write a file

## 2 退回上一个版本

$ git reset --hard HEAD^

## 3 后悔了，想找回原来的版本
$ git reflog
fb6e6cf (HEAD -> master) HEAD@{0}: reset: moving to HEAD^
f2dcc37 HEAD@{1}: commit: add hive
fb6e6cf (HEAD -> master) HEAD@{2}: commit: add text
809d373 HEAD@{3}: commit (initial): write a file

$ git reset --hard f2dcc37
HEAD is now at f2dcc37 add hive

```
**总结：**
1) git log
2) git reflog

## 2.3. 工作区和暂存区
Git和其他版本控制系统如SVN的一个不同之处就是**有暂存区的概念**。 

### 2.3.1. 工作区（working Directory）
相当于一个目录，一个同步的文件夹。比如之前创建的**learngit**文件夹

### 2.3.2. 版本库（Repository）
工作区有一个隐藏目录.git，这个不算工作区，而是Git的版本库。
* stage（暂存区）  
Git的版本库里存了很多东西，其中最重要的就是称为stage（或者叫index）的**暂存区**，还有Git为我们**自动创建的第一个分支master**，以及指向master的一个指针叫HEAD。

<div align="center"><a><img width="400" heigth="300" src="imgs/git/1/03.jpg"></a></div>  

* git add命令  
把要提交的所有修改放到暂存区（Stage）

<div align="center"><a><img width="400" heigth="300" src="imgs/git/1/04.jpg"></a></div>  

* git commit命令  
一次性把暂存区的所有修改提交到分支。

<div align="center"><a><img width="400" heigth="300" src="imgs/git/1/05.jpg"></a></div>  

## 2.4. 管理修改

如下过程：  
第一次修改 -> git add -> 第二次修改 -> git commit  
结果：第一次的修改被提交了，第二次的修改不会被提交。
用下面令可以查看工作区和版本库里面最新版本的区别：
```git
git diff HEAD -- readme.txt
```
**总结：**  
如果不用git add到暂存区，那就不会加入到commit中。

## 2.5. 撤销修改
* 当你修改了文件内容，还没来得及执行 git add 操作
```shell
# 撤销操作：git checkout -- file 使文件回到修改之前， 
$ git checkout -- readme.txt
```
* 当你修改了文件内容，并执行git add 操作提交到暂存区：
```shell
# 撤销操作：git reset
$ git reset HEAD readme.txt
```

## 2.6. 删除文件
创建新的文件，test.txt并git add提交了：
```shell
$ git add text.txt

# 删除
$ rm text.txt
```
（1）确实需要删除test.txt
```shell
# 从版本库中删除
$ git rm text.txt
# 提交
$ git commit  -m "remove a file"
```
（2）错误删除了文件
但是版本库中还有文件的修改
```git
$ git checkout -- test.txt
```

# 3. 远程仓库
使用github远程仓库。当不同电脑登录github并需要同步代码时，必须要执行如下操作：  
(1) 创建ssh-key  
在用户主目录下，看看有没有隐藏的.ssh目录：id_rsa和id_rsa.pub文件。
```shell
$ ssh-keygen -t rsa -C "youremail@example.com"
```
**说明**  
id_rsa和id_rsa.pub两个文件，这两个就是SSH Key的**秘钥对**，id_rsa是私钥，不能泄露出去，id_rsa.pub是公钥，可以放心地告诉任何人。
(2) 登录github，打开Settings --> ssh and GPG keys --> New SSh key --> 把id_rsa.pub内容复制进去。

## 3.1. 添加远程仓库
1. github创建仓库很容易。创建learngit仓库；
2. 将已有的本地仓库与github上的仓库关联：  
```shell
## 在本地仓库 learngit 下执行
$ git remote add origin git@github.com:michaelliao/learngit.git
```
3. 把本地内容推送到远程仓库：
```shell
$ git push -u origin master
```

**总结**

要关联一个远程库，使用命令
```git
git remote add origin git@server-name:path/repo-name.git
```
关联后，使用命令: 第一次推送master分支的所有内容；
```
git push -u origin master
```
此后，每次本地提交后，只要有必要，就可以使用命令:
```
git push origin master推送最新修改
```

## 从远程库克隆
你也许还注意到，GitHub给出的地址不止一个，还可以用https://github.com/michaelliao/gitskills.git这样的地址。实际上，Git支持多种协议，默认的git://使用ssh，但也可以使用https等其他协议。

使用https除了速度慢以外，还有个最大的麻烦是每次推送都必须输入口令，但是在某些只开放http端口的公司内部就无法使用ssh协议而只能用https。

```
$ git clone git@github.com:michaelliao/gitskills.git
```
**总结**  
要克隆一个仓库，首先必须知道仓库的地址，然后使用git clone命令克隆。Git支持多种协议，包括https，但通过ssh支持的原生git协议速度最快。

# 分支管理
## 创建与合并分支
