FROM node:20-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
COPY . .
# Mount ~/.npmrc as a secret so Artifactory credentials are never stored in the image.
# Build with: docker compose build (secret is passed via docker-compose.override.yml)
RUN --mount=type=secret,id=npmrc,target=/root/.npmrc \
    npm ci --frozen-lockfile && npm run build

FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public
EXPOSE 3000
CMD ["node", "server.js"]
