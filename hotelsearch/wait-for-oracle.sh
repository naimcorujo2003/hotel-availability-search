#!/bin/bash
echo "Waiting for Oracle to be ready..."
sleep 90
echo "Starting application..."
exec java -jar app.jar