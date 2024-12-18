# Kotlin/JVM Sample Project

このプロジェクトは、Docker 環境で実行できる Kotlin/JVM のサンプルプロジェクトです。

## 環境

- Kotlin: 2.0.21
- Java: 21
- Docker
- Docker Compose
- VSCode + DevContainer

## 開発環境のセットアップ

### VSCode + DevContainer を使用する場合（推奨）

1. 必要なツールのインストール:

   - VSCode
   - Docker Desktop
   - VSCode Remote Development 拡張機能

2. プロジェクトを開く:

   - VSCode でプロジェクトを開く
   - 左下の「><」アイコンをクリックし、「Reopen in Container」を選択
   - DevContainer が自動的にビルドされ、開発環境が整います

### 従来の Docker Compose を使用する場合

1. プロジェクトのクローン:

```bash
git clone <repository-url>
cd <project-directory>
```

2. Docker コンテナの起動:

```bash
docker-compose up -d --build
```

3. コンテナにログイン:

```bash
docker-compose exec app bash
```

4. アプリケーションを実行:

```bash
./gradlew run
```

5. コンテナからログアウト:

```bash
exit
```

6. コンテナの停止

```bash
docker-compose down
```

## AWS 認証情報の設定

このプロジェクトは、環境変数を使用して AWS 認証情報を管理します。コンテナの作成時に、環境変数が自動的に Docker コンテナに渡されます。

**AWS 認証情報の設定手順:**

1. AWS 認証情報を設定します。AWS ドキュメントを参照してください。
2. 環境変数 `AWS_ACCESS_KEY_ID`、`AWS_SECRET_ACCESS_KEY`、`AWS_SESSION_TOKEN`、`AWS_REGION`、`AWS_DEFAULT_REGION` を設定します。
3. コンテナを作成(`docker-compose up -d`)します。

**注意:** コンテナのビルド前に、AWS 認証情報を設定する必要があります。認証情報が切れてしまった場合、上記の環境変数が設定されたホストのシェルで`docker-compose up -d`を実行すれば、コンテナが再作成されます。（ホストと同期しているディレクトリ以外の作業中のファイルは削除されます）
