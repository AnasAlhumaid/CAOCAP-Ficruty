"use client";

import { Monitor, Moon, Sun } from "lucide-react";
import { useEffect, useState } from "react";

type ThemePreference = "system" | "light" | "dark";

const storageKey = "caocap-theme";

const themeOptions: {
  value: ThemePreference;
  label: string;
  icon: typeof Monitor;
}[] = [
  { value: "system", label: "Use system theme", icon: Monitor },
  { value: "light", label: "Use light theme", icon: Sun },
  { value: "dark", label: "Use dark theme", icon: Moon }
];

function isThemePreference(value: string | null): value is ThemePreference {
  return value === "system" || value === "light" || value === "dark";
}

function getSystemTheme() {
  return window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
}

function applyTheme(preference: ThemePreference) {
  const resolvedTheme = preference === "system" ? getSystemTheme() : preference;
  const root = document.documentElement;

  root.dataset.theme = resolvedTheme;
  root.dataset.themePreference = preference;
  root.style.colorScheme = resolvedTheme;
}

export function ThemeToggle() {
  const [preference, setPreference] = useState<ThemePreference>("system");

  useEffect(() => {
    const storedPreference = window.localStorage.getItem(storageKey);
    const initialPreference = isThemePreference(storedPreference)
      ? storedPreference
      : "system";

    setPreference(initialPreference);
    applyTheme(initialPreference);
  }, []);

  useEffect(() => {
    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
    const handleSystemThemeChange = () => {
      if (preference === "system") {
        applyTheme("system");
      }
    };

    mediaQuery.addEventListener("change", handleSystemThemeChange);
    return () => mediaQuery.removeEventListener("change", handleSystemThemeChange);
  }, [preference]);

  function handleThemeChange(nextPreference: ThemePreference) {
    setPreference(nextPreference);
    window.localStorage.setItem(storageKey, nextPreference);
    applyTheme(nextPreference);
  }

  return (
    <div className="theme-toggle" role="group" aria-label="Color theme">
      {themeOptions.map((option) => {
        const Icon = option.icon;
        const isSelected = preference === option.value;

        return (
          <button
            aria-label={option.label}
            aria-pressed={isSelected}
            className="theme-toggle-button"
            key={option.value}
            onClick={() => handleThemeChange(option.value)}
            title={option.label}
            type="button"
          >
            <Icon aria-hidden="true" size={16} strokeWidth={2.2} />
          </button>
        );
      })}
    </div>
  );
}
