# GoogleAuth-2FA
Web application with 2FA with the Google Authenticator App

# How to run
docker build -t googleAuth .

docker run -p 8080:8080 googleAuth

to access the application, navigate to http://localhost:8080 with your local browser.
The whole server-side application state is being kept in an in-memory database which is exposed via a GUI under http://localhost:8080/h2-console. login: sa, password: password. Make sure it looks like this.

![image](https://user-images.githubusercontent.com/58360529/175850082-453c1fe2-9060-4019-8d4f-f889a221cf8d.png)


Please keep in mind that the application uses an external web API to map IPs to geographic coordinates. Hence, it needs internet access. The API is nice but also rate limited, if the validation via the API fails, all successful authorization attempts will be assumed to have happened from latitude=longitude=0.
There is a switch that allows to use a randomizer instead that will existing but arbitrary coords instead.

docker run -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=MockedGeoLocation" googleAuth

The image can be run setting environment variables to customize the app's behaviour.

| Name of Environment variable  | Value(s) | Effect | Default |
| ------------- | ------------- | -----| --- 
| SPRING_PROFILES_ACTIVE  | MockedGeoLocation  | Switched from online API to translate IPs to Geolocation to a randomizer | not set |
| APP_TOKEN_LIFETIME  | any number  | lifetime of JWT tokens in secs | 600 |
| APP_MFA_STRATEGY | on/off/adaptive | Determines if a login requires 2FA | adaptive |
| APP_DEVICE_RETENTION_TIME | any number | time (in sec) we remember a used device before considering 2FA | 2592000 |
| APP_MAXIMUM_TRAVEL_SPEED | any number (in km/h) | the travel speed deemed possible. | 200 |

Example:

docker run -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=MockedGeoLocation" -e "APP_MFA_STRATEGY=on" googleAuth


#Known bugs:
- Setting APP_MFA_STRATEGY to anything else than the supported values will not result in an error but in erratic program behaviour.
- The JWT token does NOT use Signatures, therefore the JWT can be tampered with
- The user registration is not sanitizing the username and the password
- Displaying the QR code during registration sometimes failed when not done in its seperate window. This makes it necessare to move away from there manually.
- When moving from the registration form where username and password is provided to the QR, qn exception is thrown in the terminal. Unpretty but the app is fine.

#Missing features:
- the 2FA strategy does only support global configuration, not individual overrides.

#Remarks
- Passwords are un-hashed, un-salted to keep possibilities for testing by changing values in the in-memory db
- I ran out of time ... the case of adaptive MFA is insufficiently tested.

