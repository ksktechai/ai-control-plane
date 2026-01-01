#!/bin/bash
set -e

echo "Starting AI Control Plane infrastructure..."

# Start Docker Compose
docker-compose up -d

echo "Waiting for PostgreSQL to be ready..."
until docker exec ai-postgres pg_isready -U aiuser -d aidb > /dev/null 2>&1; do
    sleep 1
done
echo "PostgreSQL is ready!"

echo "Waiting for Ollama to be ready..."
sleep 5
until curl -s http://localhost:11434/api/tags > /dev/null; do
    sleep 2
done
echo "Ollama is ready!"

echo ""
echo "Pulling required Ollama models (this may take a while)..."
docker exec ai-ollama ollama pull nomic-embed-text
docker exec ai-ollama ollama pull phi3:mini
docker exec ai-ollama ollama pull qwen2.5:7b
docker exec ai-ollama ollama pull llama3.1:8b

echo ""
echo "Infrastructure is ready!"
echo "PostgreSQL: localhost:5432 (user: aiuser, db: aidb)"
echo "Ollama: http://localhost:11434"
echo ""
echo "To stop: ./scripts/stop-infra.sh"
