import { FileText } from "lucide-react";
import { SiteNav } from "../components/SiteNav";

export default function TermsPage() {
  return (
    <main className="legal-page">
      <SiteNav showContribute={false} />

      <div className="legal-container">
        <div className="legal-header">
          <div className="legal-icon">
            <FileText size={32} />
          </div>
          <h1>Terms of Service</h1>
          <p>Effective Date: April 25, 2026</p>
        </div>

        <section className="legal-content">
          <h2>1. Acceptance of Terms</h2>
          <p>
            By downloading or using CAOCAP, you agree to these Terms of Service and our Privacy Policy. If you do not agree, do not use the application.
          </p>

          <h2>2. License</h2>
          <p>
            We grant you a personal, non-transferable license to use CAOCAP on supported iOS and iPadOS devices. The core application logic and spatial engine are proprietary, while specific exported components may be subject to open-source licenses as noted in our repository.
          </p>

          <h2>3. Pro Subscriptions</h2>
          <p>
            CAOCAP Pro is a subscription service. Payments are handled via Apple&apos;s StoreKit 2. Subscriptions automatically renew unless cancelled at least 24 hours before the end of the current period. You can manage your subscription in your App Store Account Settings.
          </p>

          <h2>4. User Content</h2>
          <p>
            You retain full ownership of all code, designs, and requirements created within CAOCAP. You are solely responsible for ensuring your content does not violate any laws or third-party rights.
          </p>

          <h2>5. AI CoCaptain</h2>
          <p>
            AI-generated code and suggestions are provided &quot;as is.&quot; While CoCaptain is designed to be helpful, we do not guarantee the accuracy, security, or functionality of AI-proposed changes. Always review AI suggestions before applying them to your production projects.
          </p>

          <h2>6. Limitation of Liability</h2>
          <p>
            CAOCAP is provided &quot;as is&quot; without warranties of any kind. We are not liable for any loss of data, profits, or damages resulting from the use or inability to use the software.
          </p>

          <h2>7. Changes to Terms</h2>
          <p>
            We may update these terms from time to time. Continued use of the app after updates constitutes acceptance of the new terms.
          </p>
        </section>
      </div>
    </main>
  );
}
