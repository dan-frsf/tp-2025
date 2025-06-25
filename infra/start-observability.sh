#!/bin/bash

# Create observability network if it doesn't exist
docker network create observability 2>/dev/null || true

# Start observability stack
echo "Starting observability stack..."
docker compose -f observability-compose.yml up -d

echo "Waiting for services to be ready..."
sleep 10

echo "Observability stack started!"
echo ""
echo "Access URLs:"
echo "- Grafana: http://localhost:3000 (admin/admin)"
echo "- Prometheus: http://localhost:9090"
echo "- Loki: http://localhost:3100"
echo "- Tempo: http://localhost:3200"
echo ""
echo "To start microservices with observability:"
echo "docker compose -f docker-compose.full.yml up -d"
echo ""
echo "Or use the existing docker-compose.yml (requires network):"
echo "docker compose -f docker-compose.yml up -d"