RealTrack
=========

This is a project started at the [*Random Hacks of Kindness*](http://www.rhok.org/event/atlanta-ga-usa-1) (part of the [National Civic Day of Hacking](http://hackforchange.org/)) event at Atlanta, GA on 6/1-2/2013.

**RealTrack** is an Android smartphone application designed to facilitate day-to-day data collection by Peace Corps Volunteers (PCV) in the field. This app is based on an [idea](http://www.rhok.org/problems/realtrack-app) suggested by Julia Schulkers, who is a Peace Corps Volunteer in Thailand, for the [Peace Corps Innovation Challenge](innovationchallenge.peacecorps.gov).


#### Motivation
Essentially, the problem we're aiming to tackle is that PCVs currently use a 22-page long pen and paper-based "Activity and Outcome Tracking booklet" to keep track of their day-to-day activities in the field. Data collection is vital to

* gauging the effectiveness of volunteers
* gauging the effectiveness and usage of funding by various governmental entities for Peace Corps tasks
* engender a sense of accomplishment among the volunteers, who can look back at data collected after their term of service

Some examples of data collected by volunteers is

* the number of students taught in a class
* the number of local leaders contacted

The problem with tracking data on paper is that if the volunteer's tasks are outside or some distance from their homes (very common), and they forget to carry the paper with them, data for that day is lost. As it happens, this is a very common occurrence and volunteers often omit to record data meticulously, leading them to reconstruct data from memory (and thus creating inaccurate records).

Using a smartphone application to track data is a good idea because most volunteers own iOS or Android-based smartphones and carry them with themselves wherever they go. Smartphone apps can also remind you if you forget to make a note or record data on a given day. Smartphones also raise the possibility of making richer records e.g. by using the smartphone's camera to add images to the records being kept.

#### Features

* Streamlined interface
* Spreadsheet reports emailed to user on demand
* Daily/weekly reminders to record data for activities
* Quickly record participations without a reminder
* Review and change participation details from within the app
* Sign-in sheets for participants with a digital signature

#### Notes for Users
RealTrack conceptualizes a PCVs data tracking in terms of the following hierarchy (from high-level to low):

1. **Projects**: a project is the long-term engagement of a PCV at their post
2. **Activities**:
    * Activities are tasks that the PCV performs to meet their project objectives.
    * Activities usually recur weekly or daily (but can also be one-off).
    * Activities are oriented towards communities and are usually in support of an initiative such as malaria or AIDS.
    * An activity ranges over a user-defined time period.
    * An example of an activity is weekly study sessions for children at the village school from 4/1/2014 to 8/1/2014.
3. **Participation**:
    * A participation is a record of an actual engagement *on a certain date and time* undertaken as part of an activity.
    * Participations are used for tracking or evaluating a PCV's activities.
    * Participations usually have indicators associated with them that are used for tracking.  Indicators are dictated by the project requirements. The most common indicator is the number of individuals that participate on that day.
    *  An example of a participation is a study session that happened on 4/17/2014 as part of the aforementioned activity.

#### Notes for Developers
You will need the excellent [ActionBarSherlock](http://actionbarsherlock.com/download.html) library set up in your workspace to work on RealTrack. [Here](http://stackoverflow.com/a/15244538/611888) is a brief guide on setting it up if you're having problems.

You will also need an Android Support v7 GridLayout project in your workspace. [This](http://developer.android.com/tools/support-library/features.html#v7-gridlayout) page will give you details on where to find the source in the SDK as well as setting it up in Eclipse.

Note that both these projects should be set up as *library projects* in Eclipse.

#### Proposed features

* Create a sign-in sheet so PCVs can collect details on participants
* Recording meetings with local leaders
* Allow PCVs to tweet their accomplishments
* Ability to share images from phone's gallery
