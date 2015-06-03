# Tweet Sampler [![Circle CI](https://circleci.com/gh/lachatak/tweetsampler/tree/master.svg?style=svg)](https://circleci.com/gh/lachatak/tweetsampler/tree/master) [![Coverage Status](https://coveralls.io/repos/lachatak/tweetsampler/badge.svg?branch=master)](https://coveralls.io/r/lachatak/tweetsampler?branch=master)

This project is part of the Dev Challenges series organised for Gamesys developers.
THe description can be found [here](CHALLENGE.md)

### Tool set ###
- [Play](https://www.playframework.com/) to host the tweet application
- [Akka](http://akka.io/) to have actor support
- [AngularJS](https://angularjs.org/) to increase the UI development speed
- [Bootstrap](http://getbootstrap.com/) to speed up ergonomical UI development
- [Heroku](https://www.heroku.com/) to be able to run our app in the cloud
- [Twitter](https://twitter.com/lachata_k) to collect business information and follow technologial trends
- [CircleCI](https://circleci.com/) to build and deploy the application

The running application is available [here](http://tweetsampler.herokuapp.com/)

## How it works? ##

![Alt text](pics/TweetSampler_flow.jpg?raw=true "Flow")

1. Open the application in browser. Index page will be called from the webserver
2. Play delegates the call to the **Index controller**. It gives back the index page
3. When the index page is initialised a **websocket channel** will be opened
4. The call goes to the **Index controller** which creates a new **UserActor** for the session. It will be responsible for accepting user messages, converting those messages and forwarding to processor actors
5. The **UserActor(( initialises a **TwitterFilterActor** for the session. It will be responsible for handling twitter streams and delegeting new tweets for further procession to statistics calculator actors
6. Two statistics calculator actors are initialised. One for the **hashTag calculations** and one for the **filter statictics**
7. User sends filter words he is interested in
8. Play immediately delegates the request to the proper actor based on the session
9. **TwitterFilterActor** configures the new search for the **twitter streams client**
10. If there is a new tweet for the search option a call back method will be called in the **TwitterFilterActor**
11. The new **tweet is pushed to the UI**
12. The new **tweet is pushed to the statistics calculators**
13. Statistics calculators **asynchronously** calculates the results
14. UI displays the new tweet
15. UI displays the **asynchronously pushed** statisctics calculation results
16. User **clicks on a tweet** to see detailed user profile
17. **User controller** processes the request and delegates it to the **twitter client** to query for user details
18. Response is **processed fromt twitter client and pushed to the UI**
19. UI shows the detailed user profile

## Continous delivery ##
When code change happens in the github repository it is automatically picked up by CircleCI. It runs all the tests and based on the outcome deployes the application to Heroku

## Screen shots ##
### Main page ###
![Alt text](pics/TweetSamplerUI1.png?raw=true "Flow")

### User profile ###
![Alt text](pics/TweetSamplerUI2.png?raw=true "Flow")
