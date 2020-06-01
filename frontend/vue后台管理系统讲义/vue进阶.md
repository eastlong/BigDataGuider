# vue数据交互
## json-server下载安装
1. 全局安装
```sh
npm install -g json-server
```
[github地址](https://github.com/typicode/json-server)

2. 建立一个db.json
```json
{
  "posts": [
    { "id": 1, "title": "json-server", "author": "typicode" }
  ],
  "comments": [
    { "id": 1, "body": "some comment", "postId": 1 }
  ],
  "profile": { "name": "typicode" }
}
```

3. 启动服务
```sh
# 默认3000端口
json-server --watch db.json

# 自定义端口
json-server --watch --port 3001 db.json
```

## RESTful接口规则





