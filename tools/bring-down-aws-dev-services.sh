aws ecs update-service --cluster pooch-dev --service pooch-api-dev \
--task-definition pooch-api-dev \
--desired-count 0 \
--profile pooch \
--output text \
--no-cli-pager

read -t 1 -p "taking down api server."

aws ecs update-service --cluster pooch-dev --service pooch-graphql-dev \
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

