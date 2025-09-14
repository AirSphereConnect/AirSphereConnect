#!/bin/bash
if [ -z "$1" ]; then
    echo "📋 Logs of all services:"
    docker-compose logs -f
else
    echo "📋 Logs of service $1:"
    docker-compose logs -f "$1"
fi