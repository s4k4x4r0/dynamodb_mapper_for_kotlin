FROM amazoncorretto:21

# Install required packages
RUN yum install -y tar gzip

# Install Kotlin
ENV KOTLIN_VERSION=2.1.0
RUN curl -L https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip -o kotlin-compiler.zip && \
    unzip kotlin-compiler.zip -d /opt && \
    rm kotlin-compiler.zip
ENV PATH=$PATH:/opt/kotlinc/bin

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build and run commands can be specified in docker-compose.yml or as CMD/ENTRYPOINT 