# vue商城项目实战
## 一些资料
该项目前后端完整源码：  
* 前端：https://gitee.com/wBekvam/vue-shop-admin.git   
* 后端：https://gitee.com/wBekvam/vueShop-api-server.git   
希望对大家有帮助！

## 启动
1. 加载数据库文件

2. 在后端工程文件夹vueShop-api-server安装依赖
```sh
npm install 
```

3. 启动后端
在vueShop-api-server执行命令
```sh
node app.js
```

4. 全局安装vue-cli
```sh
npm install --global vue-cli



```

回车
? Install vue-router? Yes
? Use ESLint to lint your code? (Y/n) Y
? Set up unit tests No
? Setup e2e tests with Nightwatch? No

5. 安装项目
```sh
# 项目下载
vue init webpack mallmanager

npm install

# 启动项目
npm  run dev
```

# 开发
## 基础
### 安装使用element-ui
1. 安装
```sh
npm i element-ui -S
```

2. 引入
在main.js添加
```js
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)
```

### git版本控制
新建
git init .
git add .
git commit -m"vue项目启动"
git remote add origin https://github.com/eastlong/vue-mallmanager.git
git pull origin master
git push origin master -f

git checkout -b dev-login

### 
[vue格式化代码配置](https://www.cnblogs.com/llxpbbs/p/11393685.html)


