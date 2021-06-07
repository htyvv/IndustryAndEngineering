import boto3
import datetime

client = boto3.client('personalize-events', region_name='us-east-1')

response = client.put_events(
    trackingId='<trackingId>',
    sessionId='1',
    userId='<userId>',
    eventList=[
        {
            'eventType': "Interaction",
            'properties': "{\"itemId\": \"<itemId>\"}",
            'sentAt': datetime.datetime.now().timestamp()
        },
    ]
)
