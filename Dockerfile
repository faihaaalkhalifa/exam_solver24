# المرحلة الأولى: بناء المشروع باستخدام Maven و Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# نسخ ملف إعدادات Maven أولاً لتسريع عملية البناء (Caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# نسخ باقي كود المشروع
COPY src ./src

# بناء المشروع وإنشاء ملف الـ jar (تجاهل الاختبارات لتسريع الرفع)
RUN mvn clean package -DskipTests

# المرحلة الثانية: تشغيل المشروع في بيئة خفيفة لتقليل استهلاك الرام
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# نسخ ملف الـ jar النهائي من المرحلة الأولى
COPY --from=build /app/target/*.jar app.jar

# فتح البورت الافتراضي 
EXPOSE 8080

# أمر تشغيل المشروع
ENTRYPOINT ["java", "-jar", "app.jar"]