#!/bin/bash
set -e

echo "Stopping AI Control Plane infrastructure..."
docker-compose down

echo "Infrastructure stopped!"
echo "To remove data volumes, run: docker-compose down -v"
