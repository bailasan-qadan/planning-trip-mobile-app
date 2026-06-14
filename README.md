# Planning Trip Mobile App

## Overview

Planning Trip Mobile App is a native Android application that provides users with a complete trip planning experience — from discovering destinations to creating personalized itineraries. The application enables users to explore curated city guides, search for destinations, plan and save custom trips with events and date ranges, and manage their travel details through a clean and intuitive interface.

The project was developed as an individual project using Android Studio and Java, with an emphasis on practical mobile development concepts, screen navigation, local data persistence, and user-centered UI design.

---

## Features

### User Onboarding and Authentication
* Welcome and onboarding screens for new users
* User registration and login interfaces
* Session management using SharedPreferences

### Destination Discovery
* Browse a curated list of global trip destinations
* View featured destinations on the home screen
* See All Trips screen for the full destination catalog
* Each destination includes restaurants, hotels, famous landmarks, and safety notes

### Destination Guide Search
* Search for cities in real time using a live-filter search bar
* Grid-based destination browsing layout
* Tap any destination to view its full travel guide

### Trip Planning
* Create custom trips with destination, start date, and end date
* Interactive date picker for selecting travel dates
* Add multiple events and activities to a trip itinerary
* Save planned trips locally to internal storage

### User Trip Management
* View all personally created trips on the home screen
* Edit existing trips and their details
* Navigate to detailed views of saved user trips

---

## Technologies Used

### Mobile Development
* Java
* Android SDK
* XML (UI Layout Design)

### Libraries and Tools
* Gson — JSON serialization and deserialization
* RecyclerView — Efficient list and grid rendering
* SharedPreferences — Lightweight session and data storage
* DatePickerDialog — Native date selection

### Development Environment
* Android Studio
* Android Emulator / Physical Device Testing
* Gradle (Kotlin DSL)

---

## Installation

1. Clone the repository:
```bash
git clone https://github.com/bailasan-qadan/planning-trip-mobile-app.git
```

2. Navigate to the project directory:
```bash
cd planning-trip-mobile-app
```

3. Open the project in **Android Studio** and let Gradle sync and resolve dependencies automatically.

4. Run the application on an emulator or connected physical device:
   * Minimum SDK: API 24 (Android 7.0)
   * Target SDK: API 35

---

## Live Demo

Demo Video (Google Drive):
[Watch the demo](https://drive.google.com/file/d/1YODxeYUkBht5cJgdwdng5iB1jqy8g2zM/view?usp=sharing)

---

## Note

This project focuses on the client-side mobile application interface and user experience. Backend services and live data integration are not included in the current implementation. Destination data is loaded from a locally bundled JSON file, and user trips are persisted to the device's internal storage.
