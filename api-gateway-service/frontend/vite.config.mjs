import { defineConfig } from "vite";
import { resolve } from "node:path";

export default defineConfig({
  define: {
    "process.env.NODE_ENV": JSON.stringify("production")
  },
  build: {
    lib: {
      entry: resolve(import.meta.dirname, "src/main.jsx"),
      name: "UzhavamPlantHealth",
      formats: ["iife"],
      fileName: () => "plant-health.js"
    },
    outDir: "../src/main/resources/static/assets",
    emptyOutDir: false,
    minify: true
  }
});
