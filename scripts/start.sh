#!/bin/bash
echo "Restart Air Sphere Connect!"

# Create .env if not exists
if [ ! -f .env ]; then
echo "Creating .env file"
cp .env.example .env
fi

# Stop the existing services
echo "Stopping old services"
docker-compose down

# Build and start complete
echo "Build and start services"
docker-compose up -d --build

# Wait for everything to be ready
echo "Waiting for services"
sleep 90

# Check status
echo "🔍 Checking services"
docker-compose ps

echo ""
echo "AirSphere Connect started!"
echo "Frontend: http://localhost:4200"
echo "Backend: http://localhost:8080"
echo "Base: localhost:3306"
echo ""
echo "Useful commands:"
echo "Logs: docker-compose logs -f [service]"
echo "Stop: ./scripts/stop.sh"