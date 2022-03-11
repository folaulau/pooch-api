aws rds start-db-instance \
--profile pooch \
--db-instance-identifier pooch-api-dev-db \
--output text \
--no-cli-pager

read -t 180 -p "postgres is starting up. waiting for 3 mins before starting api server."

aws ecs update-service --cluster pooch-dev --service pooch-api-dev \
--task-definition pooch-api-dev \
--desired-count 1 \
--profile pooch \
--output text \
--no-cli-pager

read -t 0 -p "api server is starting up. starting graphql server..."

aws ecs update-service --cluster pooch-api --service pooch-graphql-dev \
--task-definition pooch-graphql-dev \
--desired-count 1 \
--profile pooch \
--output text \
--no-cli-pager


read -t 90 -p "graphql server is starting up."

echo "removing maintenance index file"
aws s3 rm s3://dev.poochapp.net/index.html \
--profile pooch \
--output text \
--no-cli-pager
echo "maintenance index file has been removed"

read -t 3 -p "renaming old-index.html back to index.html"

aws s3 mv s3://dev.poochapp.com/old-index.html s3://dev.poochapp.com/index.html \
--profile pooch \
--output text \
--no-cli-pager
echo "old-index.html has been renamed back to index.html"

read -t 3 -p "invalidating cloudfront"

aws cloudfront create-invalidation --distribution-id=E3FP06IF5DA4AS \
--paths '/index.html' '/version.txt' \
--profile pooch \
--output text \
--no-cli-pager
echo "cloudfront restarted"

