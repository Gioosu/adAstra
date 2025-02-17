**adAstra**

**Componenti del gruppo:**

R. Pretali - 870452

G. L. Formisano - 885862

G. Moisè - 885986

A. Polizzi Andreeff - 886170

A. Vita - 887466

<br>
<br>

**What's adAstra?**

This application is designed for astronomy and space enthusiasts. It allows users to track the International Space Station (ISS), explore the NASA Picture of the Day, dive into an astronomical encyclopedia.

**FUNCTIONALITIES:**
- **ISS Tracking:** The International Space Station (ISS) is visible to the naked eye during the night and is continually monitored. Hence, it's possible to predict when it will pass over one's location. User could be provided with ISS timings based on his current location.
- **Picture of The Day:** Everyday we'll show you the NASA Picture of the Day with a description.
- **Astronomical Encyclopedia:** An encyclopedia containing information about constellations, planets, and other astronomical topics, each with a brief description.

**API:**
- [Where the ISS at](https://wheretheiss.at/w/developer)
- [NASA API](https://api.nasa.gov)
- [GoogleMaps API](https://developers.google.com/maps?hl=it)

**Palette:**
  - **Dark Theme:**
    - #121212 (Black)
    - #8B0000 (Crimson Red)
  - **Light Theme:**
    - #FFFFFF (White)
    - #001372 (Navy Blue)


**MAIN NAVBAR:**
- Encyclopedia: short descryption and an image.
- ISS: where it's, visibility, speed and more info.
- Homepage: NASA Picture of the Day with a desription.
- Profile: Preferences and account settings.


**MVVM PATTERN:**
Approaching the code may seems hard the first time you face it, so here is a simple Sequence Diagram that shows View, ViewModel and Model interactions. We used callbacks and LiveData to handle every asynchronous updates. 

on Model folder, you can find the essential Java classes that represent the business logic objects that will be shared through liveData, these objects are treated as Room database tables, so that they can also be saved and retrieved from the database.

On Data folder, you will find the most of the business logic, including API calls, callbacks definition, Room database and DAOs. Last but not least, here is defined all the business logic to handle these processes, such as local and remote calls for retrieving datas.

On UI folder there are two main parts: View and ViewModel. ViewModels have the duty to expose the business logic contained in Data and Model in a way that it is easily accessible to Views. This way you can maintain a good separation of duty.

Views don't contain any sort of business logic, they handle the UI components bindings and observes changes on liveData exposed by ViewModels.



**FUTURE UPDATES:**
- **List of Future Astronomical Events:** Display a list of upcoming astronomical events such as comets, meteor showers, eclipses, and more.
- **ISS Path Prediction:** Display an accurate path.


**CONTACT & SUPPORT**

For any questions or issues, feel free to contact us at adiutoriumadastra@gmail.com or open an issue on GitHub.


**ADDITIONAL NOTES**

Feel free to suggest improvements, report bugs, or request new features to help us enhance the app!

`42`
