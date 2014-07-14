###RealTrack
####Android solution to track Peace Corps Volunteer activities in the field
-----
[![Build Status](https://travis-ci.org/PeaceCorps/realtrack-android.svg?branch=master)](https://travis-ci.org/PeaceCorps/realtrack-android)

#### Screenshots
##### Project Screens
![All projects](screens/all_projects.png?raw=true All projects)
![Add/Edit project - required fields](screens/addproject_required.png?raw=true Add/Edit project - Required fields)
![Add/Edit project - optional fields](screens/addproject_optional.png?raw=true Add/Edit project - Optional fields)
![Project details](screens/project_details.png?raw=true Project details)

##### Activities screens
![Add/Edit activity - required fields](screens/addactivity_required.png?raw=true Add/Edit activity - Required fields)
![Add/Edit activity - optional fields](screens/addactivity_optional.png?raw=true Add/Edit activity - Optional fields)
![Add/Edit activity - reminders](screens/addactivity_reminders.png?raw=true Add/Edit activity - Reminders)
![Activity Details](screens/activity_details.png?raw=true Activity details)

##### Recording Participations screens
![Record participation - required fields](screens/recordparticipation_required.png?raw=true Record-participation - Required fields)
![Record participation - optional fields](screens/recordparticipation_optional.png?raw=true Record-participation - Optional fields)
![Participation details](screens/participation_details.png?raw=true Participation details)

##### Sign-in screens
![Sign-in - details](screens/signin_details.png?raw=true Sign-in - details)
![Sign-in - signature](screens/signin_signature.png?raw=true Sign-in - signature)

#### App Features

* Streamlined and clean interface
* Repeating or non-repeating activities
* Daily/weekly reminders to record data for repeating activities
* Quickly record participations for non-repeating activities
* Review and change participation details from within app
* Spreadsheet reports emailed to user on demand
* Sign-in sheets with finger-drawn signatures for event participants
* PDF export of signatures that can be emailed as proof of participant involvement
* **New: transfer reports to your laptop using Bluetooth**


#### Background
This is a project started at the [*Random Hacks of Kindness*](http://www.rhok.org/event/atlanta-ga-usa-1) (part of the [National Civic Day of Hacking](http://hackforchange.org/)) event at Atlanta, GA on 6/1-2/2013.

**RealTrack** is an Android smartphone application designed to facilitate day-to-day data collection by Peace Corps Volunteers (PCV) in the field. This app is based on an [idea](http://www.rhok.org/problems/realtrack-app) suggested by Julia Schulkers, who is a Peace Corps Volunteer in Thailand, for the [Peace Corps Innovation Challenge](innovationchallenge.peacecorps.gov).


#### Notes for Developers
**Prerequisites**

In addition to the Android SDK Platform (API 17 is targeted but you can use a more recent one), you will need

* `Android SDK Tools` found under `Tools` in the `SDK Manager` (e.g. `v 23.0.2`)
* `Android SDK Platform-tools` found under `Tools` in the `SDK Manager` (e.g. >= `v 20`)
* `Android SDK Build-tools` found under `Tools` in the `SDK Manager` (use `19.0.3` if you plan to use Travis as that is the most recent version they have as of 07/14/2014. Or else you can use a more recent one)
* `Gradle 1.11` (if you would like to use gradle to build the project)

This repo will provide the following external libraries for you:

* `ActionBarSherlock`
* `GridLayout v7`
* `PagerSlidingTabStrip`
* `Support Library v4`
* `AChartEngine`

**Installation for development**

1. Clone this repository into a temporary location: 
<pre>temp$> git clone https://github.com/PeaceCorps/realtrack-android.git</pre>
This will create the directory `temp/realtrack-android`.
2. Open Eclipse. File -> Import -> Existing Android Code Into Workspace. Put the path of the `temp/realtrack-android` directory created in step 1 into the `Root Folder` field of the Eclipse dialog.
3. Import the following four projects (you will see these names in the 'New Project Name' column of the 'Projects' field). Make sure the `Copy projects into workspace` checkbox is selected before you do this.:
 * `RealTrack`
 * `ActionBarSherlock`
 * `GridLayout`
 * `PagerSlidingTabStrip`

4. Thus, after step 3, the workspace directory layout should look like this:
 <pre>
workspace
    |___ ActionBarSherlock
    |___ GridLayout
    |___ PagerSlidingTabStrip
    |___ RealTrack
 </pre>
5. Double-check the following:
    * `ActionBarSherlock`, `GridLayout`, `PagerSlidingTabStrip` are set up as Library projects (check via right click on project name -> Properties -> Android -> isLibrary (should be checked))
    * `RealTrack` references each of those projects (check via right click on project name -> Properties -> Android -> Library (the three projects should show up as references))

6. If you have `gradle`, you can issue a build (or any other gradle task) from the RealTrack directory, e.g.:
<pre>workspace/RealTrack>$ ./gradlew assembleDebug</pre>
Note that `gradle` assumes you either have a `local.properties` file in your project directory or the environment variable `ANDROID_HOME` set. If you followed the 5 steps listed above, Eclipse would have generated the `local.properties` file for you. However, if you skip those steps, please make sure to either:
 * (recommended method) generate a `local.properties` file using the command:
 <pre>workspace/RealTrack>$ android update project -s -t android-17 -p .</pre> in the RealTrack project root. You can change the `android-17` part to target another platform version. OR
 * set the `ANDROID_HOME` variable to your android installation directory

#### External Libraries Used
* ActionBarSherlock
* AChartEngine
* iText-G Android PDF
* PagerSlidingTabStrip

