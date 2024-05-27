#!/bin/bash
rm -vrf release && \
mkdir -v release

# 1.20.1
rm -v gradle.properties && \
cp -v gradle.properties_1.20.1 gradle.properties && \
rm -vrf build/libs && \
./gradlew build && \
mv -v build/libs/*-all.jar release/Kamibot-forge-1.20.1-0.0.1.jar

# 1.20.2
rm -v gradle.properties && \
cp -v gradle.properties_1.20.2 gradle.properties && \
rm -vrf build/libs && \
./gradlew build && \
mv -v build/libs/*-all.jar release/Kamibot-forge-1.20.2-0.0.1.jar

# 1.20.3
rm -v gradle.properties && \
cp -v gradle.properties_1.20.3 gradle.properties && \
rm -vrf build/libs && \
./gradlew build && \
mv -v build/libs/*-all.jar release/Kamibot-forge-1.20.3-0.0.1.jar

# 1.20.4
rm -v gradle.properties && \
cp -v gradle.properties_1.20.4 gradle.properties && \
rm -vrf build/libs && \
./gradlew build && \
mv -v build/libs/*-all.jar release/Kamibot-forge-1.20.4-0.0.1.jar

# 1.20.6
rm -v gradle.properties && \
cp -v gradle.properties_1.20.6 gradle.properties && \
rm -vrf build/libs && \
./gradlew build && \
mv -v build/libs/*-all.jar release/Kamibot-forge-1.20.6-0.0.1.jar



# final 
rm -vrf build/libs && \
rm -v gradle.properties && \
cp -v gradle.properties_1.20.1 gradle.properties

