# improving accessibility of eclipse rcp applications 
Proof of Concept Application and Accessible Module

Created in 2015 for the bachelor thesis of Christian Heimann, heig-vd, Switzerland.
- - - -
##Abstract
The goal was to create a simple way for eclipse rcp applications to become accessible for blind people. The main points focused on:
* JAWS screenreader output
* Structured keyboard navigation

The project started with research for techniques that are used to make JAWS speak, and the right way to navigate through rich applications. For eclipse RCP which uses SWT components for the GUI, many promising elements (e.g. Accessible-Object, Accessibility Phrase) could not act as complete as the documentation promised. Then an empty «Proof of Concept» application was built to use as a testbed. This application is contained in the project-folder ch.chrissharkman.accessibility.rcp.application.poc.

To keep these necessary accessible elements reusable for any kind of eclipse rcp application, making a module was the defined approach. So an Accessible Module was built apart.

The implementation of the module into the proof of concept showed that it can work. A second implementation into a much more complex application failed. A lack of respecting generic needs caused this failure. But also the existing GUI must be respect basic elements (e.g. use buttons as buttons, not labels as buttons). Improvements are still necessary to create _an easy way to accessibility_ for eclipse RCP applications. 

##eclipse plugin-projects
This repository contains three plugin-projects:
* .jars: necessary jar files to make work the proof of concept application
* .poc: the proof of concept RCP application plugin project
* .base: the accessible module plugin project

They can directly be imported into eclipse IDE. The proof of concept application can be launched from eclipse to test the functionality of the accessible module.

##Accessible Module
The functionality of the module match the goals:
* Set labels, tooltips and use of Accessible object getName method to create a correct screenreader output.
* Define an «escape landmark», a point which can always be accessed by pressing ESC.
* Structures the keyboard navigation into regions, whithin iterations are made when user is tabbing.

###XML file describes structure
An xml file describes the desired navigation structure. So can be fixed, which elements receive focus in which sequence. It also defines the navigation regions. In the same time the nodes can have attributes for the enhancement of screenreader output. When e.g. an accessibleName attribute is set, then he concerned widget will receive an accessibleListener which returns a getName value.

This file allows to define many accessibility aspects at one central place.

###ViewTree
With the xml file a viewTree object is created, which reflects the actual elements of the application that are described in the xml. This viewTree is the central point for the keyboard navigation and for a correct enhancement.

###Events
All events are handled with the eclipse event broker.

##Test yourself
Install a demo version of JAWS, if you haven't installed it yet, and start it. Then launch the proof of concept from eclipse IDE. As soon as the application is ready, you can start navigating with the keyboard:
* F6 moves focus to the next region/part
* Tab iterates (when part completely described in xml) in region
* Esc sets focus on global trimbar on the left side for the choice of the perspective

Listen to the output, and compare with the xml file. Change values and run it again.
And then, sending me a small feedback at ch[dot]sharkman[at]gmail[dot]com would really be appreciated.


> Much more Information about this project is available at http://tb.chrissharkman.ch
