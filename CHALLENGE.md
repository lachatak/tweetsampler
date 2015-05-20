## Tap Twitter Stream ##
Twitter can be loved and hated, but one thing is obvious. It is an endless source of technical and business information.
Our current challenge is about how we could potentially build an application which uses Twitter to extract those invaluable business infos.

### The task ###
The task is to create a **web application** that, using **Twitter's API**, taps the **real-time stream of tweets** and filters for those which contains user provided words. **Each user has its own set of search criteria**.
The app should display both **the stream of tweets** and the **statistics of hashTags** extracted from the tweets containing the provided filter words in a moving window fashion.

Search for "poker tournament" should show every tweet which has either **poker** or **tournament** in the content text:
```
bla bla bla poker blab bla #Casino #Poker
bla blu blabal tournament blabala #Poker
```
Those 2 tweet should be displayed and they should be used to extract hasTag infos.
```
#Poker, 2
#Casino, 1
```

### The UI ###
There should be a simple **text field** where the user can define what tweets he is interested in. The default should be **Poker** which is sampled as the page is opened. There could be **multiple keywords**. When the user provides a new keyword the newly defined samples from Twitter should be displayed on the UI alongside with the hashTag statistics.
Update the statistics as you update tweets.
Elements of the UI:
- **text field** to be able to provide filter keywords
- filtered **english** tweets
- **hashTag statistics**. It is a simple list of hashTags and a number of occurrences extracted from the tweets.

The layout of the UX is up to you. Can be user friendly, funny, functional or just a dump page. Can use push or pull technologies. Use what ever you feel comfortable with and suitable for the purpose. Use your imagination!

#### hashTag stats ####
Display hashTags within a moving time frame. It means that the UI should show the most popular tags in the last 1 minute. It can be configurable. Update in every minute or when you have a new tweet to display.
```
#Poker, 5
#Casino, 3
#NYC, 2
```

### Mockup ###
![Alt text](TweetSampler.jpg?raw=true "Mockup")

### Delivery ###
As it should be a **running web application** I would recommend to pick a cloud provider where you could potentially **deploy your application** for DEMO purpose. The organisers definitelly don't want to run 100 different type of web applications locally. ;) It can be AWS, Heroku or any other cloud provider what you prefer.

### Hints ###
- Twitter provides Streaming APIs in many different languages. For general information about the Streaming API check https://dev.twitter.com/streaming/overview.
  For libraries check https://dev.twitter.com/overview/api/twitter-libraries.
- Twitter's Streaming API requires OAuth authentication. You need to
  - sign up for Twitter
  - create a Twitter application on https://apps.twitter.com
  to get OAuth credentials. Both are simple processes, taking approximately 1 minute each.
- Make sure that your credentials are not awailable for unauthorized person

### Optional task ###
If the user uses multiple filter keyword provide a statistics which shows how many tweet occured for each and every filter keyword in the predefined windowed interval (let say in the last 1 minute). If the user filteres like "poker tournament":
```
poker, 5
tournament, 2
```
Don't forget it is a moving window as well!!

### Potential rating ideas ###
- Best UX
- Most User friendly UX
- Performant solution
- Scalable solution
- Promissing technology
- Longest solution
- Shortest Solution
- Original Idea
- Exotic solution
- I just like it most!
- Definitely not for me!