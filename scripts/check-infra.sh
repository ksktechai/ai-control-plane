#!/bin/bash

echo "Checking AI Control Plane infrastructure status..."
echo ""

# Check Docker Compose
echo "Docker Compose services:"
docker-compose ps

echo ""
echo "PostgreSQL status:"
if docker exec ai-postgres pg_isready -U aiuser -d aidb > /dev/null 2>&1; then
    echo "  ✓ PostgreSQL is running and ready"
else
    echo "  ✗ PostgreSQL is not ready"
fi

echo ""
echo "Ollama status (host installation):"
if curl -s http://localhost:11434/api/tags > /dev/null 2>&1; then
    echo "  ✓ Ollama is running and ready"
    echo "  Available models:"
    curl -s http://localhost:11434/api/tags | grep -o '"name":"[^"]*"' | cut -d'"' -f4 | sed 's/^/    - /'
else
    echo "  ✗ Ollama is not ready"
    echo "  Please start Ollama:"
    echo "    macOS: Ollama should auto-start"
    echo "    Linux: systemctl start ollama"
fi

echo ""
