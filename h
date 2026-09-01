[1mdiff --git a/app/build.gradle.kts b/app/build.gradle.kts[m
[1mindex f47610a..1e38ba1 100644[m
[1m--- a/app/build.gradle.kts[m
[1m+++ b/app/build.gradle.kts[m
[36m@@ -59,9 +59,9 @@[m [mdependencies {[m
     implementation("androidx.navigation:navigation-compose:2.8.1")[m
 [m
     // Room[m
[31m-    implementation("androidx.room:room-runtime:2.6.1")[m
[31m-    implementation("androidx.room:room-ktx:2.6.1")[m
[31m-    ksp("androidx.room:room-compiler:2.6.1")[m
[32m+[m[32m    implementation("androidx.room:room-runtime:2.7.0")[m
[32m+[m[32m    implementation("androidx.room:room-ktx:2.7.0")[m
[32m+[m[32m    ksp("androidx.room:room-compiler:2.7.0")[m
 [m
     // DataStore (settings)[m
     implementation("androidx.datastore:datastore-preferences:1.1.1")[m
[1mdiff --git a/build.gradle.kts b/build.gradle.kts[m
[1mindex 4c2dc76..9716f3a 100644[m
[1m--- a/build.gradle.kts[m
[1m+++ b/build.gradle.kts[m
[36m@@ -3,5 +3,5 @@[m [mplugins {[m
     id("org.jetbrains.kotlin.android") version "2.0.20" apply false[m
     id("org.jetbrains.kotlin.plugin.compose") version "2.0.20" apply false[m
     id("org.jetbrains.kotlin.plugin.serialization") version "2.0.20" apply false[m
[31m-    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false[m
[32m+[m[32m    id("com.google.devtools.ksp") version "2.0.20-1.0.28" apply false[m
 }[m
