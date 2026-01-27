.PHONY: all build test clean run-backend run-frontend build-backend build-frontend test-backend test-frontend docker-up docker-down format format-backend format-frontend

# Default target
all: build

# Build everything
build: build-backend build-frontend

# Test everything
test: test-backend test-frontend

# Format everything
format: format-backend format-frontend

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

format-backend:
	@echo "Formatting Backend..."
	cd backend && ./gradlew spotlessApply

# --- Frontend (React) ---
build-frontend:
	@echo "Building Frontend..."
	cd frontend && npm install && npm run build

test-frontend:
	@echo "Testing Frontend..."
	cd frontend && npm install && npm run test

run-frontend:
	@echo "Starting Frontend..."
	cd frontend && npm run dev

format-frontend:
	@echo "Formatting Frontend..."
	npm run format

# --- Docker ---
docker-up:
	docker-compose up -d

docker-down:
	docker-compose down

clean:
	@echo "Cleaning up..."
	cd backend && ./gradlew clean
	rm -rf frontend/dist frontend/node_modules
