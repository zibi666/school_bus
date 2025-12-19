# 如何在云服务器部署springboot后端

## 第一步：打包成JAR文件

```bash
# Maven 项目
mvn clean package -DskipTests

# Gradle 项目
./gradlew build
```

打包之后的文件路径

- Maven: `target/your-project-name-0.0.1-SNAPSHOT.jar`
- Gradle:`build/libs/your-project-name-0.0.1-SNAPSHOT.jar`

## 第二步：上传JAR到云服务器

使用`scp`命令（在本地执行）

```bash
scp target/your-project-name-0.0.1-SNAPSHOT.jar root@你的服务器IP:/root/
```

或者直接拖拽

## 第三步：在云服务器上安装Java环境（必须）

最好使用写springboot后端使用的Java环境

可以直接在Java官网安装（记得解压位置）

**ubuntu安装JDK17**

```bash
sudo apt update
sudo apt install openjdk-17-jdk -y
```

**验证安装**

```bash
java -version
```

## 第四步：运行JAR并设置后台自启（systemctl推荐）

**1. 创建systemd服务文件**

```bash
sudo nano /etc/systemd/system/myspringapp.service
```

粘贴以下内容（请替换JAR文件名）

```Ini
[Unit]
Description=My Spring Boot App
After=syslog.target

[Service]
User=root
ExecStart=/usr/bin/java -jar /root/your-project-name-0.0.1-SNAPSHOT.jar   //注意java地址和你的JAR文件地址
//我的是ExecStart=/usr/jdk-21.0.8/bin/java -jar /root/login_test-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

**2.启动并设置开机自启**

```bash
sudo systemctl daemon-reload
sudo systemctl start myspringapp
sudo systemctl enable myspringapp
```

**3. 查看运行状态**

```bash
sudo systemctl status myspringapp
```

看到 `active (running)` 就成功了！

**4.查看实时日志（调试必备）：**

```bash
journalctl -u myspringapp -f
```

## 第五步：开放服务器端口

根据你的Spring Boot开放端口，你需要在云服务器安全组（阿里云/腾讯云控制台）中：

- **放行配置端口**
- 源地址：`0.0.0.0/0`（所有人可访问）或限制你的 IP

## 当我的后端代码发生改变应该怎么办

### 步骤1：修改代码重新打包

```bash
mvn clean package -DskipTests
```

### 步骤二：重新上传新的JAR到服务器

```bash
scp target/login_test-0.0.1-SNAPSHOT.jar root@139.224.16.12:/root/
```

### 步骤三：停止当前服务（重要！）

```bash
sudo systemctl stop myspringapp
```

### 步骤四：移动新JAR到正确位置

可以根据版本号重命名

例如`login_test-0.0.2-SNAPSHOT.jar`和`login_test-0.0.1-SNAPSHOT.jar`

但是确保和 `myspringapp.service` 里写的路径和文件名**完全一致**

### 步骤五：启动服务

```bash
sudo systemctl start myspringapp
```

### 步骤六：查看状态+日志

```bash
sudo systemctl status myspringapp
journalctl -u myspringapp -f
```

