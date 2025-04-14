mkdir lib
curl https://repo1.maven.org/maven2/org/glassfish/jakarta.faces/4.1.1/jakarta.faces-4.1.1.jar > lib/jakarta.faces-4.1.1.jar
curl https://repo1.maven.org/maven2/org/jetbrains/annotations/26.0.2/annotations-26.0.2.jar > lib/annotations-26.0.2.jar
curl https://repo1.maven.org/maven2/org/glassfish/jakarta.faces/4.1.1/jakarta.faces-4.1.1.jar > lib/jakarta.faces-4.1.1.jar
curl https://repo1.maven.org/maven2/jakarta/faces/jakarta.faces-api/4.1.1/jakarta.faces-api-4.1.1.jar > lib/jakarta.faces-api-4.1.1.jar
curl https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.4/postgresql-42.7.4.jar > lib/postgresql-42.7.4.jar
curl https://repo1.maven.org/maven2/jakarta/platform/jakarta.jakartaee-api/10.0.0/jakarta.jakartaee-api-10.0.0.jar > lib/jakarta.jakartaee-api-10.0.0.jar
curl https://repo1.maven.org/maven2/org/primefaces/primefaces/14.0.6/primefaces-14.0.6-jakarta.jar > lib/primefaces-14.0.6-jakarta.jar
curl https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/1.9.25/kotlin-stdlib-1.9.25.jar > lib/kotlin-stdlib-1.9.25.jar
curl https://repo1.maven.org/maven2/org/jetbrains/kotlinx/kotlinx-coroutines-core/1.9.0/kotlinx-coroutines-core-1.9.0.jar > lib/kotlinx-coroutines-core-1.9.0.jar
wget https://github.com/JetBrains/kotlin/releases/download/v2.1.20/kotlin-compiler-2.1.20.zip
unzip kotlin-compiler-2.1.20.zip -d kotlin-compiler

curl https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar > lib/junit-4.13.2.jar
curl https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar > lib/hamcrest-core-1.3.jar
curl https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-test-junit/2.0.21/kotlin-test-junit-2.0.21.jar > lib/kotlin-test-junit-2.0.21.jar
curl https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-test/2.0.21/kotlin-test-2.0.21.jar > lib/kotlin-test-2.0.21.jar