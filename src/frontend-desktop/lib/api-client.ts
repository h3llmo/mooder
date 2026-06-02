/**
 * Mooder API client (server-side / BFF layer).
 *
 * All fetch calls are made from Next.js server components or route handlers —
 * never directly from the browser. This enforces the BFF pattern (ADR-004):
 * tokens stay server-side, the browser only ever receives cookies.
 *
 * Base URL: MOODER_API_URL env var (internal docker network address in docker,
 * http://localhost:8080 in standalone dev).
 */

const BASE_URL = process.env.MOODER_API_URL ?? "http://localhost:8080";

async function apiFetch<T>(
  path: string,
  options: RequestInit & { patternHash?: string } = {}
): Promise<T> {
  const { patternHash, ...fetchOptions } = options;

  const headers: HeadersInit = {
    "Content-Type": "application/json",
    ...(patternHash ? { "X-Mooder-Pattern": patternHash } : {}),
    ...(fetchOptions.headers ?? {}),
  };

  const res = await fetch(`${BASE_URL}${path}`, { ...fetchOptions, headers });

  if (!res.ok) {
    throw new Error(`API error ${res.status} on ${path}`);
  }

  return res.json() as Promise<T>;
}

export const apiClient = {
  chat: {
    listConversations: (patternHash?: string) =>
      apiFetch("/api/v1/chat/conversations", { patternHash }),

    listMessages: (conversationId: string, patternHash?: string) =>
      apiFetch(`/api/v1/chat/conversations/${conversationId}/messages`, { patternHash }),

    sendMessage: (conversationId: string, content: string) =>
      apiFetch(`/api/v1/chat/conversations/${conversationId}/messages`, {
        method: "POST",
        body: JSON.stringify({ content }),
      }),
  },
};
