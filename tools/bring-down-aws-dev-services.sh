aws ecs update-service --cluster pooch-api-dev --service pooch-api-dev \
--task-definition pooch-api-dev \
--desired-count 0 \
--profile pooch \
--output text \
--no-cli-pager

read -t 1 -p "taking down api server."

aws ecs update-service --cluster pooch-api-dev --service pooch-graphql-dev \
--task-definition pooch-graphql-dev \
--desired-count 0 \
--profile pooch \
--output text \
--no-cli-pager

read -t 1 -p "taking down graphql server."

aws rds stop-db-instance --profile pooch \
--db-instance-identifier pooch-api-dev-db \
--output text \
--no-cli-pager

read -t 1 -p "taking down postgres server."

echo "rename index.html to old-index.html"
aws s3 mv s3://dev.poochapp.com/index.html s3://dev.poochapp.com/old-index.html \
--profile pooch \
--output text \
--no-cli-pager
echo "index.html has been renamed to old-index.html"

read -t 3 -p "uploading maintenance index file"

aws s3 cp index.html s3://dev.poochapp.com/index.html \
--content-type 'text/html' \
--cache-control 'no-store, max-age=0' \
--metadata-directive REPLACE \
--profile pooch \
--output text \
--no-cli-pager
echo "maintenance index file has been uploaded to s3"

read -t 3 -p "invalidating cloudfront"

aws cloudfront create-invalidation --distribution-id=E3FP06IF5DA4AS \
--paths '/index.html' '/version.txt' \
--profile pooch \
--output text \
--no-cli-pager
echo "cloudfront restarted"

