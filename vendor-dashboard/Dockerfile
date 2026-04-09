# Build stage
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY ./ ./

# Build application
RUN npm run build

# Production stage
FROM node:20-alpine

LABEL maintainer="Marketplace Platform Team"
LABEL description="Vendor Dashboard - React Frontend"
LABEL version="1.0.0"

WORKDIR /app

# Install serve to run the application
RUN npm install -g serve

# Copy built application from builder
COPY --from=builder /app/dist ./dist

# Create non-root user
RUN addgroup -S appuser && \
    adduser -S appuser -G appuser && \
    chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:3000 || exit 1

# Set environment
ENV NODE_ENV=production

# Run application
CMD ["serve", "-s", "dist", "-l", "3000"]
