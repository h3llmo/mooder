FROM node:20-alpine AS deps
WORKDIR /app
COPY package.json ./
# npm install generates the lockfile during image build.
# Once you run `npm install` locally and commit package-lock.json,
# switch this to `npm ci --frozen-lockfile` for reproducible builds.
RUN npm install

FROM node:20-alpine AS builder
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

FROM node:20-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public
EXPOSE 3000
CMD ["node", "server.js"]
