pluginManagement {
    repositories {
        // 阿里云镜像（优先，加速国内下载）
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        // 官方源（fallback）
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // 阿里云镜像（优先）
        maven { url = uri("https://maven.aliyun.com/repository/google") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        // Apollo 专用仓库
        maven("https://apollographql.github.io/apollo-kotlin/kotlin-jvm")
        // 官方源（fallback）
        google()
        mavenCentral()
    }
}

rootProject.name = "pokemon-quiz-demo"
include(":app")
