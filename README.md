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

The running application is available [here](http://tweetsampler.herokuapp.com/)

## How it works? ##

![Alt text](pics/TweetSampler_flow.jpg?raw=true "Flow")

1. Start provisioning AWS EBS application and environments with ***Ansible***

