#!/usr/bin/env bash

gcloud compute instance-templates create pubsub-producer-template --machine-type=n1-standard-1 \
--image-project=debian-cloud --image=debian-9 --region=us-central1 --metadata-from-file=startup-script=application_startup.sh