echo "Removing old container and image..."

docker kill client
docker rm -f client
docker rmi -f push_notification_client

echo "Building new image..."
docker build . -t push_notification_client

echo "Running new container..."
docker run --env-file=.env --name client --detach push_notification_client
