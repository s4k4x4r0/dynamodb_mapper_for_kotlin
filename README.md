# Kotlin DynamoDB Mapper サンプルプロジェクト

このプロジェクトは、Docker 環境で実行できる Kotlin DynamoDB Mapper のサンプルプロジェクトです。

## 必要な環境

- Docker
- Docker Compose
- VSCode + DevContainer

GitHub Codespaces からの実行も可能です。

## 開発環境のセットアップ

### GitHub Codespaces を使用する場合（推奨）

   - GitHubのリポジトリページにアクセス
   - 「Code」からCodespacesを起動する

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

1. Docker コンテナの起動:

   ```bash
   docker-compose up -d --build
   ```

2. コンテナにログイン:

   ```bash
   docker-compose exec app bash
   ```

3. コンテナ内で任意の操作が可能

4. コンテナからログアウト:

   ```bash
   exit
   ```

5. コンテナの停止:

   ```bash
   docker-compose down
   ```

## コンテナ内でアプリを実行する方法

1. AWS 認証情報を設定:

   ```bash
   aws configure
   ```

   このコマンドを実行する代わりに、ホスト側で次の環境変数を設定した状態で、コンテナを作成してもよいです。（環境変数が自動で引き継がれます）
   
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`
   - `AWS_SESSION_TOKEN`
   - `AWS_REGION`
   - `AWS_DEFAULT_REGION`

2. アプリケーションをビルドして実行:

   ```bash
   ./gradlew run
   ```