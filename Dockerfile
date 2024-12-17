FROM amazoncorretto:21

# Install required packages
RUN yum install -y tar gzip unzip

# Install Kotlin
ENV KOTLIN_VERSION=2.1.0
RUN curl -L https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip -o kotlin-compiler.zip && \
    unzip kotlin-compiler.zip -d /opt && \
    rm kotlin-compiler.zip
ENV PATH=$PATH:/opt/kotlinc/bin

# Install Gradle
ENV GRADLE_VERSION=8.5
RUN curl -L https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle.zip && \
    unzip gradle.zip -d /opt && \
    rm gradle.zip
ENV PATH=$PATH:/opt/gradle-${GRADLE_VERSION}/bin

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Initialize Gradle Wrapper
RUN gradle wrapper

# Make gradlew executable
RUN chmod +x gradlew

# Build and run commands can be specified in docker-compose.yml or as CMD/ENTRYPOINT