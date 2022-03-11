aws ecs update-service --cluster learnmymath-api --service learnmymath-api-dev-service \
--task-definition learnmymath-api-dev-task \
--desired-count 0 \
--profile folauk110 \
--output text \
--no-cli-pager

read -t 1 -p "taking down api server."

aws ecs update-service --cluster learnmymath-api --service learnmymath-graphql-dev-service \
--task-definition learnmymath-graphql-dev \
--desired-count 0 \
--profile folauk110 \
--output text \
--no-cli-pager

read -t 1 -p "taking down graphql server."

aws rds stop-db-instance --profile folauk110 \
--db-instance-identifier learnmymath-api-dev \
--output text \
--no-cli-pager

read -t 1 -p "taking down postgres server."

echo "rename index.html to old-index.html"
aws s3 mv s3://dev.learnmymath.io/index.html s3://dev.learnmymath.io/old-index.html \
--profile folauk110 \
--output text \
--no-cli-pager
echo "index.html has been renamed to old-index.html"

read -t 3 -p "uploading maintenance index file"

aws s3 cp index.html s3://dev.learnmymath.io/index.html \
--content-type 'text/html' \
--cache-control 'no-store, max-age=0' \
--metadata-directive REPLACE \
--profile folauk110 \
--output text \
--no-cli-pager
echo "maintenance index file has been uploaded to s3"

read -t 3 -p "invalidating cloudfront"

aws cloudfront create-invalidation --distribution-id=E3FP06IF5DA4AS \
--paths '/index.html' '/version.txt' \
--profile folauk110 \
--output text \
--no-cli-pager
echo "cloudfront restarted"

