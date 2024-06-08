Radio Station Availability Checker

Project Description
The Radio Station Availability Checker is a simple application that displays a list of public radio stations and their current availability status (online or offline). It utilizes the free Radio Browser API (https://de1.api.radio-browser.info/) to fetch station data and check their availability.

Features
Fetches a list of radio stations based on a specified language.
Checks the current availability status (online or offline) of each station.
Displays the station name and its corresponding availability status.
MVVM architecture with clean code practices.
Unit tests following TDD principles.
Integration Tests
Certificate pinning for enhanced network security.
ProGuard rules for code obfuscation.
Caching mechanism for API calls.
Accessibility features for inclusive user experience.
Optimized code and custom components like Text.

Assumptions
The application assumes that the Radio Browser API is accessible and functioning correctly.
The application assumes that the station list and availability check endpoints are valid and return the expected data format.

Usage

When the application launches, it will display an initial list of radio stations based on the specified language. As the user scrolls down, the application will load and append additional stations to the list.
To check the availability status of a specific radio station, the user can tap or click on the desired station from the list. This action will navigate to the Station Availability screen.
On the Station Availability screen, the application will fetch and display the current online or offline status for the selected radio station.
The user can navigate back to the Radio Station List screen by tapping or clicking the back button or navigating back using the appropriate method provided by the application's user interface.

The application follows this flow:

Radio Station List Screen

Displays an initial list of radio stations based on the specified language.
Loads and appends additional stations as the user scrolls down.
Allows the user to select a specific radio station to check its availability.

Station Availability Screen

Fetches and displays the current online or offline status for the selected radio station.
Provides a back button or navigation method to return to the Radio Station List screen.

Navigation

Tapping or clicking on a radio station from the list navigates to the Station Availability screen.
Tapping or clicking the back button or using the appropriate navigation method returns to the Radio Station List screen.
This usage flow allows users to browse through the list of radio stations, check the availability status of specific stations, and seamlessly navigate between the two screens.

Known Issues

The application currently fails to combine the data from the Radio Station API and the Station Availability API to display the station list and availability status in a paired format. The codebase attempts to load the availability status for each station alongside the station details, but this implementation is incomplete.

As an alternative solution, the application separates the station list and availability status into two distinct screens:

Radio Station List Screen: Displays the list of radio stations fetched from the Radio Station API.
Station Availability Screen: Shows the online or offline status for a selected radio station by making a separate call to the Station Availability API.

While this workaround allows users to view the station list and check individual station availability, it does not provide a combined view of both data sets on a single screen. Improving the integration of the two APIs to display the station list with corresponding availability statuses in a paired format remains an outstanding issue.

Future Improvements

1) Implement Offline Mode
Enhance the application to support offline mode by leveraging the local data cache.
If the device is offline or the APIs are unavailable, the application should display the cached data instead of making API calls.
Implement appropriate visual indicators or messages to inform the user when the application is running in offline mode and displaying cached data.


2) Combine Radio Station and Availability Data
Improve the integration of the Radio Station API and Station Availability API to display the station list with corresponding availability statuses on a single screen.
Develop a mechanism to fetch and combine the data from both APIs efficiently, ensuring that the availability status is displayed alongside each radio station in the list.


