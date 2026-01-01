#!/bin/bash
set -e

echo "Starting AI Control Plane infrastructure..."

# Start Docker Compose (PostgreSQL only)
docker-compose up -d

echo "Waiting for PostgreSQL to be ready..."
until docker exec ai-postgres pg_isready -U aiuser -d aidb > /dev/null 2>&1; do
    sleep 1
done
echo "PostgreSQL is ready!"

echo ""
echo "Checking Ollama installation (host)..."
if ! command -v ollama &> /dev/null; then
    echo "ERROR: Ollama is not installed on the host."
    echo "Please install Ollama from: https://ollama.ai"
    exit 1
fi

echo "Waiting for Ollama to be ready..."
until curl -s http://localhost:11434/api/tags > /dev/null; do
    echo "Ollama not responding. Please start Ollama:"
    echo "  macOS: Ollama should auto-start, or run 'ollama serve'"
    echo "  Linux: systemctl start ollama"
    sleep 2
done
echo "Ollama is ready!"

echo ""
echo "Checking required Ollama models..."
REQUIRED_MODELS=("nomic-embed-text" "phi3:mini" "qwen2.5:7b")
MISSING_MODELS=()

for model in "${REQUIRED_MODELS[@]}"; do
    if ! ollama list | grep -q "$model"; then
        MISSING_MODELS+=("$model")
    fi
done

if [ ${#MISSING_MODELS[@]} -ne 0 ]; then
    echo "Missing models: ${MISSING_MODELS[@]}"
    echo "Pulling missing models (this may take a while)..."
    for model in "${MISSING_MODELS[@]}"; do
        ollama pull "$model"
    done
else
    echo "All required models are available!"
fi

echo ""
echo "Infrastructure is ready!"
echo "PostgreSQL: localhost:5433 (user: aiuser, db: aidb)"
echo "Ollama: http://localhost:11434 (host installation)"
echo ""
echo "To stop: ./scripts/stop-infra.sh"
