# TCP Training App

This project is an Android application designed to communicate with a lower-level device using TCP. The app's primary features include login, training functionality, real-time graph display, settings, and historical training record viewing.

## Main Features

- **Login and Training Page**: The training page contains a line chart and settings functionality. It features start and stop buttons to manage training sessions.
  - Set up the desired parameters first, save them, then click **Start** to initiate communication with the lower-level device over TCP.
  - The line chart will start moving dynamically as data flows in real-time.
  - Click **Stop** when the training session is over to save the data.
- **Historical Records**: View previous training records and analyze past sessions.

## Technologies Used

- **Android Compile SDK Version**: 34
- **Java Version**: VERSION_17
- **Dependency Management**: Kotlin DSL
- **UI Development**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Architecture**: ViewModel pattern to separate UI from data logic
- **HTTP Connections**: Retrofit2
- **TCP/UDP Communication**: Ktor
- **Chart Library**: Vico

### Jetpack Compose Details

- **MainActivity**: Entry point for the app.
- **EntryScreen**: Manages login state, determining if a user is logged in.
- **Navigation**: Uses `NavHost` to manage navigation between screens.
- **Adaptive UI**: The app adjusts the UI based on the screen orientation.
  - In **landscape mode**, the menu is displayed on the left side.
  - In **portrait mode** (typically on phones), the menu is shown at the bottom.
- **Screens**:
  - **LoginScreen**: The login page for the app.
  - **MainScreen**: Determines adaptive behavior based on screen size and orientation.
  - **HomeScreen**: Contains the main content, including:
    - **ComposeChart**: Displays the training line chart.
    - **SettingScreen**: Allows configuration of training parameters.

## Installation

To install and run the app:

1. Clone the repository:
   ```bash
   git clone <repository_url>
   ```
2. Open the project in **Android Studio**.
3. Install dependencies using Gradle.
4. Run the app on an emulator or physical device.
5. Perform a mock login, then click **Start** to see the line chart in action. Note that the data is currently mocked for testing purposes.

## Getting Started

This project aims to facilitate communication between an Android app and a lower-level device using TCP, providing real-time visual feedback through charts. It serves as a helpful tool for training and monitoring data.

Feel free to explore, contribute, or use this project as a foundation for further development.