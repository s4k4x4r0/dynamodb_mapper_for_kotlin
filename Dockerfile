# 開発用のベースイメージを使用
FROM mcr.microsoft.com/devcontainers/java:21

ARG TARGETARCH

# パッケージのインストール
RUN apt-get install -y \
    curl \
    unzip

# Kotlinのインストール
ENV KOTLIN_VERSION=2.0.21
RUN curl -L https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip -o kotlin-compiler.zip && \
    unzip kotlin-compiler.zip -d /opt && \
    rm kotlin-compiler.zip
ENV PATH=$PATH:/opt/kotlinc/bin

# Gradleのインストール
ENV GRADLE_VERSION=8.5
RUN curl -L https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle.zip && \
    unzip gradle.zip -d /opt && \
    rm gradle.zip
ENV PATH=$PATH:/opt/gradle-${GRADLE_VERSION}/bin

# AWS CLIのインストール
RUN if [ "$TARGETARCH" = "amd64" ]; then \
    curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"; \
    else \
    curl "https://awscli.amazonaws.com/awscli-exe-linux-aarch64.zip" -o "awscliv2.zip"; \
    fi && \
    unzip awscliv2.zip && \
    sudo ./aws/install && \
    rm awscliv2.zip

# Build and run commands can be specified in docker-compose.yml or as CMD/ENTRYPOINT