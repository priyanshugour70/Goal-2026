import SwiftUI
#if canImport(composeApp)
import composeApp
#endif

#if canImport(composeApp)
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
#else
struct ComposeView: View {
    var body: some View {
        VStack(spacing: 12) {
            Text("composeApp module not found")
                .font(.headline)
            Text("This is a placeholder SwiftUI view. Add the generated composeApp.xcframework or package to enable the Compose-based UI.")
                .font(.subheadline)
                .multilineTextAlignment(.center)
                .padding(.horizontal)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color(.systemBackground))
    }
}
#endif

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all, edges: .bottom)
    }
}
