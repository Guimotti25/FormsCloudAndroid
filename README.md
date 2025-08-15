FormsCloud for Android

Overview
FormsCloud for Android is a native and robust application that dynamically renders complex forms from a JSON definition.
This project demonstrates how to build rich, data-driven user interfaces using modern Android development technologies and practices, such as Kotlin, Architecture Components, and Material Design, following the MVVM architecture.
Key Features
📱 Native Interface with Material Design
The entire interface is built with XML layouts and Material Design components, ensuring a familiar, responsive, and modern user experience that adheres to Google’s design guidelines.
📄 Dynamic JSON Rendering
The app reads a JSON structure to build forms at runtime, allowing the form layout to be updated without requiring an app release.

🎨 Support for Multiple Field Types
Renders a wide range of native UI components, including:

Text fields (TextInputEditText), email, and password fields (with icon to toggle visibility)
Calendar date pickers (MaterialDatePicker)
Radio buttons (RadioGroup) for single selection
Checkboxes (CheckBox) for multiple selection
Dropdown menus (AutoCompleteTextView with exposed menu style)
Multi-line text areas (TextInputEditText)
🗂️ Sections with Rich HTML Content
Groups fields into sections with titles that support HTML content, including rendering images from the internet using Jsoup for HTML parsing and Glide for image loading.
💾 Local Persistence with Room
Form responses are securely stored on the device using Google’s Room persistence library, providing a robust and efficient local SQL-based database.

📂 Attachment Viewing
The details screen can display files attached by the user, such as images, loaded asynchronously using Glide.

✅ Real-Time Validation
The save button is dynamically enabled or disabled based on whether all required fields (required: true) have been filled in.

🏛️ MVVM + Repository Architecture
The code is structured following the Model-View-ViewModel (MVVM) pattern, complemented by a Repository layer. This clean separation between UI (Activity/XML), state logic (ViewModel), and data access (Repository) results in cleaner, decoupled, and easier-to-test code.

🗑️ Data Management
Users can list all submissions for a form, view details for each one, and delete them via a long-press gesture with a confirmation MaterialAlertDialog.

Tech Stack
Language: Kotlin
UI Framework: Android XML Layouts & Material Design Components
Database: Room Persistence Library
Architecture: Android Architecture Components (ViewModel, LiveData) + Repository
HTML Parsing: Jsoup
Image Loading: Glide
File Selection: ActivityResultLauncher with ACTION_OPEN_DOCUMENT
Asynchronous Operations: Kotlin Coroutines



Running project in Android Studio - Android Studio Narwhal | 2025.1.1 Patch 1
Build #AI-251.25410.109.2511.13752376, built on July 8, 2025
Runtime version: 21.0.6+-13391695-b895.109 aarch64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.
Toolkit: sun.lwawt.macosx.LWCToolkit
macOS 15.5
GC: G1 Young Generation, G1 Concurrent GC, G1 Old Generation
Memory: 3072M
Cores: 10
Metal Rendering is ON
Registry:
  debugger.new.tool.window.layout=true
  ide.experimental.ui=true
  com.android.studio.ml.activeModel=com.android.studio.ml.AidaModel
