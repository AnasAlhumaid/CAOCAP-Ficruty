import Link from "next/link";
import { ThemeToggle } from "./ThemeToggle";

const githubUrl = "https://github.com/Azzam-Alrashed/CAOCAP-Ficruty";

type SiteNavProps = {
  homeHref?: string;
  showContribute?: boolean;
};

export function SiteNav({ homeHref = "/", showContribute = true }: SiteNavProps) {
  return (
    <nav className="site-nav" aria-label="Primary navigation">
      <Link className="brand-mark" href={homeHref} aria-label="CAOCAP home">
        CAOCAP
      </Link>
      <div className="nav-actions">
        {showContribute ? (
          <a className="nav-link" href={githubUrl} target="_blank" rel="noreferrer">
            Contribute
          </a>
        ) : null}
        <ThemeToggle />
      </div>
    </nav>
  );
}
