# EldoHub Training Manuals App

A mobile application developed for EldoHub to manage and view training manuals efficiently. This app is designed with inclusivity and performance in mind, ensuring trainers can access materials even in low-connectivity or high-contrast environments.

## Features

- **Offline Access**: Local caching of training manuals via Room database.
- **Remote Synchronization**: Pull-to-refresh and manual sync triggers to fetch the latest manuals.
- **Inclusive UI**:
    - **High Contrast Mode**: A dedicated theme for users with visual impairments, togglable from the top bar.
    - **Adaptive Layouts**: Responsive design that switches between list and grid views based on screen size (e.g., tablets).
    - **Text-to-Speech (TTS)**: Integrated "Read Aloud" feature for training manuals to assist users with reading difficulties.
    - **Accessibility Semantics**: Optimized for screen readers with proper content descriptions and click labels.
    - **Localization Ready**: All UI strings are externalized for easy translation.
- **Optimized Imaging**: Uses Coil for efficient image downsampling, preventing memory issues with large assets.
- **PDF Viewer**: Seamlessly render training materials within the app.

## Tech Stack

- **UI Framework**: Jetpack Compose
- **Language**: Kotlin
- **Asynchronous Programming**: Kotlin Coroutines & Flow
- **Dependency Injection**: Hilt (Dagger)
- **Local Storage**: Room Persistence Library
- **Image Loading**: Coil
- **Architecture**: MVVM (Model-View-ViewModel) with a Repository pattern.

## Project Structure

- `ke.eldohub.training.ui`: Contains the UI layer (Screens, ViewModels, Theme).
    - `list`: Manual list screen and logic.
    - `viewer`: PDF rendering components.
    - `components`: Reusable UI elements (e.g., `ManualCard`).
- `ke.eldohub.training.data`: Data layer handling local and remote sources.
    - `repo`: Repository implementation and domain models.
    - `local`: Room Database and DAOs.
    - `remote`: API interfaces for fetching content.
- `ke.eldohub.training.sync`: Background synchronization logic using WorkManager.
- `ke.eldohub.training.di`: Dependency injection modules.

## How to use

1. **Synchronize**: On the main screen, pull down or tap the refresh icon to fetch available manuals.
2. **Toggle Contrast**: Use the contrast icon in the top bar to switch between the standard and high-contrast themes.
3. **Open Manual**: Tap on any available manual to open the PDF viewer.
