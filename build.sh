#!/bin/bash
rm -vrf build/libs && \
rm -vf ../data/mods/Kamibot-forge-1.20.1-0.0.1.jar && \
./gradlew build && \
mv -v build/libs/*-all.jar ../data/mods/Kamibot-forge-1.20.1-0.0.1.jar && \
docker compose -f ../docker-compose.yml up