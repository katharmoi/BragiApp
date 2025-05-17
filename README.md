## Setup and Build Instructions

### Prerequisites

*   Android Studio
*   JDK 17 or higher

### API Key

1.  Open `app/build.gradle.kts` file.
2.  Find  `defaultConfig` block and add your api key:
    ```kotlin
    buildConfigField("String", "TMDB_API_KEY", "\"\"")
    ```

### Building the Project

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    ```
2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select "Open an Existing Project".
    *   Navigate to the cloned project directory and open it.
3.  **Build the App:**
    *   Select `Build` > `Make Project`.
4.  **Run the App:**
    *   Click the "Run" button (green play icon) in Android Studio.
       
    **Note:** For brevity API key placed in `build.gradle.kts`, in a real life scenario it is safer
    to add to `local.properties` and not commit to version control. 

## Architecture

App follows the **Clean Architecture** pattern. It's structured into three main layers:

*   **Domain Layer:** Contains business logic & use cases.
    *   Defines domain models (e.g., `Movie`, `Genre`).
    *   Includes Use Cases (e.g., `GetMoviesUseCase`, `GetGenresUseCase`).
    *   Independent of Android framework.
*   **Data Layer:** Provides data to the domain layer.
    *   Implements repository interfaces defined in the domain layer.
    *   Handles data fetching from the TMDB API using **Retrofit** and **OkHttp**.
    *   Includes Data Transfer Objects (DTOs) for API responses and mappers to convert DTOs to domain models.
*   **Presentation Layer:** Handles UI and user interactions.
    *   Built with **Jetpack Compose**.
    *   Uses **MVVM (Model-View-ViewModel)** with AndroidX ViewModels.
    *   **Navigation Compose** for screen transitions.
    *   Observes data using Kotlin Flows and StateFlows.

## Tech Stack & Libraries

*   **Jetpack Compose:** For UI
*   **Kotlin Coroutines & Flows:** For asynchronous programming and reactive data streams.
*   **Koin:** For Dependency Injection.
*   **Retrofit:** For networking.
*   **OkHttp:** For Networking.
*   **Gson:** For parsing JSON responses from the API.
*   **Coil:** For Image loading.
*   **Navigation Compose:** For navigating between composable screens.
*   **AndroidX ViewModel:** For UI-related data lifecycle management.
*   **Timber:** For logging.
*   **Material 3:** Design components for UI.