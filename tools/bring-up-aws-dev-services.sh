aws rds start-db-instance \
--profile folauk110 \
--db-instance-identifier learnmymath-api-dev \
--output text \
--no-cli-pager

read -t 180 -p "postgres is starting up. waiting for 3 mins before starting api server."

aws ecs update-service --cluster learnmymath-api --service learnmymath-api-dev-service \
--task-definition learnmymath-api-dev-task \
--desired-count 1 \
--profile folauk110 \
--output text \
--no-cli-pager

read -t 0 -p "api server is starting up. starting graphql server..."

aws ecs update-service --cluster learnmymath-api --service learnmymath-graphql-dev-service \
--task-definition learnmymath-graphql-dev \
--desired-count 1 \
--profile folauk110 \
--output text \
--no-cli-pager


read -t 90 -p "graphql server is starting up."

echo "removing maintenance index file"
aws s3 rm s3://dev.learnmymath.io/index.html \
--profile folauk110 \
--output text \
--no-cli-pager
echo "maintenance index file has been removed"

read -t 3 -p "renaming old-index.html back to index.html"

aws s3 mv s3://dev.learnmymath.io/old-index.html s3://dev.learnmymath.io/index.html \
--profile folauk110 \
--output text \
--no-cli-pager
echo "old-index.html has been renamed back to index.html"

read -t 3 -p "invalidating cloudfront"

aws cloudfront create-invalidation --distribution-id=E3FP06IF5DA4AS \
--paths '/index.html' '/version.txt' \
--profile folauk110 \
--output text \
--no-cli-pager
echo "cloudfront restarted"

