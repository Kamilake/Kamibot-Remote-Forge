#!/bin/bash
rm -vrf build/libs && \
rm -vf ../data/mods/app.jar && \
./gradlew build && \
mv -v build/libs/*.jar ../data/mods/app.jar && \
docker compose -f ../docker-compose.yml up