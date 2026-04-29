RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Stock Market Simulator - HA Setup${NC}"
echo -e "${GREEN}========================================${NC}"

if ! command -v docker &> /dev/null; then
    echo -e "${RED}Error: Docker is not installed${NC}"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo -e "${RED}Error: Docker Compose is not installed${NC}"
    exit 1
fi

PORT=${1:-8080}
echo -e "${YELLOW}Using port: ${PORT}${NC}"

export API_PORT=${PORT}

echo -e "${YELLOW}Stopping existing containers...${NC}"
docker-compose down 2>/dev/null

echo -e "${YELLOW}Building and starting containers...${NC}"
docker-compose up --build -d

echo -e "${YELLOW}Waiting for services to start...${NC}"
sleep 10

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Services status:${NC}"
docker-compose ps

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Application is running!${NC}"
echo -e "${YELLOW}Available on: http://localhost:${PORT}${NC}"
echo -e "${YELLOW}Available instances:${NC}"
echo -e "  Instance 1: http://localhost:8081"
echo -e "  Instance 2: http://localhost:8082"
echo -e "  Instance 3: http://localhost:8083"
echo -e ""
echo -e "${YELLOW}PostgreSQL: localhost:5432${NC}"
echo -e "${YELLOW}Swagger UI: http://localhost:8081/swagger-ui.html${NC}"
echo -e "${GREEN}========================================${NC}"

echo -e ""
echo -e "${YELLOW}Testing High Availability...${NC}"
echo -e "Kill instance 1 (POST /chaos):"
echo -e "  curl -X POST http://localhost:8081/chaos -H \"X-API-Key: remitly-internship-2026-secret-key\""
echo -e ""
echo -e "Check if instance 2 still works:"
echo -e "  curl -H \"X-API-Key: remitly-internship-2026-secret-key\" http://localhost:${PORT}/stocks"
echo -e ""
echo -e "${GREEN}To stop all containers: docker-compose down${NC}"