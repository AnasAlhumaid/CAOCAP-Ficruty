import SwiftUI

/// Full-screen editor for the Firebase Web config node.
struct FirebaseConfigNodeEditorView: View {
    let node: SpatialNode
    let store: ProjectStore
    @Environment(\.dismiss) private var dismiss
    @State private var jsonText: String
    @State private var collectionPath: String

    init(node: SpatialNode, store: ProjectStore) {
        self.node = node
        self.store = store
        _jsonText = State(initialValue: node.textContent ?? "")
        _collectionPath = State(initialValue: node.firebaseFirestorePath ?? "")
    }

    private var currentNode: SpatialNode {
        store.nodes.first(where: { $0.id == node.id }) ?? node
    }

    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    Text("Firebase (Web)")
                        .font(.title2.bold())

                    Text(
                        "In Firebase Console: Project settings → Your apps → Web app → copy the `firebaseConfig` object. Paste it below. Live Preview loads the compat SDK and sets `window.__caocapFirestore` and optional `window.__caocapFirestoreDefaultPath`."
                    )
                    .font(.subheadline)
                    .foregroundColor(.secondary)

                    Text("Web app config (JSON)")
                        .font(.headline)

                    TextEditor(text: $jsonText)
                        .font(.system(.body, design: .monospaced))
                        .frame(minHeight: 260)
                        .padding(8)
                        .background(Color(uiColor: .secondarySystemBackground))
                        .clipShape(RoundedRectangle(cornerRadius: 12))

                    Text("Optional Firestore path")
                        .font(.headline)

                    Text("Example: `products` or `users/alice/orders`. Exposed to JS as `window.__caocapFirestoreDefaultPath`.")
                        .font(.caption)
                        .foregroundColor(.secondary)

                    TextField("Collection or document path", text: $collectionPath)
                        .textFieldStyle(.roundedBorder)
                        .autocapitalization(.none)
                        .autocorrectionDisabled()

                    Text("JavaScript example")
                        .font(.headline)
                        .padding(.top, 8)

                    Text(
                        """
                        const db = window.__caocapFirestore;
                        const base = window.__caocapFirestoreDefaultPath || '';
                        if (db && base) {
                          db.collection(base).limit(10).get().then(snap => {
                            console.log(snap.size, 'docs');
                          });
                        }
                        """
                    )
                    .font(.system(.caption, design: .monospaced))
                    .padding(12)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color(uiColor: .tertiarySystemBackground))
                    .clipShape(RoundedRectangle(cornerRadius: 10))
                }
                .padding()
            }
            .navigationTitle(currentNode.displayTitle)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        let trimmedPath = collectionPath.trimmingCharacters(in: .whitespacesAndNewlines)
                        store.updateNodeTextContent(id: node.id, text: jsonText, persist: true)
                        store.updateNodeFirebaseFirestorePath(id: node.id, path: trimmedPath.isEmpty ? nil : trimmedPath)
                        dismiss()
                    }
                    .fontWeight(.semibold)
                }
            }
        }
    }
}
