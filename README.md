RealTrack
=========

#### Screenshots
##### Project Screens
![All projects](http://i.imgur.com/KU5sylo.png)
![Add/Edit project - required fields](http://i.imgur.com/dRxFuUm.png)
![Add/Edit project - optional fields](http://i.imgur.com/bmMxuxy.png)
![Project details](http://i.imgur.com/70tOvJq.png)

##### Activities screens
![Add/Edit activity - required fields](http://i.imgur.com/hMiiwBh.png)
![Add/Edit activity - optional fields](http://i.imgur.com/ivi6rcG.png)
![Add/Edit activity - reminders](http://i.imgur.com/Acgw4fv.png)
![Activity Details](http://i.imgur.com/RXFz5GK.png)

##### Recording Participations screens
![Record participation - required fields](http://i.imgur.com/MtqfiYA.png)
![Record participation - optional fields](http://i.imgur.com/B7Ub8WO.png)
![Participation details](http://i.imgur.com/Rrz3UAZ.png)

#### App Features

* Streamlined and clean interface
* Daily/weekly reminders to record data for activities
* Quickly record participations without a reminder
* Spreadsheet reports emailed to user on demand
* Review and change participation details from within app
* Sign-in sheets with finger-drawn signatures for event participants
* PDF export of signatures that can be emailed as proof of participant involvement


#### Background
This is a project started at the [*Random Hacks of Kindness*](http://www.rhok.org/event/atlanta-ga-usa-1) (part of the [National Civic Day of Hacking](http://hackforchange.org/)) event at Atlanta, GA on 6/1-2/2013.

**RealTrack** is an Android smartphone application designed to facilitate day-to-day data collection by Peace Corps Volunteers (PCV) in the field. This app is based on an [idea](http://www.rhok.org/problems/realtrack-app) suggested by Julia Schulkers, who is a Peace Corps Volunteer in Thailand, for the [Peace Corps Innovation Challenge](innovationchallenge.peacecorps.gov).



#### Notes for Developers
**Prerequisites**

In addition to the Android SDK Platform (API 17 is targeted but you can use a more recent one), you will need

* `Android SDK Tools` found under `Tools` in the `SDK Manager` (e.g. `v 23.0.2`)
* `Android SDK Platform-tools` found under `Tools` in the `SDK Manager` (e.g. >= `v 20`)
* `Android SDK Build-tools` found under `Tools` in the `SDK Manager` (use `19.0.3` if you plan to use Travis as that is the most recent version they have as of 07/14/2014. Or else you can use a more recent one)
* `Gradle 1.11`

This repo will provide the following external libraries for you:

* `ActionBarSherlock`
* `GridLayout v7`
* `PagerSlidingTabStrip`
* `Support Library v4`
* `AChartEngine`

**Installation for development**

1. `temp$> git clone https://github.com/PeaceCorps/realtrack-android.git`. This will create the directory `temp/realtrack-android`.
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

6. If you have `gradle`, you can issue a build (or any other gradle task) from the RealTrack directory: `workspace/RealTrack>$ ./gradlew assembleDebug`

#### External Libraries Used
* ActionBarSherlock
* AChartEngine
* iText-G Android PDF
* PagerSlidingTabStrip
