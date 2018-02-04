# porg

A simple AWS Lambda image compressor using [sksamuel](https://github.com/sksamuel/scrimage).

## Setting up your AWS lambda
There are three environment variables that required in your Lambda configuration:

1. `DEFAULT_BUCKET`: Default bucket name that this Lambda function can read and write.
2. `REGION`: The bucket's region.
3. `SLACK_INCOMING_WEBHOOK`: This is for debugger since I found AWS Cloudwatch is hard to track. I will make this optional in the future.

#### Recommend memory and runtime settings
Handler: `com.porg.Main::makeThumbnailFromEvent`
Memory: 512mb
Timeout: 30s

#### Generating fat .jar
```$scala
sbt> assembly
```
