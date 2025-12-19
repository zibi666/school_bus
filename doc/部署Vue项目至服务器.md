## 一、前端项目构建（本地）

### 1️⃣ 安装依赖并打包

```
npm install
npm run build
```

构建完成后生成目录：

```
dist/
├── index.html
├── assets/
```

------

## 二、将构建产物上传到服务器

### 2️⃣ 使用 scp 上传到服务器

```
scp -r dist/* ubuntu@118.195.133.25:/home/ubuntu/vue_app/
```

服务器目录结构：

```
/home/ubuntu/vue_app
├── index.html
├── assets/
```

------

## 三、服务器环境准备（Ubuntu）

### 3️⃣ 修复 apt 软件源（避免 404）

编辑源文件：

```
sudo nano /etc/apt/sources.list
```

内容设置为 Ubuntu 官方源：

```
deb http://archive.ubuntu.com/ubuntu jammy main restricted universe multiverse
deb http://archive.ubuntu.com/ubuntu jammy-updates main restricted universe multiverse
deb http://archive.ubuntu.com/ubuntu jammy-security main restricted universe multiverse
```

更新软件索引：

```
sudo apt clean
sudo apt update
```

------

## 四、安装并启动 Nginx

### 4️⃣ 安装 Nginx

```
sudo apt install nginx -y
```

### 5️⃣ 启动并设置开机自启

```
sudo systemctl start nginx
sudo systemctl enable nginx
```

验证：

```
nginx -v
```

------

## 五、配置 Nginx 部署 Vue 项目

### 6️⃣ 创建站点配置文件

```
sudo nano /etc/nginx/sites-available/vue_app
```

配置内容：

```
server {
    listen 80;
    server_name 118.195.133.25;

    root /home/ubuntu/vue_app;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

启用站点：

```
sudo ln -s /etc/nginx/sites-available/vue_app /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
```

测试并重启：

```
sudo nginx -t
sudo systemctl restart nginx
```

------

## 六、解决 500 错误（关键：权限问题）

### 7️⃣ 修改目录权限（保持路径不变）

```
sudo chmod 755 /home
sudo chmod 755 /home/ubuntu
sudo chmod -R 755 /home/ubuntu/vue_app
sudo chmod 644 /home/ubuntu/vue_app/index.html
```

原因：

- Nginx 运行用户为 `www-data`
- 必须拥有路径每一层目录的“进入权限”

------

## 七、访问验证

### 8️⃣ 浏览器访问

```
http://118.195.133.25
```

应看到：

✅ Vue 项目首页正常显示
 ✅ 刷新页面不 404（支持 history 路由）