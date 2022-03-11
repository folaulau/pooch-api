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

aws ecs update-service --cluster pooch-dev --service pooch-graphql-dev \
--task-definition pooch-graphql-dev \
--desired-count 1 \
--profile pooch \
--output text \
--no-cli-pager


read -t 90 -p "graphql server is starting up."