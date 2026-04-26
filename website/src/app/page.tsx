import {
  AppWindow,
  ArrowUpRight,
  Bot,
  Boxes,
  Code2,
  Github,
  Sparkles,
  TestTube2
} from "lucide-react";
import Link from "next/link";
import { SiteNav } from "./components/SiteNav";

const appStoreUrl = "https://apps.apple.com/us/app/caocap/id1447742145";
const testFlightUrl = "https://testflight.apple.com/join/aS7Jwlof";
const githubUrl = "https://github.com/Azzam-Alrashed/CAOCAP-Ficruty";

const ctas = [
  {
    label: "App Store",
    href: appStoreUrl,
    icon: AppWindow,
    ariaLabel: "Download CAOCAP on the App Store"
  },
  {
    label: "TestFlight",
    href: testFlightUrl,
    icon: TestTube2,
    ariaLabel: "Join the CAOCAP TestFlight beta"
  },
  {
    label: "GitHub",
    href: githubUrl,
    icon: Github,
    ariaLabel: "Star CAOCAP on GitHub and contribute"
  }
];

const features = [
  {
    title: "Map the interface",
    body: "Create pages as connected ideas instead of buried files, with requirements, structure, style, behavior, and preview visible together."
  },
  {
    title: "Build in motion",
    body: "Shape HTML, CSS, and JavaScript on a spatial canvas that keeps the live result close enough to guide every decision."
  },
  {
    title: "Keep the context",
    body: "CAOCAP keeps the project graph readable, so the next change starts from the whole idea rather than a blank editor tab."
  }
];

const stats = [
  "Native iOS and iPadOS",
  "Live WebKit preview",
  "Open source roadmap",
  "Built for spatial thinking"
];

function CtaButtons() {
  return (
    <div className="cta-row" aria-label="Primary actions">
      {ctas.map((cta) => {
        const Icon = cta.icon;

        return (
          <a
            className="cta-button"
            href={cta.href}
            key={cta.label}
            aria-label={cta.ariaLabel}
            target="_blank"
            rel="noreferrer"
          >
            <Icon aria-hidden="true" size={20} strokeWidth={2.2} />
            <span>{cta.label}</span>
            <ArrowUpRight aria-hidden="true" size={17} strokeWidth={2.2} />
          </a>
        );
      })}
    </div>
  );
}

function CanvasMockup() {
  return (
    <div className="canvas-shell" aria-label="CAOCAP spatial canvas preview">
      <div className="canvas-toolbar">
        <div>
          <span className="toolbar-kicker">Project</span>
          <strong>Launch page</strong>
        </div>
        <span className="toolbar-pill">Live</span>
      </div>
      <div className="canvas-stage">
        <svg
          className="canvas-lines"
          viewBox="0 0 640 460"
          fill="none"
          aria-hidden="true"
        >
          <path d="M161 106 C239 112 252 184 324 188" />
          <path d="M161 106 C244 78 372 82 475 114" />
          <path d="M324 188 C399 196 431 263 501 288" />
          <path d="M304 344 C367 329 427 313 501 288" />
        </svg>

        <article className="node node-srs">
          <span>SRS</span>
          <strong>What should this become?</strong>
          <p>Capture the intent before the interface hardens.</p>
        </article>

        <article className="node node-html">
          <span>HTML</span>
          <strong>Structure</strong>
          <p>&lt;main&gt; ideas into place.</p>
        </article>

        <article className="node node-css">
          <span>CSS</span>
          <strong>Feel</strong>
          <p>Shape hierarchy, rhythm, and tone.</p>
        </article>

        <article className="node node-js">
          <span>JS</span>
          <strong>Behavior</strong>
          <p>Make the canvas respond.</p>
        </article>

        <article className="preview-node">
          <span>Live Preview</span>
          <div className="phone-frame">
            <div className="mini-page">
              <div />
              <div />
              <div />
            </div>
          </div>
        </article>
      </div>
    </div>
  );
}

export default function Home() {
  return (
    <main>
      <section className="hero-section">
        <SiteNav homeHref="#top" />

        <div className="hero-grid" id="top">
          <div className="hero-copy">
            <p className="eyebrow">Mindmap your HTML</p>
            <h1>CAOCAP</h1>
            <p className="hero-lede">
              A spatial, mindmap-driven way to build HTML, CSS, and JavaScript
              on iOS and iPadOS.
            </p>
            <CtaButtons />
          </div>
          <CanvasMockup />
        </div>
      </section>

      <section className="section-panel">
        <div className="section-heading">
          <p className="eyebrow">Spatial development</p>
          <h2>Build with nodes that keep the idea visible.</h2>
        </div>
        <div className="feature-grid">
          {features.map((feature) => (
            <article className="feature-card" key={feature.title}>
              <Boxes aria-hidden="true" size={24} />
              <h3>{feature.title}</h3>
              <p>{feature.body}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="split-section">
        <div>
          <p className="eyebrow">CoCaptain</p>
          <h2>An AI companion that sees the whole project graph.</h2>
        </div>
        <div className="assistant-panel">
          <div className="assistant-icon">
            <Bot aria-hidden="true" size={28} />
          </div>
          <p>
            CAOCAP is being shaped around grounded assistance: requirements,
            code nodes, relationships, and previews in one context window.
          </p>
          <span>Review, apply, keep building.</span>
        </div>
      </section>

      <section className="native-section">
        <div className="native-copy">
          <p className="eyebrow">Native foundation</p>
          <h2>Designed for touch, canvas thinking, and real devices.</h2>
          <p>
            CAOCAP is built natively for iPhone and iPad, using WebKit for live
            previews and an open GitHub roadmap for the work ahead.
          </p>
        </div>
        <div className="status-grid" aria-label="CAOCAP status">
          {stats.map((stat) => (
            <div className="status-item" key={stat}>
              <Sparkles aria-hidden="true" size={18} />
              <span>{stat}</span>
            </div>
          ))}
        </div>
      </section>

      <section className="final-cta">
        <Code2 aria-hidden="true" size={30} />
        <h2>Start building through the map.</h2>
        <p>
          Download CAOCAP, join the TestFlight, or help shape the future in
          public on GitHub.
        </p>
        <CtaButtons />
      </section>

      <footer className="site-footer">
        <div className="footer-content">
          <p>© 2026 Azzam Alrashed. Built for the spatial era.</p>
          <div className="footer-links">
            <Link href="/privacy">Privacy Policy</Link>
            <Link href="/terms">Terms of Service</Link>
            <a href={githubUrl} target="_blank" rel="noreferrer">GitHub</a>
          </div>
        </div>
      </footer>
    </main>
  );
}
