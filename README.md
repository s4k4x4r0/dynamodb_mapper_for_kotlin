# Kotlin/JVM Sample Project

このプロジェクトは、Docker 環境で実行できる Kotlin/JVM のサンプルプロジェクトです。

## 環境

- Kotlin: 2.1.0
- Java: Amazon Corretto 21
- Docker
- Docker Compose

## セットアップと実行方法

1. プロジェクトのクローン:

```bash
git clone <repository-url>
cd <project-directory>
```

2. Docker コンテナの起動:

```bash
docker-compose up -d --build
```

3. コンテナ内でアプリケーションを実行:

```bash
docker-compose exec app ./gradlew run
```

## テストの実行

```bash
docker-compose exec app ./gradlew test
```

## プロジェクトのビルド

```bash
docker-compose exec app ./gradlew build
```

## コンテナの停止

```bash
docker-compose down
```
