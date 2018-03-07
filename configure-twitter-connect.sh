cat >./twitter-connect.json <<EOF
{
  "connector.class": "com.github.jcustenborder.kafka.connect.twitter.TwitterSourceConnector",
  "twitter.oauth.accessTokenSecret": "$TWITTER_ACCESS_TOKEN_SECRET",
  "process.deletes": "false",
  "filter.keywords": "kafka,scala,lightbend,python,java,c#",
  "kafka.status.topic": "tweets",
  "tasks.max": "1",
  "name": "TwitterSourceConnector",
  "twitter.oauth.consumerSecret": "$TWITTER_CONSUMER_SECRET",
  "twitter.oauth.accessToken": "$TWITTER_ACCESS_TOKEN",
  "twitter.oauth.consumerKey": "$TWITTER_CONSUMER_KEY"
}
EOF

curl -X PUT \
  -H "Content-Type: application/json" \
  --data @twitter-connect.json \
  http://localhost:8083/connectors/TwitterSourceConnector/config