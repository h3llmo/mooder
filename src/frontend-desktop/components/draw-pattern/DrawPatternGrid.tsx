"use client";

/**
 * DrawPatternGrid — 4×4 dot-connect pattern input (ADR-020 / US-044).
 *
 * Design constraints:
 *  - Identical interaction on iOS (touch) and desktop (mouse).
 *  - Same component is used for master pattern setup and safe pattern entry.
 *  - No label indicates what the pattern "does" — context is set by the parent.
 *  - A wrong pattern attempt must be silent (no error state exposed here).
 *
 * Usage:
 *   <DrawPatternGrid onComplete={(hash) => sendToBackend(hash)} />
 *
 * The component emits a SHA-256 hex string of the dot-index sequence.
 * The server re-hashes with Argon2id before storage (US-049).
 */

import { useCallback, useRef, useState } from "react";

const GRID_SIZE = 4;
const DOT_COUNT = GRID_SIZE * GRID_SIZE; // 16 dots

interface Props {
  /** Called when the user lifts their finger/mouse after drawing a path of ≥4 dots. */
  onComplete: (patternHash: string) => void;
  /** Minimum number of dots required before onComplete fires. Default: 4. */
  minDots?: number;
}

type DotIndex = number; // 0–15, row-major

export default function DrawPatternGrid({
  onComplete,
  minDots = 4,
}: Props) {
  const [activeDots, setActiveDots] = useState<DotIndex[]>([]);
  const [drawing, setDrawing] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  const dotPosition = (index: DotIndex) => ({
    row: Math.floor(index / GRID_SIZE),
    col: index % GRID_SIZE,
  });

  /** Find which dot (if any) is under the pointer coordinates. */
  const hitTest = useCallback(
    (clientX: number, clientY: number): DotIndex | null => {
      if (!containerRef.current) return null;
      const dots = containerRef.current.querySelectorAll<HTMLElement>("[data-dot]");
      for (const el of dots) {
        const rect = el.getBoundingClientRect();
        if (
          clientX >= rect.left &&
          clientX <= rect.right &&
          clientY >= rect.top &&
          clientY <= rect.bottom
        ) {
          return Number(el.dataset.dot);
        }
      }
      return null;
    },
    []
  );

  const startDrawing = useCallback((clientX: number, clientY: number) => {
    setDrawing(true);
    const hit = hitTest(clientX, clientY);
    setActiveDots(hit !== null ? [hit] : []);
  }, [hitTest]);

  const continueDrawing = useCallback((clientX: number, clientY: number) => {
    if (!drawing) return;
    const hit = hitTest(clientX, clientY);
    if (hit !== null) {
      setActiveDots((prev) =>
        prev.includes(hit) ? prev : [...prev, hit]
      );
    }
  }, [drawing, hitTest]);

  const endDrawing = useCallback(async () => {
    setDrawing(false);
    if (activeDots.length >= minDots) {
      const sequence = activeDots.join(",");
      const encoded = new TextEncoder().encode(sequence);
      const hashBuffer = await crypto.subtle.digest("SHA-256", encoded);
      const hashHex = Array.from(new Uint8Array(hashBuffer))
        .map((b) => b.toString(16).padStart(2, "0"))
        .join("");
      onComplete(hashHex);
    }
    setActiveDots([]);
  }, [activeDots, minDots, onComplete]);

  // ── Mouse events ──────────────────────────────────────────────────────────
  const onMouseDown = (e: React.MouseEvent) => startDrawing(e.clientX, e.clientY);
  const onMouseMove = (e: React.MouseEvent) => continueDrawing(e.clientX, e.clientY);
  const onMouseUp   = () => endDrawing();

  // ── Touch events (portability: identical behaviour on iOS) ────────────────
  const onTouchStart = (e: React.TouchEvent) => {
    e.preventDefault();
    const t = e.touches[0];
    startDrawing(t.clientX, t.clientY);
  };
  const onTouchMove = (e: React.TouchEvent) => {
    e.preventDefault();
    const t = e.touches[0];
    continueDrawing(t.clientX, t.clientY);
  };
  const onTouchEnd = () => endDrawing();

  return (
    <div
      ref={containerRef}
      onMouseDown={onMouseDown}
      onMouseMove={onMouseMove}
      onMouseUp={onMouseUp}
      onTouchStart={onTouchStart}
      onTouchMove={onTouchMove}
      onTouchEnd={onTouchEnd}
      style={{
        display: "grid",
        gridTemplateColumns: `repeat(${GRID_SIZE}, 1fr)`,
        gap: "1.5rem",
        padding: "1.5rem",
        userSelect: "none",
        touchAction: "none",
        width: "fit-content",
        cursor: drawing ? "crosshair" : "default",
      }}
    >
      {Array.from({ length: DOT_COUNT }, (_, i) => {
        const active = activeDots.includes(i);
        const order  = activeDots.indexOf(i);
        return (
          <div
            key={i}
            data-dot={i}
            title={`dot-${dotPosition(i).row}-${dotPosition(i).col}`}
            style={{
              width: 20,
              height: 20,
              borderRadius: "50%",
              background: active ? "#6366f1" : "#d1d5db",
              transition: "background 0.1s",
              position: "relative",
            }}
          >
            {active && order >= 0 && (
              <span
                style={{
                  position: "absolute",
                  top: "50%",
                  left: "50%",
                  transform: "translate(-50%, -50%)",
                  fontSize: 9,
                  color: "#fff",
                  pointerEvents: "none",
                }}
              >
                {order + 1}
              </span>
            )}
          </div>
        );
      })}
    </div>
  );
}
