.PHONY: build test clean run-backend run-frontend

# Default target
all: build

# Build everything
build: build-backend build-frontend

# Test everything
test: test-backend test-frontend

# --- Backend (Spring Boot) ---
build-backend:
	@echo "Building Backend..."
	cd backend && ./gradlew clean build -x test

test-backend:
	@echo "Testing Backend..."
	cd backend && ./gradlew test

run-backend:
	@echo "Starting Backend..."
	cd backend && ./gradlew bootRun

# --- Frontend (React) ---
build-frontend:
	@echo "Building Frontend..."
	cd frontend && npm install && npm run build

test-frontend:
	@echo "Testing Frontend..."
	# Assuming npm test exists, or use tsc for check
	cd frontend && npm install && npm run build

run-frontend:
	@echo "Starting Frontend..."
	cd frontend && npm run dev

# --- Docker ---
docker-up:
	docker-compose up -d

docker-down:
	docker-compose down

clean:
	@echo "Cleaning up..."
	cd backend && ./gradlew clean
	rm -rf frontend/dist frontend/node_modules
