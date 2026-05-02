# Subscription Feature

The Subscription feature presents CAOCAP Pro plans, loads StoreKit products, handles purchases/restores, and tracks active entitlements.

## Ownership

- `SubscriptionManager` owns StoreKit products, active entitlement IDs, purchase flow, restore flow, and transaction updates.
- `PurchaseView` renders the paywall, plan selection, purchase button, legal disclosure, and success/error states.
- `PurchaseComponents` contains reusable paywall rows, cards, and visual building blocks.
- `Subscriptions.storekit` in `App/` provides local StoreKit configuration for development.

The feature's core contract is simple: grant Pro only from verified StoreKit transactions.

## Purchase Flow

1. `PurchaseView` appears and calls `SubscriptionManager.fetchProducts()`.
2. The user selects a monthly or yearly product.
3. `purchaseAction()` either opens Apple subscription management for subscribed users or starts a StoreKit purchase.
4. `SubscriptionManager.purchase(_:)` verifies the transaction.
5. Verified transactions refresh entitlements, finish the transaction, and return success to the paywall.
6. Cancelled or pending purchases return `nil` and should not be shown as failures.
7. The paywall shows success only after a verified transaction.

## Entitlement Flow

`SubscriptionManager` keeps `purchasedProductIDs` current from two StoreKit sources:

- `Transaction.currentEntitlements` during explicit refresh/restore;
- `Transaction.updates` for renewals, refunds, revocations, and purchases completed outside the current view.

Revoked transactions must remove entitlement access. Unverified transactions must not grant Pro.

## Restore And Management

- Restore calls `AppStore.sync()`, then refreshes current entitlements.
- Already-subscribed users are routed to Apple's subscription management page.
- The paywall should keep legal links and auto-renewal disclosure visible and accurate.

## Compliance Notes

- Keep subscription copy aligned with App Store requirements.
- Keep Terms of Use and Privacy Policy links reachable from the paywall.
- Do not hard-code final pricing as truth. Use StoreKit `displayPrice` when available.
- Fallback prices are only launch-copy placeholders while StoreKit products load or fail.

## Editing Guidance

- Keep purchase and entitlement state in `SubscriptionManager`.
- Keep presentation, animations, selected product state, and alerts in `PurchaseView`.
- Do not grant Pro from an unverified transaction or UI-only state.
- Treat `.pending` as non-error; family approval or payment review may complete later through transaction updates.
- Use `Logger` instead of new `print(...)` diagnostics when touching production paths.
- If product IDs change, update `SubscriptionManager`, StoreKit config, App Store Connect, and paywall defaults together.

## Verification Checklist

- Products load from StoreKit configuration in development.
- Monthly and yearly plan selection updates the selected product.
- Purchase success shows the success state and sets `isSubscribed`.
- User cancellation does not show a failure alert.
- Pending purchase does not grant Pro immediately.
- Restore refreshes entitlements.
- Revoked transactions remove entitlement access.
- Subscribed users can open Apple's subscription management page.
- Terms, EULA, and Privacy links open correctly.

## Test Targets

Useful test coverage for this feature:

- `isSubscribed` reflects active purchased product IDs.
- verified purchase updates entitlements and finishes the transaction.
- unverified transactions throw `StoreError.failedVerification`.
- revoked current entitlements are ignored.
- restore calls entitlement refresh after App Store sync.
- paywall handles missing products with user-facing fallback/error behavior.
