#!/bin/bash
echo "🔍 AirSphere Connect Services Status:"
echo ""
docker-compose ps
echo ""
echo "🌐 Available URLs:"
echo "Frontend: http://localhost:4200"
echo "Backend: http://localhost:8080/actuator/health"
echo "Base: localhost:3306"