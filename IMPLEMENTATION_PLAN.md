# EldoHub Training App: UI Revamp & Performance Optimization Plan

## Executive Summary
This document outlines the strategic roadmap and technical implementation plan to modernize the EldoHub Training Manuals Android application. The initiative focuses on two core pillars:
1. **Modern UI/UX Revamp**: Transition to an expressive Material 3 aesthetic, clean squircle typography/shapes, interactive widgets, shimmering skeleton states, and refined accessibility.
2. **Performance Optimization**: Reduce memory pressure by ~50% on low-end/budget tablets, eliminate unnecessary database CursorWindow bloat, eliminate startup latency, and ensure smooth 60fps Compose scrolling.

---

## 1. Technical Architecture & Performance Optimizations

### 1.1 Memory & PDF Rendering Optimization
* **Bitmap Configuration (`RGB_565` vs `ARGB_8888`)**:
  * Switch to `Bitmap.Config.RGB_565` (2 bytes/px). Since PDF pages are rendered on opaque white backgrounds, alpha channel is unnecessary. This saves 50% memory immediately.
* **Smart Dynamic Cache Sizing**:
  * Automatically tune `LruCache` based on device RAM class (8 pages on normal devices, 4 pages on low-RAM devices).
* **Safe Bitmap Recycling**:
  * Ensure evicted bitmaps are properly recycled and references cleared to prevent memory leaks during rapid page navigation.

### 1.2 Room Database & Data Pipeline
* **Split Entity Projection (CursorWindow Optimization)**:
  * Introduce a lightweight `ManualSummary` projection (`id`, `title`, `version`, `assetPath`, `filePath`, `lastViewedAt`) for all list and dashboard queries. Only fetch `textContent` on-demand when opening the PDF Viewer or triggering Text-to-Speech.
* **Database Indexing**:
  * Add `@Index` annotations on `lastViewedAt` and `title` in `manuals`, and `createdAt` in `reports`.

### 1.3 Startup Time & Perceived Speed
* **Remove Artificial Splash Latency**:
  * Remove artificial delay in `SplashScreen.kt`; wire up instant transition to dashboard once local Room data is initialized.

### 1.4 Jetpack Compose Recomposition & Stability
* **Stable UI States**:
  * Ensure UI state classes are annotated with `@Immutable` / `@Stable` so Compose can skip recompositions when lists haven't changed.
* **Stable Lambda Handlers & Keys**:
  * Use proper `key` and `contentType` in `LazyColumn` and `LazyVerticalGrid` to optimize view recycling.

---

## 2. Modern UI & Visual Revamp

### 2.1 Unified Design System & Theming
* **Material 3 Dynamic Palette**:
  * **Brand Primary**: EldoHub Forest Emerald (`#00695C` / `#2B7C6B`) with matching tonal containers.
  * **Brand Secondary**: Mastercard Sunset Orange (`#FF5F00` / `#FF7A29`).
  * **Brand Tertiary**: Mastercard Golden Amber (`#F79E1B` / `#FFA726`).
  * **Neutral Surface System**: Modern soft off-white (`#F8FAFC`), cool slate container tones, and deep slate dark-theme backgrounds (`#0F172A`).
  * **High-Contrast Theme**: Retained full WCAG AAA yellow-on-black mode.
* **Modern Typography & Shapes**:
  * Squircle corner radiuses (12dp, 16dp, 24dp) and pill buttons.
  * High-clarity typography scale with clear hierarchy.

### 2.2 Screen-by-Screen Modernization
* **Dashboard**: Warm personalized greeting, dynamic sync status chip, modern stat cards, "Continue Training" hero with page progress.
* **Manuals Explorer**: Filter chips (`All`, `Offline Ready`, `Finance`, `Digital`), rich manual cards with badges, shimmer loading skeleton.
* **PDF Reader & Audio**: Auto-hiding floating HUD, bottom page scrubber pill (`Page X of Y`), eye comfort modes (Sepia / Night), integrated bottom TTS mini-player.
* **Reports**: Status badges (`Synced` vs `Pending Sync`), attachment preview card, modern card-grouped form.
* **Accessibility Hub**: Clean, non-intrusive placement in top app bar / header without blocking screen contents.
