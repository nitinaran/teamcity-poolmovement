
 TeamCity plugin to do pool movement when the agent is idle for a configurable time
 
 This plugin will move the agent when the agent is in a different pool than the default one if it is idle for more then a configurable time.
 This feature can be enabled per agent via the agent configuration
 Additionally, if the build queue is paused the pool movement does not happen during that time. Once the queue is enabled again, it waits for 10 mins before actually starting to do any type of pool movement
