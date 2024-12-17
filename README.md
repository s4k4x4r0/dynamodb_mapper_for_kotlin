# Kotlin/JVM Sample Project

このプロジェクトは、Docker 環境で実行できる Kotlin/JVM のサンプルプロジェクトです。

## 環境

- Kotlin: 2.1.0
- Java: Amazon Corretto 21
- Docker
- Docker Compose
- aws-vault（AWS 認証情報の管理）

## セットアップと実行方法

1. プロジェクトのクローン:

```bash
git clone <repository-url>
cd <project-directory>
```

2. Docker コンテナの起動:

```bash
aws-vault exec dev -- docker-compose up -d --build
```

3. aws-vault を使用してアプリケーションを実行:

```bash
# プロファイル名を指定して実行（例：dev）
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

## AWS 認証情報の設定

このプロジェクトは、aws-vault を使用して AWS 認証情報を管理します。以下の環境変数が自動的に Docker コンテナに渡されます：

- AWS_ACCESS_KEY_ID
- AWS_SECRET_ACCESS_KEY
- AWS_SESSION_TOKEN
- AWS_REGION
- AWS_DEFAULT_REGION

aws-vault のプロファイル設定が必要な場合は、以下のコマンドで設定できます：

```bash
aws-vault add dev  # devは任意のプロファイル名
```
