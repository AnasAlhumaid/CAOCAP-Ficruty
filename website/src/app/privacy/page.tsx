import { Shield } from "lucide-react";
import { SiteNav } from "../components/SiteNav";

export default function PrivacyPage() {
  return (
    <main className="legal-page">
      <SiteNav showContribute={false} />

      <div className="legal-container">
        <div className="legal-header">
          <div className="legal-icon">
            <Shield size={32} />
          </div>
          <h1>Privacy Policy</h1>
          <p>Effective Date: April 25, 2026</p>
        </div>

        <section className="legal-content">
          <h2>1. Overview</h2>
          <p>
            At CAOCAP, we prioritize your privacy. As a spatial IDE built for local-first thinking, we minimize data collection and ensure you maintain control over your code and creative assets.
          </p>

          <h2>2. Data Collection</h2>
          <p>
            <strong>Authentication:</strong> We use Firebase Authentication to secure your account. This may collect your email address or unique identifiers provided by Apple, Google, or GitHub.
          </p>
          <p>
            <strong>Usage Data:</strong> We collect anonymous crash reports and performance telemetry via Firebase Crashlytics to improve the stability of the app.
          </p>
          <p>
            <strong>Project Data:</strong> Your project files, nodes, and code are stored locally on your device and synced via Firebase only if you are signed in. We do not sell your project data to third parties.
          </p>

          <h2>3. AI Processing (CoCaptain)</h2>
          <p>
            When you use the CoCaptain AI features, relevant snippets of your project context (SRS, HTML, CSS, JS) are sent to Google Gemini via Firebase AI Logic to generate suggestions. This data is processed transiently and is subject to Google&apos;s Enterprise Privacy standards.
          </p>

          <h2>4. Your Rights</h2>
          <p>
            You have the right to access, export, or delete your account at any time directly from the &quot;Profile&quot; section of the app. Deleting your account permanently removes all project data from our servers.
          </p>

          <h2>5. Contact</h2>
          <p>
            If you have questions about this policy, contact us at privacy@caocap.app.
          </p>
        </section>
      </div>
    </main>
  );
}
