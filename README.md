# CSC2007-TEAM06-2023 ![build workflow](https://github.com/ict2105-csc2007/exampleteamproject-2022/actions/workflows/main.yml/badge.svg)

<a name="readme-top"></a>

<br />
<div align="center">
    <img src="https://user-images.githubusercontent.com/16435270/228751750-bb5624a7-7d2c-4f69-963e-5fa7c6714b26.png" alt="TTSH" width="150" height="150">
  </a>
<h3 align="center">TTSH Equipment Scheduling</h3>
</div>

## About The Project

A mobile application that enables Tan Tock Seng Hospital to schedule and manage its medical equipment efficiently. The app will allow the hospital staff to book equipment as per their needs, check availability, and receive alerts on equipment maintenance.

### Features:

1. **Equipment Event Schedule**: The app provides an intuitive interface for staff to efficiently manage schedules for events such as sampling, washing, and repairs through a monthly calendar view.

2. **Availability check**: Staff can check the availability of equipment before making a booking, avoiding conflicts with other users.

3. **Equipment details**: The app will provide detailed information on each equipment, such as the type, location, availability, and maintenance schedule.

4. **Schedule alerts**: The app will notify staff via a push notification for upcoming equipment events.

5. **Search and filter**: The app will provide a search function to find specific equipment by type, availability, and other filters.

6. **Reporting**: The app has a reporting feature that enables users to generate a PDF report of equipment history and send it via email.

7. **User profiles**: Each user will have a personal profile with their name and email.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

- Android Kotlin - https://developer.android.com/kotlin/

- Firebase - https://firebase.google.com/

- Docker - https://www.docker.com/

- Node.js - https://nodejs.org/en

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Video Demonstration

Video demonstration of the core features of our Android Application.

https://youtu.be/3nbW_dogXLE

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Demo Account

| E-mail               | Password    |
|------------------    |-------------|
| zhizhanlua@gmail.com | Asdqwe123   | 

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

### Installation

1. Android Studio - https://developer.android.com/studio

2. Node.js (LTS) - https://nodejs.org/en

3. Docker - https://docs.docker.com/get-docker/

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Usage

Clone the repo.

```bash
https://github.com/ict2105-csc2007/csc2007-team06-2023
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Android Application

1. Open our repo in Android Studio.

2. Run and install our application via Android Studio.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


### Push Notification Script

Refer to [README file](./push-notification-script/README.md) in `./push-notification-script` folder to find out how to setup and run the script.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Automated Test

Unit and Instrumented Tests are written for the Android application. They can be run on Android Studio or the terminal.


Running unit test in terminal in the root folder

```bash

# https://developer.android.com/studio/test/command-line
./gradlew test
```

Running instrumented test in terminal in the root folder


```bash

# Start android emulator (optional, can use actual device as well)
# https://developer.android.com/studio/run/emulator-commandline
emulator -avd avd_name

# https://developer.android.com/studio/test/command-line
./gradlew connectedAndroidTest
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>
